package org.example.footballplanning.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementResponseBean;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.example.footballplanning.bean.request.get.GetRequestResponseBean;
import org.example.footballplanning.bean.request.rollbackRequest.RollbackRequestRequestBean;
import org.example.footballplanning.bean.request.showReceivedRequests.ShowReceivedRequestsRequestBean;
import org.example.footballplanning.bean.request.showSentRequests.ShowSentRequestsRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestResponseBean;
import org.example.footballplanning.exception.customExceptions.*;
import org.example.footballplanning.helper.*;
import org.example.footballplanning.model.child.*;
import org.example.footballplanning.repository.*;
import org.example.footballplanning.service.RequestService;
import org.example.footballplanning.specifications.AnnouncementSpecifications;
import org.example.footballplanning.specifications.RequestSpecifications;
import org.example.footballplanning.util.GeneralUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.example.footballplanning.util.GeneralUtil.*;
import static org.example.footballplanning.staticData.GeneralStaticData.currentUserId;
import static org.example.footballplanning.util.ValidationUtil.checkPageSizeAndNumber;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    final RequestRepository requestRepository;
    final MatchServiceHelper matchServiceHelper;
    final AnnouncementRepository announcementRepository;
    final UserRepository userRepository;
    final AnnouncementServiceHelper announcementServiceHelper;
    final RequestServiceHelper requestServiceHelper;
    final MatchScheduleServiceHelper matchScheduleServiceHelper;
    final MatchRepository matchRepository;
    final UserServiceHelper userServiceHelper;
    final EmailServiceHelper emailServiceHelper;


    @Value("${app.announcement.min-hours-before-start-and-end}")
    long minHoursBeforeStartAndEnd;

    @SneakyThrows
    @Override
    @Transactional
    public RequestToAnnouncementResponseBean sendRequestToAnnouncement(RequestToAnnouncementRequestBean request) {
        validateFields(request);

        UserEnt from = userServiceHelper.getUserById(currentUserId);

        Specification<AnnouncementEnt> specification = Specification.where(
                        AnnouncementSpecifications.hasId(request.getAnnouncementId()))
                .and(AnnouncementSpecifications.hasState(1));

        AnnouncementEnt announcement = announcementRepository.findOne(specification)
                .orElseThrow(() -> new ObjectNotFoundException("Announcement not found!"));

        TeamEnt team = from.getTeam();

        //Check if user has a team
        if (team == null) {
            throw new TeamException("You do not have a team! Please create one first!");
        }

        //Prevent sending a request to own announcement
        if (announcementRepository.existsByIdAndContactUser_Id(request.getAnnouncementId(), announcement.getContactUser().getId())) {
            throw new SendRequestException("You cannot send a request to your own announcement!");
        }

        UserEnt to = announcement.getContactUser();

        List<RequestEnt> sentRequests = from.getSentRequests();

        LocalDateTime startDate = announcement.getStartDate();
        LocalDateTime endDate = announcement.getEndDate();

        // Check for conflicting announcements
        Optional<AnnouncementEnt> ownConflictAnnouncement = announcementRepository.existsConflictWithUserAnnouncement(from.getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));

        if (ownConflictAnnouncement.isPresent()) {
            throw new ConflictException("Your announcement conflicts with this one! Conflicting announcement:\n"
                    + announcementServiceHelper.mapFromAnnouncementResponseToJson(ownConflictAnnouncement.get()));
        }

        // Check if user has already sent a request to this announcement
        boolean crashSentRequests = sentRequests.stream()
                .anyMatch(req -> req.getAnnouncement()
                        .getId().equals(request.getAnnouncementId()) && req.getState() == 1);

        if (crashSentRequests) {
            throw new SendRequestException("You cannot send another request for the same announcement!");
        }

        // Check for conflicts with previously requested announcements
        Optional<RequestEnt> conflictRequest = requestRepository.existsConflictWithSentRequests(from.getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));

        if (conflictRequest.isPresent()) {
            throw new ConflictException("You have conflicting requests for the same time range! Conflicting announcement:\\n"
                    + announcementServiceHelper.mapFromAnnouncementResponseToJson(conflictRequest.get().getAnnouncement()));
        }

        //Check for conflicts with future matches
        Optional<MatchEnt> conflictMatch = matchRepository.getConflictingMatchByUser(from.getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));
        if (conflictMatch.isPresent()) {
            throw new ConflictException("This request conflicts with your future matches! Conflicting match:\n" +
                    matchServiceHelper.mapFromMatchResponseToJson(conflictMatch.get()));
        }

        //Create and save request entity
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
        requestServiceHelper.save(requestEnt);
        userRepository.saveAll(Arrays.asList(from, to));

        return createResponse(new RequestToAnnouncementResponseBean(), "Request sent successfully!");
    }

    @Override
    @Transactional
    public ReceiveRequestResponseBean receiveRequest(ReceiveRequestRequestBean request) {
        validateFields(request);

        UserEnt receiver = userServiceHelper.getUserById(currentUserId);

        String requestId = request.getRequestId();

        Specification<RequestEnt> specification = Specification.where(
                        RequestSpecifications.hasId(requestId))
                .and(RequestSpecifications.hasState(1));

        RequestEnt requestUser = requestServiceHelper.getById(specification);

        AnnouncementEnt announcementEnt = requestUser.getAnnouncement();

        if (announcementEnt.getState() == 0) {
            throw new SendRequestException("Your announcement just not active!");
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

        List<RequestEnt> updatedRequests = announcementEnt.getRequests().stream()
                .peek(requestEnt -> requestEnt.setState(0))
                .toList();

        emailServiceHelper.sendMatchPoster(match);

        requestServiceHelper.saveAll(updatedRequests);
        userRepository.saveAll(List.of(receiver, sender));
        announcementServiceHelper.saveOrUpdate(announcementEnt);
        matchServiceHelper.save(match);
        matchScheduleServiceHelper.save(matchSchedule);

        return createResponse(new ReceiveRequestResponseBean(), "Request received successfully!");
    }

    @Override
    public List<GetRequestResponseBean> showSentRequests(ShowSentRequestsRequestBean request) {
        userServiceHelper.getUserById(currentUserId);

        request.normalize();

        String date = request.getDate();
        String toUserId = request.getToUserId();
        Integer pageNumber = request.getPage();
        Integer size = request.getSize();

        checkPageSizeAndNumber(size, pageNumber);

        Specification<RequestEnt> requestSpecification = Specification
                .where(RequestSpecifications.hasUserId(currentUserId));

        if (date != null && !date.isEmpty()) {
            requestSpecification.and(RequestSpecifications.hasDate(strToDateTime(date)));
        }

        if (toUserId != null && !toUserId.isEmpty()) {
            requestSpecification.and(RequestSpecifications.hasToUserId(toUserId));
        }

        requestSpecification.and(RequestSpecifications.hasState(1));

        Pageable pageable = PageRequest.of(pageNumber, size);

        List<RequestEnt> requests = requestRepository.findAll(requestSpecification, pageable)
                .toList();

        return requestServiceHelper.getAllRequestsResponse(requests);
    }

    @Override
    public List<GetRequestResponseBean> showReceivedRequests(ShowReceivedRequestsRequestBean request) {
        userServiceHelper.getUserById(currentUserId);

        request.normalize();

        String date = request.getDate();
        String fromUserId = request.getFromUserId();
        Integer size = request.getSize();
        Integer page = request.getPage();

        checkPageSizeAndNumber(size, page);

        Specification<RequestEnt> specification = Specification.where(
                RequestSpecifications.hasUserId(currentUserId));

        if (date != null && !date.isEmpty()) {
            specification.and(RequestSpecifications.hasDate(strToDateTime(date)));
        }

        if (fromUserId != null && !fromUserId.isEmpty()) {
            specification.and(RequestSpecifications.hasFromUserId(fromUserId));
        }

        specification.and(RequestSpecifications.hasState(1));

        Pageable pageable = PageRequest.of(page, size);

        List<RequestEnt> requests = requestRepository.findAll(specification, pageable)
                .toList();

        return requestServiceHelper.getAllRequestsResponse(requests);

    }

    @Override
    public BaseResponseBean rollbackRequest(RollbackRequestRequestBean requestBean) {
        userServiceHelper.getUserById(currentUserId);

        if (GeneralUtil.isNullOrEmpty(requestBean.getRequestId())) {
            throw new ValidationException("Request ID is required!");
        }

        Specification<RequestEnt> specification = Specification.where(
                RequestSpecifications.hasUserId(currentUserId));

        specification.and(RequestSpecifications.hasId(requestBean.getRequestId()));

        specification.and(RequestSpecifications.hasState(1));

        RequestEnt request = requestRepository.findOne(specification).orElseThrow(() ->
                new ObjectNotFoundException("No active sent request found for given ID by id!"));

        request.setState(0);

        requestServiceHelper.save(request);

        return createResponse(new BaseResponseBean(), "Request rolled back successfully!");
    }
}