package org.example.footballplanning.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import netscape.javascript.JSObject;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementResponseBean;
import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.request.RequestToAnnouncementResponseBean;
import org.example.footballplanning.bean.stadium.get.GetStadiumResponseBean;
import org.example.footballplanning.bean.user.getUser.GetUserResponseBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestRequestBean;
import org.example.footballplanning.bean.user.receiveRequest.ReceiveRequestResponseBean;
import org.example.footballplanning.helper.MatchServiceHelper;
import org.example.footballplanning.model.child.*;
import org.example.footballplanning.repository.*;
import org.example.footballplanning.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static org.example.footballplanning.helper.GeneralHelper.*;
import static org.example.footballplanning.staticData.GeneralStaticData.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    AnnouncementRepository announcementRepository;
    UserRepository userRepository;
    StadiumRepository stadiumRepository;
    RequestRepository requestRepository;
    MatchServiceHelper matchServiceHelper;
    MatchRepository matchRepository;
    @Override
    @Transactional
    public CreateAnnouncementResponseBean createAnnouncement(CreateAnnouncementRequestBean request) {
        CreateAnnouncementResponseBean response=new CreateAnnouncementResponseBean();

        validateFields(request);

        UserEnt user=userRepository.findByIdAndState(currentUserId,1).orElseThrow(()->new RuntimeException("User not found!"));
        Integer playerCount=request.getPlayerCount();
        if(user.getDebt()>0){
            throw new RuntimeException("Your has debt and you cannot create new announcement! Firstly, pay the your debt!");
        }

        TeamEnt team=user.getTeam();
        if(team==null){
            throw new RuntimeException("You have not any team! Firstly create team!");
        }

        Long durationInMinutes=request.getDurationInMinutes();
        String startDateStr =request.getStartDate();
        LocalDateTime startDate = strToDateTime(startDateStr);
        LocalDateTime endDate = startDate.plusMinutes(durationInMinutes);

        if (startDate.isBefore(LocalDateTime.now().plusHours(6))) {
            throw new RuntimeException("Announcements must be created at least 6 hours in advance than now.");
        }

        //check your current announcement crash with one of your last announcements
        List<RequestEnt>sentRequests=user.getSentRequests();
        boolean hasRequestConflictAnnouncement=sentRequests.stream().filter(requestEnt ->{
            AnnouncementEnt announcement=requestEnt.getAnnouncement();
               return announcement.getState()==1;
        })
                .anyMatch(requestEnt->{
                    AnnouncementEnt announcement=requestEnt.getAnnouncement();
            LocalDateTime startDateRequest=announcement.getStartDate();
            LocalDateTime endDateRequest=announcement.getEndDate();
            return !(startDateRequest.isAfter(endDate.plusHours(6))||endDateRequest.isBefore(startDate.minusHours(6)))&&announcement.getState()==1;
        });

        if(hasRequestConflictAnnouncement){
            throw new RuntimeException("You have request for match in this interval!");
        }

        //check if your announcement crash with any of your future matches
        boolean isConflictMatch=matchServiceHelper.checkIsConflict(user,startDate,endDate);
        if (isConflictMatch) {
            throw new RuntimeException("The requested announcement conflicts with your future matches!");
        }

        String stadiumId=request.getStadiumId();
        StadiumEnt stadium=stadiumRepository.findByIdAndState(stadiumId,1).orElseThrow(()->new RuntimeException("Stadium doesn't exists!"));

        //check if any of the announcements of stadium crash with your announcement
        List<AnnouncementEnt>stadiumAnnouncements=announcementRepository.findAllByStadium_IdAndState(stadiumId,1);
        boolean existsCrashingAnnouncement=stadiumAnnouncements.stream()
                .anyMatch(announcement-> startDate.isBefore(announcement.getEndDate().plusMinutes(30))||endDate.isAfter(announcement.getStartDate().minusMinutes(30)));

        if(existsCrashingAnnouncement){
            throw new RuntimeException("There are matches on this interval! Please, take another date!");
        }

        AnnouncementEnt announcement=mapFields(new AnnouncementEnt(),request);
        double totalCost=stadium.getHourlyRate()*(durationInMinutes.doubleValue()/60);
        double costPerPlayer=totalCost/(2*playerCount);

        announcement.setStartDate(startDate);
        announcement.setTeam(team);
        announcement.setCostPerPlayer(costPerPlayer);
        announcement.setContactUser(user);
        announcement.setEndDate(endDate);
        announcement.setStadium(stadium);

        response.setStartDate(startDateStr);
        GetUserResponseBean userResponse=new GetUserResponseBean();
        userResponse.setDateOfBirth(dateToStr(user.getDateOfBirth()));
        response.setContactUser(mapFields(userResponse,user));
        response.setStadium(mapFields(new GetStadiumResponseBean(),stadium));


        userRepository.save(user);
        announcementRepository.save(announcement);
        mapFields(response,announcement);
        return createResponse(response,"Announcement created!");
    }

    @SneakyThrows
    @Override
    @Transactional
    public RequestToAnnouncementResponseBean sendRequestToAnnouncement(RequestToAnnouncementRequestBean request) {
        RequestToAnnouncementResponseBean response=new RequestToAnnouncementResponseBean();

        validateFields(request);

        String fromUserId=request.getFromUserId();
        String announcementId=request.getAnnouncementId();
        UserEnt from=userRepository.findByIdAndState(fromUserId,1).orElseThrow(()->new RuntimeException("User not found!"));
        TeamEnt team=from.getTeam();

        //check has team or not
        if(team==null){
            throw new RuntimeException("You have not any team! Firstly create team!");
        }

        //check sent request is yourself or not
        List<AnnouncementEnt>myAnnouncements=from.getSharedAnnouncements();
        if(myAnnouncements.stream().anyMatch(announcementEnt -> announcementEnt.getId().equals(announcementId))){
            throw new RuntimeException("You cannot sent request yourself!");
        }

        AnnouncementEnt announcement=announcementRepository.findByIdAndState(announcementId,1).orElseThrow(()->new RuntimeException("Announcement not found!"));

        UserEnt to=announcement.getContactUser();

        List<RequestEnt>sentRequests=from.getSentRequests();

        LocalDateTime startDate=announcement.getStartDate();
        LocalDateTime endDate=announcement.getEndDate();

        //check have you ever sent request to this user for same announcement
        boolean sentAnyRequestEver=sentRequests.stream().anyMatch(requestEnt -> requestEnt.getAnnouncement().getId().equals(announcementId));
        if(sentAnyRequestEver){
            throw new RuntimeException("You cannot sent request again this user for same announcement!");
        }

        //check requested announcement if conflict with this announcement
        //todo: try return the crashing requests
        List<AnnouncementEnt>requestedAnnouncements=sentRequests.stream().map(RequestEnt::getAnnouncement).toList();
        AnnouncementEnt crashedAnnouncement= requestedAnnouncements.stream().filter(announcementEnt ->
                startDate.isAfter(announcementEnt.getEndDate().plusHours(6))||
                        endDate.isBefore(announcementEnt.getStartDate().minusHours(6))).findFirst().orElse(null);

        if(crashedAnnouncement!=null){
            GetAnnouncementResponse announcementResponse=mapFields(new GetAnnouncementResponse(),crashedAnnouncement);
            String startDateStr=dateTimeToStr(crashedAnnouncement.getStartDate());
            String endDateStr=dateTimeToStr(crashedAnnouncement.getEndDate());
            announcementResponse.setEndDate(endDateStr);
            announcementResponse.setStartDate(startDateStr);
            ObjectMapper announcementResponseJson=new ObjectMapper();
            announcementResponseJson.writeValueAsString(announcementId);
            throw new RuntimeException("You have some requests than crash with other requests for start date and end date! Crashed request:\n"+announcementResponseJson);
        }

        //check user's sent request future matches, if exists another game in this interval, get error

        boolean isConflict=matchServiceHelper.checkIsConflict(to,startDate,endDate);
        if (isConflict) {
            throw new RuntimeException("The requested announcement conflicts with your future matches!");
        }

        //create request entity
        RequestEnt requestEnt=RequestEnt.builder()
                .message(request.getMessage())
                .from(from)
                .to(to)
                .announcement(announcement)
                .team(team)
                .playerCount(request.getPlayerCount())
                .build();
        requestRepository.save(requestEnt);
        from.getSentRequests().add(requestEnt);
        to.getReceivedRequests().add(requestEnt);
        userRepository.saveAll(Arrays.asList(from,to));

        return createResponse(response,"Request sent successfully!");
    }

    @Override
    public ReceiveRequestResponseBean receiveRequest(ReceiveRequestRequestBean request) {
        ReceiveRequestResponseBean response=new ReceiveRequestResponseBean();

        validateFields(request);
        String requestId=request.getRequestId();

        UserEnt receiver =userRepository.findByIdAndState(currentUserId,1).orElseThrow(()->new RuntimeException("User not found!"));
        RequestEnt requestUser= receiver.getReceivedRequests().stream().filter(r->r.getId().equals(requestId))
                .findFirst().orElse(null);


        if(isNull(requestUser)){
            throw new RuntimeException("You have not request given by id!");
        }
        UserEnt sender=requestUser.getFrom();
        AnnouncementEnt announcementEnt=requestUser.getAnnouncement();

        if(announcementEnt.getState()==0){
            throw new RuntimeException("This announcement already expired!");
        }

        StadiumEnt stadium=announcementEnt.getStadium();
        Long durationInMinutes=announcementEnt.getDurationInMinutes();
        Double hourlyRate=stadium.getHourlyRate();
        double playerCountReceiver=announcementEnt.getPlayerCount();
        double playerCountSender=requestUser.getPlayerCount();
        double totalCost=hourlyRate*durationInMinutes/60;
        Double costPerPlayer=totalCost/(playerCountSender+playerCountReceiver);
        LocalDateTime matchDate=announcementEnt.getStartDate();
        TeamEnt teamA= receiver.getTeam();
        TeamEnt teamB=requestUser.getTeam();

        MatchEnt match=MatchEnt.builder()
                .durationInMinutes(durationInMinutes)
                .costPerPlayer(costPerPlayer)
                .matchDate(matchDate)
                .teamA(teamA)
                .teamB(teamB)
                .stadium(stadium)
                .build();

        announcementEnt.setState(0);
        receiver.setDebt(totalCost*playerCountReceiver/(playerCountSender+playerCountReceiver));
        sender.setDebt(totalCost*playerCountSender/(playerCountSender+playerCountReceiver));

        List<RequestEnt>requestsForAnnouncement=announcementEnt.getRequests();
        requestsForAnnouncement.forEach(requestForAnnouncement->requestForAnnouncement.setState(0));

        requestRepository.saveAll(requestsForAnnouncement);
        userRepository.saveAll(List.of(receiver,sender));
        announcementRepository.save(announcementEnt);
        matchRepository.save(match);

        return createResponse(response,"Request received successfully!");
    }

}
