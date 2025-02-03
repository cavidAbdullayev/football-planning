package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementResponseBean;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.request.get.GetRequestResponseBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestResponseBean;
import org.example.footballplanning.helper.AnnouncementServiceHelper;
import org.example.footballplanning.helper.MatchServiceHelper;
import org.example.footballplanning.helper.RequestServiceHelper;
import org.example.footballplanning.helper.UserServiceHelper;
import org.example.footballplanning.model.child.*;
import org.example.footballplanning.repository.*;
import org.example.footballplanning.service.RequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.example.footballplanning.helper.GeneralHelper.*;
import static org.example.footballplanning.staticData.GeneralStaticData.currentUserId;
@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    MatchServiceHelper matchServiceHelper;
    MatchRepository matchRepository;
    AnnouncementRepository announcementRepository;
    UserRepository userRepository;
    MatchScheduleRepository matchScheduleRepository;
    AnnouncementServiceHelper announcementServiceHelper;
    RequestServiceHelper requestServiceHelper;
    UserServiceHelper userServiceHelper;

    @SneakyThrows
    @Override
    @Transactional
    public RequestToAnnouncementResponseBean sendRequestToAnnouncement(RequestToAnnouncementRequestBean request) {
        RequestToAnnouncementResponseBean response = new RequestToAnnouncementResponseBean();

        validateFields(request);

        String announcementId = request.getAnnouncementId();

        UserEnt from = userRepository.findByIdAndState(request.getFromUserId(), 1).orElseThrow(() -> new RuntimeException("User not found!"));

        AnnouncementEnt announcement = announcementRepository.findByIdAndState(request.getAnnouncementId(), 1).orElseThrow(() -> new RuntimeException("Announcement not found!"));

        TeamEnt team = from.getTeam();

        //check has team or not
        if (team == null) {
            throw new RuntimeException("You have not any team! Firstly create team!");
        }

        //check sent request is yourself or not
        if (from.getSharedAnnouncements().stream().anyMatch(announcementEnt -> announcementEnt.getId().equals(announcementId))) {
            throw new RuntimeException("You cannot sent request yourself!");
        }

        UserEnt to = announcement.getContactUser();

        List<RequestEnt> sentRequests = from.getSentRequests();

        LocalDateTime startDate = announcement.getStartDate();
        LocalDateTime endDate = announcement.getEndDate();

        //check have you active announcement crash with this announcement
        List<AnnouncementEnt> announcements = from.getSharedAnnouncements();
        Optional<AnnouncementEnt> ownConflictAnnouncement = announcementServiceHelper.checkIsConflictWithAnnouncement(announcements, startDate, endDate);
        if (ownConflictAnnouncement.isPresent()) {
            throw new RuntimeException("Your has announcement crash with this announcement! The announcement: \n"
                    + announcementServiceHelper.mapFromAnnouncementResponseToJson(ownConflictAnnouncement.get()));
        }

        //check have you ever sent request to this user for same announcement
        boolean crashSentRequests = sentRequests.stream().anyMatch(requestEnt -> requestEnt.getAnnouncement().getId().equals(announcementId) && requestEnt.getState() == 1);
        if (crashSentRequests) {
            throw new RuntimeException("You cannot sent request again this user for same announcement!");
        }

        //check if one of your requested announcements conflict with this announcement
        List<AnnouncementEnt> requestedAnnouncements = sentRequests.stream().map(RequestEnt::getAnnouncement).toList();
        Optional<AnnouncementEnt> conflictAnnouncement = announcementServiceHelper.checkIsConflictWithAnnouncement(requestedAnnouncements, startDate, endDate);

        if (conflictAnnouncement.isPresent()) {
            throw new RuntimeException("You have some requests than crash with other requests for start date and end date! Crashed announcement:\n"
                    + announcementServiceHelper.mapFromAnnouncementResponseToJson(conflictAnnouncement.get()));
        }

        //check user's sent request future matches, if exists another game in this interval, get error
        //todo: check
        List<MatchEnt> futureMatches = userServiceHelper.getFutureMatches(from);
        Optional<MatchEnt> conflictMatch = matchServiceHelper.checkIsConflictWithMatch(futureMatches, startDate, endDate);
        if (conflictMatch.isPresent()) {
            throw new RuntimeException("The requested announcement conflicts with your future matches! The match: \n" +
                    matchServiceHelper.mapFromMatchResponseToJson(conflictMatch.get()));
        }

        //create request entity
        RequestEnt requestEnt = RequestEnt.builder()
                .message(request.getMessage())
                .from(from)
                .to(to)
                .announcement(announcement)
                .team(team)
                .playerCount(request.getPlayerCount())
                .build();

        from.getSentRequests().add(requestEnt);
        to.getReceivedRequests().add(requestEnt);
        requestRepository.save(requestEnt);
        userRepository.saveAll(Arrays.asList(from, to));

        return createResponse(response, "Request sent successfully!");
    }

    @Override
    public ReceiveRequestResponseBean receiveRequest(ReceiveRequestRequestBean request) {
        ReceiveRequestResponseBean response = new ReceiveRequestResponseBean();

        validateFields(request);
        String requestId = request.getRequestId();

        UserEnt receiver = userRepository.findByIdAndState(currentUserId, 1).orElseThrow(() -> new RuntimeException("User not found!"));

        RequestEnt requestUser = receiver.getReceivedRequests().stream().filter(r -> r.getId().equals(requestId) && r.getState() == 1)
                .findFirst().orElseThrow(() -> new RuntimeException("You have not received request given by id!"));

        AnnouncementEnt announcementEnt = requestUser.getAnnouncement();

        if (announcementEnt.getState() == 0) {
            throw new RuntimeException("Your announcement just not active!");
        }

        UserEnt sender = requestUser.getFrom();

        StadiumEnt stadium = announcementEnt.getStadium();
        Long durationInMinutes = announcementEnt.getDurationInMinutes();
        Double hourlyRate = stadium.getHourlyRate();
        double playerCountReceiver = announcementEnt.getPlayerCount();
        double playerCountSender = requestUser.getPlayerCount();
        double costPerPlayer = (hourlyRate * durationInMinutes / 60) / (playerCountSender + playerCountReceiver);

        MatchEnt match = MatchEnt.builder()
                .durationInMinutes(durationInMinutes)
                .costPerPlayer(costPerPlayer)
                .matchDate(announcementEnt.getStartDate())
                .teamA(receiver.getTeam())
                .teamB(requestUser.getTeam())
                .stadium(stadium)
                .announcement(announcementEnt)
                .build();

        MatchScheduleEnt matchSchedule = MatchScheduleEnt.builder()
                .match(match)
                .startDate(match.getMatchDate())
                .endDAte(match.getMatchDate().plusMinutes(durationInMinutes))
                .build();

        announcementEnt.setState(0);
        receiver.setDebt(costPerPlayer * playerCountReceiver);
        sender.setDebt(costPerPlayer * playerCountSender);
        requestUser.setState(0);

        requestRepository.saveAll(announcementEnt.getRequests());
        userRepository.saveAll(List.of(receiver, sender));
        announcementRepository.save(announcementEnt);
        matchRepository.save(match);
        matchScheduleRepository.save(matchSchedule);

        return createResponse(response, "Request received successfully!");
    }

    @Override
    public List<GetRequestResponseBean> showSentRequests() {
        UserEnt user = userRepository.findByIdAndState(currentUserId, 1).orElseThrow(() -> new RuntimeException("User not found!"));
        List<RequestEnt> requests = user.getSentRequests();
        List<GetRequestResponseBean> response = new ArrayList<>();

        requests.stream().filter(request -> request.getState() == 1).forEach(request ->
                response.add(requestServiceHelper.getRequestResponse(request)));

        return response;
    }

    @Override
    public List<GetRequestResponseBean> showReceivedRequests() {
        UserEnt user = userRepository.findByIdAndState(currentUserId, 1).orElseThrow(() -> new RuntimeException("User not found!"));
        List<RequestEnt> requests = user.getReceivedRequests();
        List<GetRequestResponseBean> response = new ArrayList<>();

        requests.stream().filter(request -> request.getState() == 1).forEach(request ->
                response.add(requestServiceHelper.getRequestResponse(request)));

        return response;
    }

    @Override
    public BaseResponseBean rollbackRequest(String requestId) {
        UserEnt user = userRepository.findByIdAndState(currentUserId, 1).orElseThrow(() ->
                new RuntimeException("User given by id not found!"));

        RequestEnt request = user.getSentRequests().stream().filter(requestEnt -> requestEnt.getId().equals(requestId) && requestEnt.getState() == 1).findFirst()
                .orElseThrow(() -> new RuntimeException("You have not any sent request given by id!"));

        request.setState(0);

        requestRepository.save(request);

        return createResponse(new BaseResponseBean(), "Request rolled back successfully!");

    }
}