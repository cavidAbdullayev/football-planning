package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.activateOldAnnouncement.ActivateOldAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.activateOldAnnouncement.ActivateOldAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.deactivateAnnouncement.DeactivateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.deactivateAnnouncement.DeactivateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.bean.announcement.showMyAnnouncements.ShowMyAnnouncementsRequestBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.update.UpdateAnnouncementResponseBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;
import org.example.footballplanning.exception.customExceptions.ConflictException;
import org.example.footballplanning.exception.customExceptions.GameValidationException;
import org.example.footballplanning.exception.customExceptions.ObjectNotFoundException;
import org.example.footballplanning.exception.customExceptions.ValidationException;
import org.example.footballplanning.helper.AnnouncementServiceHelper;
import org.example.footballplanning.helper.MatchServiceHelper;
import org.example.footballplanning.helper.RequestServiceHelper;
import org.example.footballplanning.helper.UserServiceHelper;
import org.example.footballplanning.model.child.*;
import org.example.footballplanning.repository.AnnouncementRepository;
import org.example.footballplanning.repository.MatchRepository;
import org.example.footballplanning.repository.StadiumRepository;
import org.example.footballplanning.service.AnnouncementService;
import org.example.footballplanning.specifications.AnnouncementSpecifications;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.example.footballplanning.staticData.GeneralStaticData.currentUserId;
import static org.example.footballplanning.util.GeneralUtil.*;
import static org.example.footballplanning.util.ValidationUtil.checkPageSizeAndNumber;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    final AnnouncementRepository announcementRepository;
    final StadiumRepository stadiumRepository;
    final MatchServiceHelper matchServiceHelper;
    final AnnouncementServiceHelper announcementServiceHelper;
    final MatchRepository matchRepository;
    final RequestServiceHelper requestServiceHelper;
    final UserServiceHelper userServiceHelper;

    @Value("${app.announcement.min-hours-before-start-and-end}")
    long minHoursBeforeStartAndEnd;

    @Override
    @Transactional
    public CreateAnnouncementResponseBean createAnnouncement(CreateAnnouncementRequestBean request) {
        validateFields(request);

        UserEnt user = userServiceHelper.getUserById(currentUserId);
        announcementServiceHelper.validateUserForAnnouncement(user, request.getPlayerCount());

        if (request.getTitle().length() > 80) {
            throw new ValidationException("Title length must be at most 80 characters!");
        }

        // Parse and validate dates
        LocalDateTime startDate = strToDateTime(request.getStartDate());
        LocalDateTime endDate = startDate.plusMinutes(request.getDurationInMinutes());
        announcementServiceHelper.validateAnnouncementTiming(user, startDate, endDate);

        // Get stadium & check availability
        StadiumEnt stadium = stadiumRepository.findByIdAndState(request.getStadiumId(), 1)
                .orElseThrow(() -> new ObjectNotFoundException("Stadium doesn't exists!"));
        announcementServiceHelper.checkStadiumAvailability(stadium, startDate, endDate);

        // Create and save announcement
        TeamEnt team = user.getTeam();
        double totalCost = stadium.getHourlyRate() * (request.getDurationInMinutes() / 60.0);
        double costPerPlayer = totalCost / (2 * request.getPlayerCount());

        AnnouncementEnt announcement = mapFields(new AnnouncementEnt(), request);
        announcement.setStartDate(startDate);
        announcement.setEndDate(endDate);
        announcement.setTeam(team);
        announcement.setCostPerPlayer(costPerPlayer);
        announcement.setCostPerPlayer(costPerPlayer);
        announcement.setContactUser(user);
        announcement.setStadium(stadium);

        announcementServiceHelper.saveOrUpdate(announcement);

        CreateAnnouncementResponseBean response = new CreateAnnouncementResponseBean();
        response.setStartDate(response.getStartDate());
        response.setContactUser(mapFields(new GetUserResponseBean(), user));
        response.setStadium(mapFields(new GetStadiumResponseBean(), stadium));
        response.setID(announcement.getId());
        mapFields(response, announcement);

        return createResponse(response, "Announcement created successfully!");
    }

    @Override
    @Transactional
    public DeactivateAnnouncementResponseBean deactivateAnnouncement(DeactivateAnnouncementRequestBean request) {
        validateFields(request);

        String announcementId = request.getAnnouncementId();

        Specification<AnnouncementEnt> specification = Specification
                .where(AnnouncementSpecifications.hasId(request.getAnnouncementId()))
                .and(AnnouncementSpecifications.hasContactUserId(currentUserId))
                .and(AnnouncementSpecifications.hasState(1));

        AnnouncementEnt announcement = announcementServiceHelper.getByGivenSpecification(specification);

        announcement.setState(0);

        announcementServiceHelper.saveOrUpdate(announcement);

        DeactivateAnnouncementResponseBean response = new DeactivateAnnouncementResponseBean();
        response.setID(announcementId);

        return createResponse(new DeactivateAnnouncementResponseBean(), "Announcement already deactivated!");
    }

    @SneakyThrows
    @Override
    public UpdateAnnouncementResponseBean updateAnnouncement(UpdateAnnouncementRequestBean request) {
        UserEnt user = userServiceHelper.getUserById(currentUserId);

        String announcementId = request.getAnnouncementId();

        if (isNullOrEmpty(announcementId)) {
            throw new ValidationException("Announcement ID cannot be empty!");
        }

        Specification<AnnouncementEnt>specification=Specification.where(
                AnnouncementSpecifications.hasId(announcementId)
                        .and(AnnouncementSpecifications.hasContactUserId(currentUserId))
                        .and(AnnouncementSpecifications.hasState(1)));

        // Fetch the active announcement owned by the user
        AnnouncementEnt announcement = announcementRepository.findOne(specification)
                .orElseThrow(() -> new ObjectNotFoundException("No active announcement found with the given ID!"));

        // Update Duration
        if (request.getDurationInMinutes() != null && !request.getDurationInMinutes().equals(announcement.getDurationInMinutes())) {
            Long durationInMinutes = request.getDurationInMinutes();

            if (durationInMinutes < 60) {
                throw new GameValidationException("Your game period must be at least 60 minutes!");
            }

            announcement.setDurationInMinutes(durationInMinutes);
        }


        // Update Player Count
        if (request.getPlayerCount() != null && !announcement.getPlayerCount().equals(request.getPlayerCount())) {
            int playerCount = request.getPlayerCount();

            if (playerCount < 4 || playerCount > 11) {
                throw new GameValidationException("Your team must have between 4 and 11 players!");
            }

            double totalCost = announcement.getStadium().getHourlyRate() * (announcement.getDurationInMinutes().doubleValue() / 60);

            announcement.setPlayerCount(playerCount);
            announcement.setCostPerPlayer(totalCost / (playerCount * 2));
        }

        // Update Title
        if (!isNullOrEmpty(request.getTitle()) && !request.getTitle().equals(announcement.getTitle())) {

            if (request.getTitle().length() > 80) {
                throw new ValidationException("Title length must not exceed 80 characters!");
            }

            announcement.setTitle(request.getTitle());
        }

        // Update Start Date
        if (!(isNullOrEmpty(request.getStartDate()))) {
            LocalDateTime startDate = strToDateTime(request.getStartDate());

            if (startDate.isBefore(LocalDateTime.now().plusHours(minHoursBeforeStartAndEnd))) {
                throw new GameValidationException("Announcements must be at least 6 hours in advance.");
            }

            LocalDateTime endDate = startDate.plusMinutes(announcement.getDurationInMinutes());

            String stadiumId = request.getStadiumId() == null ? announcement.getStadium().getId() : request.getStadiumId();
            StadiumEnt stadium = stadiumRepository.findByIdAndState(stadiumId, 1)
                    .orElseThrow(() -> new ObjectNotFoundException("Stadium not found!"));

            // Check conflicts with active announcements
            boolean hasConflict = announcementRepository.existsByContactUserAndStateAndStartDateBetween(user, 1, startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));

            if (hasConflict) {
                throw new ConflictException("You have an active announcement in this interval!");
            }

            // Check conflicts with stadium
            announcementRepository.existsConflictWithStadium(stadiumId, startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd))
                    .ifPresent(announcementEnt -> {
                        throw new ConflictException("This stadium is already booked during this time! The match:\n" +
                                mapFields(new GetStadiumResponseBean(), announcementEnt));
                    });

            // Check conflicts with future matches
            matchRepository.getConflictingMatchByUser(user.getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd))
                    .ifPresent(match -> {
                        throw new ConflictException("You have a scheduled match conflicting with this date! The match:\n" +
                                matchServiceHelper.mapFromMatchResponseToJson(match));
                    });

            announcement.setStadium(stadium);
            announcement.setStartDate(startDate);
            announcement.setEndDate(startDate.plusMinutes(announcement.getDurationInMinutes()));

            List<RequestEnt> requestsAnnouncement = announcement.getRequests();
            requestsAnnouncement.stream()
                    .filter(requestEnt -> requestEnt.getState() == 1)
                    .forEach(requestEnt -> requestEnt.setState(0));

            requestsAnnouncement.forEach(requestServiceHelper::save);
        }

        announcementServiceHelper.saveOrUpdate(announcement);

        GetAnnouncementResponse announcementResponse = announcementServiceHelper.mapToGetResponse(announcement);

        UpdateAnnouncementResponseBean response = new UpdateAnnouncementResponseBean();
        response.setAnnouncementResponse(announcementResponse);

        return createResponse(response, "Announcement updated successfully! Updated announcement:");
    }

    @Override
    public ActivateOldAnnouncementResponseBean activateOldAnnouncement(ActivateOldAnnouncementRequestBean request) {
        UserEnt user = userServiceHelper.getUserById(currentUserId);

        validateFields(request);

        String announcementId = request.getAnnouncementId();

        AnnouncementEnt announcement = announcementRepository
                .getAnnouncementEntByIdAndContactUserIdAndState(currentUserId, announcementId, 0)
                .orElseThrow(() -> new ConflictException("You have not any non-active announcement with ID: " + announcementId));

        LocalDateTime startDate = strToDateTime(request.getStartDate());
        LocalDateTime endDate = startDate.plusMinutes(request.getDurationInMinutes());

        if (startDate.isBefore(LocalDateTime.now().plusHours(minHoursBeforeStartAndEnd))) {
            throw new GameValidationException("Announcements must be created at least 6 hours in advance.");
        }

        // Check conflict with user's own active announcements
        boolean hasConflict = announcementRepository.existsByContactUserAndStateAndStartDateBetween(
                user, 1, startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));

        if (hasConflict) {
            throw new ConflictException("You have an active announcement in this interval!");
        }

        // Check conflict with stadium bookings
        announcementRepository.existsConflictWithStadium(
                announcement.getStadium().getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd)
        ).ifPresent(conflicting -> {
            throw new ConflictException("This stadium is already booked during this time! The match:\n" +
                    mapFields(new GetStadiumResponseBean(), conflicting));
        });

        // Check conflict with scheduled matches
        matchRepository.getConflictingMatchByUser(
                user.getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd)
        ).ifPresent(conflict -> {
            throw new ConflictException("You have a scheduled match conflicting with this date! The match:\n" +
                    matchServiceHelper.mapFromMatchResponseToJson(conflict));
        });

        // Activate announcement
        announcement.setStartDate(startDate);
        announcement.setEndDate(endDate);
        announcement.setState(1);

        announcementServiceHelper.saveOrUpdate(announcement);

        // Build response
        return ActivateOldAnnouncementResponseBean.builder()
                .announcementResponse(announcementServiceHelper.mapToGetResponse(announcement))
                .message("Your announcement activated!")
                .build();
    }

    @Override
    public List<GetAnnouncementResponse> showMyAnnouncements(ShowMyAnnouncementsRequestBean request) {

        request.normalize();
        Integer state = request.getState();
        Integer size = request.getSize();
        Integer page = request.getPage();
        String startDate = request.getStartDate();


        if (!(state == 1 || state == 0)) {
            throw new ValidationException("Invalid state!");
        }

        checkPageSizeAndNumber(size, page);

        Pageable pageable = PageRequest.of(page, size);

        Specification<AnnouncementEnt> specification = Specification
                .where(AnnouncementSpecifications.hasId(currentUserId))
                .and(AnnouncementSpecifications.hasState(state));

        if (startDate != null) {
            specification.and(AnnouncementSpecifications.startDateAfter(strToDateTime(startDate)));
        }

        List<AnnouncementEnt> announcements = announcementRepository.findAll(specification, pageable)
                .toList();

        return announcements.stream()
                .map(announcement -> {
                    GetAnnouncementResponse announcementResponse = new GetAnnouncementResponse();
                    mapFields(announcementResponse, announcement);
                    return announcementResponse;
                }).toList();
    }
}