package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementResponseBean;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;
import org.example.footballplanning.helper.AnnouncementServiceHelper;
import org.example.footballplanning.helper.MatchServiceHelper;
import org.example.footballplanning.helper.UserServiceHelper;
import org.example.footballplanning.model.child.*;
import org.example.footballplanning.repository.*;
import org.example.footballplanning.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.footballplanning.helper.GeneralHelper.*;
import static org.example.footballplanning.staticData.GeneralStaticData.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    AnnouncementRepository announcementRepository;
    UserRepository userRepository;
    StadiumRepository stadiumRepository;
    MatchServiceHelper matchServiceHelper;
    AnnouncementServiceHelper announcementServiceHelper;
    RequestRepository requestRepository;
    UserServiceHelper userServiceHelper;

    @Override
    @Transactional
    public CreateAnnouncementResponseBean createAnnouncement(CreateAnnouncementRequestBean request) {
        CreateAnnouncementResponseBean response = new CreateAnnouncementResponseBean();

        validateFields(request);

        UserEnt user = userRepository.findByIdAndState(currentUserId, 1).orElseThrow(() -> new RuntimeException("User not found!"));
        Integer playerCount = request.getPlayerCount();

        if (playerCount < 4) {
            throw new RuntimeException("Your team must have at least 4 players!");
        } else if (playerCount > 11) {
            throw new RuntimeException("Your team must have at most 12 players!");
        }
        if (user.getDebt() > 0) {
            throw new RuntimeException("Your has debt and you cannot create new announcement! Firstly, pay the your debt!");
        }

        TeamEnt team = user.getTeam();
        if (team == null) {
            throw new RuntimeException("You have not any team! Firstly create team!");
        }

        String title = request.getTitle();
        if (title.length() > 80) {
            throw new RuntimeException("Title length must be greater than 80!");
        }

        Long durationInMinutes = request.getDurationInMinutes();

        if (durationInMinutes < 60) {
            throw new RuntimeException("Your game must be equal or greater than 60 minutes!");
        }

        String startDateStr = request.getStartDate();
        LocalDateTime startDate = strToDateTime(startDateStr);
        LocalDateTime endDate = startDate.plusMinutes(durationInMinutes);

        if (startDate.isBefore(LocalDateTime.now().plusHours(6))) {
            throw new RuntimeException("Announcements must be created at least 6 hours in advance than now.");
        }

        //check if user has announcement bat this interval
        Optional<AnnouncementEnt> conflictAnnouncement = announcementServiceHelper.checkIsConflictWithAnnouncement(user.getSharedAnnouncements(), startDate, endDate);
        if (conflictAnnouncement.isPresent()) {
            throw new RuntimeException("You has already active announcement in this interval! The announcement: \n"
                    + announcementServiceHelper.mapFromAnnouncementResponseToJson(conflictAnnouncement.get()));
        }

        //check your current announcement crash with one of your requests
        List<RequestEnt> sentRequests = user.getSentRequests();
        Optional<RequestEnt> conflictRequest = sentRequests.stream().filter(requestEnt -> requestEnt.getAnnouncement().getState() == 1 && requestEnt.getState() == 1 &&
                        !(requestEnt.getAnnouncement().getStartDate().isAfter(endDate.plusHours(6)) || requestEnt.getAnnouncement().getEndDate().isBefore(startDate.minusHours(6))))
                .findFirst();

        if (conflictRequest.isPresent()) {
            throw new RuntimeException("You have request to announcement for match in this interval! The announcement: \n"
                    + announcementServiceHelper.mapFromAnnouncementResponseToJson(conflictRequest.get().getAnnouncement()));
        }

        //check if your announcement crash with any of your future matches
        List<MatchEnt> futureMatches = userServiceHelper.getFutureMatches(user);
        Optional<MatchEnt> conflictMatch = matchServiceHelper.checkIsConflictWithMatch(futureMatches, startDate, endDate);

        if (conflictMatch.isPresent()) {
            throw new RuntimeException("The announcement conflicts with one of your future matches! The match: \n"
                    + matchServiceHelper.mapFromMatchResponseToJson(conflictMatch.get()));
        }

        String stadiumId = request.getStadiumId();
        StadiumEnt stadium = stadiumRepository.findByIdAndState(stadiumId, 1).orElseThrow(() -> new RuntimeException("Stadium doesn't exists!"));

        //check if any of the announcements of stadium crash with your announcement
        List<AnnouncementEnt> matches = announcementRepository.findAllByStadium_IdAndState(stadiumId, 1);
        Optional<AnnouncementEnt> conflictAnnouncementStadium = announcementServiceHelper.checkIsConflictWithAnnouncement(matches, startDate, endDate);

        if (conflictAnnouncementStadium.isPresent()) {
            throw new RuntimeException("There are announcements on this interval at that stadium! Please, take another date or stadium! The announcement: \n"
                    + announcementServiceHelper.mapFromAnnouncementResponseToJson(conflictAnnouncementStadium.get()));
        }

        AnnouncementEnt announcement = mapFields(new AnnouncementEnt(), request);
        double totalCost = stadium.getHourlyRate() * (durationInMinutes.doubleValue() / 60);
        double costPerPlayer = totalCost / (2 * playerCount);

        announcement.setStartDate(startDate);
        announcement.setTeam(team);
        announcement.setCostPerPlayer(costPerPlayer);
        announcement.setContactUser(user);
        announcement.setEndDate(endDate);
        announcement.setStadium(stadium);

        response.setStartDate(startDateStr);
        GetUserResponseBean userResponse = new GetUserResponseBean();
        userResponse.setDateOfBirth(dateToStr(user.getDateOfBirth()));
        response.setContactUser(mapFields(userResponse, user));
        response.setStadium(mapFields(new GetStadiumResponseBean(), stadium));


        userRepository.save(user);
        announcementRepository.save(announcement);
        mapFields(response, announcement);
        return createResponse(response, "Announcement created!");
    }

    @Override
    public BaseResponseBean deactivateAnnouncement(String announcementId) {
        UserEnt user = userRepository.findByIdAndState(currentUserId, 1).orElseThrow(() ->
                new RuntimeException("User not found!"));

        AnnouncementEnt announcement = user.getSharedAnnouncements().stream().filter(announcementEnt -> announcementEnt.getId().equals(announcementId) && announcementEnt.getState() == 1)
                .findFirst().orElseThrow(() -> new RuntimeException("You has no active announcement given by id!"));

        announcement.setState(0);

        announcementRepository.save(announcement);

        return createResponse(new BaseResponseBean(), "Announcement already deactivated!");
    }

    @SneakyThrows
    @Override
    public UpdateAnnouncementResponseBean updateAnnouncement(UpdateAnnouncementRequestBean request) {
        UpdateAnnouncementResponseBean response = new UpdateAnnouncementResponseBean();

        UserEnt user = userRepository.findByIdAndState(currentUserId, 1).orElseThrow(() ->
                new RuntimeException("User not found!"));

        String announcementId = request.getAnnouncementId();

        if (isNullOrEmpty(announcementId)) {
            throw new RuntimeException("Announcement ID cannot be empty!");
        }

        AnnouncementEnt announcement = user.getSharedAnnouncements().stream().filter(announcementEnt ->
                        announcementEnt.getState() == 1 && announcementEnt.getId().equals(announcementId)).findFirst()
                .orElseThrow(() -> new RuntimeException("You have no any active announcement given by ID!"));

        Long durationInMinutes;

        if (request.getDurationInMinutes() != null && !request.getDurationInMinutes().equals(announcement.getDurationInMinutes())) {
            durationInMinutes = request.getDurationInMinutes();

            if (durationInMinutes < 60) {
                throw new RuntimeException("Your game period must be equal or greater than 60 minutes!");
            }

            announcement.setDurationInMinutes(durationInMinutes);
        }

        int playerCount;

        if (request.getPlayerCount() != null && !announcement.getPlayerCount().equals(request.getPlayerCount())) {
            playerCount = request.getPlayerCount();

            if (playerCount < 4) {
                throw new RuntimeException("Your team must have at least 4 players!");
            } else if (playerCount > 11) {
                throw new RuntimeException("Your team must have at most 12 players!");
            }

            double totalCost = announcement.getStadium().getHourlyRate() * (announcement.getDurationInMinutes().doubleValue() / 60);
            Double costPerPlayer = totalCost / (playerCount * 2);

            announcement.setPlayerCount(playerCount);
            announcement.setCostPerPlayer(costPerPlayer);
        }


        String title;
        if (!(isNullOrEmpty(request.getTitle()) && request.getTitle().equals(announcement.getTitle()))) {
            title = request.getTitle();

            if (title.length() > 80) {
                throw new RuntimeException("Title length must be greater than 80!");
            }

            announcement.setTitle(title);
        }


        LocalDateTime startDate;

        if (!(isNullOrEmpty(request.getStartDate()) && strToDateTime(request.getStartDate()).equals(announcement.getStartDate()))) {
            startDate = strToDateTime(request.getStartDate());

            if (startDate.isBefore(LocalDateTime.now().plusHours(6))) {
                throw new RuntimeException("Announcements must be created at least 6 hours in advance than now.");
            }

            LocalDateTime endDate = startDate.plusMinutes(announcement.getDurationInMinutes());

            Optional<RequestEnt> conflictRequest = user.getSentRequests().stream().filter(requestEnt -> requestEnt.getState() == 1 &&
                    !(startDate.isAfter(requestEnt.getAnnouncement().getEndDate().plusHours(6)) ||
                            endDate.isBefore(requestEnt.getAnnouncement().getEndDate().minusHours(6)))).findFirst();

            if (conflictRequest.isPresent()) {
                throw new RuntimeException("You have request to announcement for match in this interval! The announcement: \n"
                        + announcementServiceHelper.mapFromAnnouncementResponseToJson(announcement));
            }

            String stadiumId = request.getStadiumId() == null ? announcement.getStadium().getId() : request.getStadiumId();
            List<AnnouncementEnt> matches = announcementRepository.findAllByStadium_IdAndState(stadiumId, 1);

            StadiumEnt stadium = stadiumRepository.findByIdAndState(stadiumId, 1).orElseThrow(() ->
                    new RuntimeException("Stadium given by ID not found!"));

            Optional<AnnouncementEnt> conflictAnnouncementStadium = announcementServiceHelper.checkIsConflictWithAnnouncement(matches, startDate, endDate);

            if (conflictAnnouncementStadium.isPresent()) {
                throw new RuntimeException("There are announcements on this interval at that stadium! Please, take another date or stadium! The announcement: \n"
                        + announcementServiceHelper.mapFromAnnouncementResponseToJson(conflictAnnouncementStadium.get()));
            }

            announcement.setStadium(stadium);

            Optional<AnnouncementEnt> conflictAnnouncement = user.getSharedAnnouncements().stream().filter(announcementEnt ->
                    announcementEnt.getState().equals(1) && !(startDate.isAfter(announcement.getEndDate().plusHours(6))
                            || endDate.isBefore(announcement.getStartDate().minusHours(6)))).findFirst();

            if (conflictAnnouncement.isPresent()) {
                throw new RuntimeException("You have active announcement in this interval! The announcement: \n"
                        + announcementServiceHelper.mapFromAnnouncementResponseToJson(announcement));
            }

            List<MatchEnt> futureMatches = userServiceHelper.getFutureMatches(user);
            Optional<MatchEnt> conflictMatch = matchServiceHelper.checkIsConflictWithMatch(futureMatches, startDate, endDate);

            if (conflictMatch.isPresent()) {
                throw new RuntimeException("You have future match conflicts with this date! The match: \n"
                        + matchServiceHelper.mapFromMatchResponseToJson(conflictMatch.get()));
            }

            List<RequestEnt> requestsAnnouncement = announcement.getRequests();
            requestsAnnouncement.stream().filter(requestEnt -> requestEnt.getState() == 1).forEach(requestEnt -> requestEnt.setState(0));

            requestRepository.saveAll(requestsAnnouncement);
            announcement.setStartDate(startDate);
            announcement.setEndDate(startDate.plusMinutes(announcement.getDurationInMinutes()));
        }

        announcementRepository.save(announcement);
        GetAnnouncementResponse announcementResponse = announcementServiceHelper.mapToGetResponse(announcement);
        response.setAnnouncementResponse(announcementResponse);
        return createResponse(response, "Announcement updated successfully! Updated announcement:");
    }
}