package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.announcement.create.CreateAnnouncementRequestBean;
import org.example.footballplanning.bean.announcement.get.GetAnnouncementResponse;
import org.example.footballplanning.bean.match.get.GetMatchResponseBean;
import org.example.footballplanning.exception.customExceptions.*;
import org.example.footballplanning.model.child.*;
import org.example.footballplanning.repository.AnnouncementRepository;
import org.example.footballplanning.repository.MatchRepository;
import org.example.footballplanning.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.footballplanning.util.GeneralUtil.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AnnouncementServiceHelper {
    final MatchServiceHelper matchServiceHelper;
    final AnnouncementRepository announcementRepository;
    final RequestRepository requestRepository;
    final MatchRepository matchRepository;


    @Value("${app.announcement.min-hours-before-start-and-end}")
    long minHoursBeforeStartAndEnd;


    public String mapFromAnnouncementResponseToJson(AnnouncementEnt announcement) {
        return createJson(mapToGetResponse(announcement));
    }

    public GetAnnouncementResponse mapToGetResponse(AnnouncementEnt announcement) {
        return GetAnnouncementResponse.builder()
                .ID(announcement.getId())
                .title(announcement.getTitle())
                .endDate(dateTimeToStr(announcement.getEndDate()))
                .startDate(dateTimeToStr(announcement.getStartDate()))
                .costPerPlayer(announcement.getCostPerPlayer())
                .durationInMinutes(announcement.getDurationInMinutes())
                .playerCount(announcement.getPlayerCount())
                .build();
    }

    public List<GetAnnouncementResponse>mapToResponseAll(List<AnnouncementEnt> announcements){
        return announcements.stream()
                .map(this::mapToGetResponse)
                .toList();
    }

    public void validateUserForAnnouncement(UserEnt user, int playerCount) {
        if (playerCount < 4 || playerCount > 100) {
            throw new ValidationException("Your team must have between 4 and 11 players!");
        }

        if (user.getDebt() > 0) {
            throw new DebtException("You have unpaid debt ("+user.getDebt()+"AZN)! Pay it before creating an announcement.");
        }

        if (user.getTeam() == null) {
            throw new TeamException("You need to create a team before making an announcement!");
        }
    }

    public void validateAnnouncementTiming(UserEnt user, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isBefore(LocalDateTime.now().plusHours(minHoursBeforeStartAndEnd))) {
            throw new GameValidationException("Announcements must be created at least 6 hours in advance.");
        }
        Optional<AnnouncementEnt> conflictingAnnouncement = announcementRepository.existsConflictWithUserAnnouncement(user.getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));
        if (conflictingAnnouncement.isPresent()) {
            throw new ConflictException("You already have an active announcement in this interval! The announcement: \n " +
                    conflictingAnnouncement.get());
        }

        Optional<RequestEnt> conflictingRequest = requestRepository.existsConflictWithSentRequests(user.getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));
        if (conflictingRequest.isPresent()) {
            throw new ConflictException("You have a conflicting request in this interval! The announcement: \n " +
                    mapFields(new GetAnnouncementResponse(), conflictingRequest.get().getAnnouncement()));
        }
        Optional<MatchEnt> conflictingMatch = matchRepository.getConflictingMatchByUser(user.getId(), startDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));
        if (conflictingMatch.isPresent()) {
            throw new ConflictException("This announcement conflicts with one of your future matches! The match:\n" +
                    matchServiceHelper.mapFromMatchResponseToJson(conflictingMatch.get()));
        }
    }

    public void checkStadiumAvailability(StadiumEnt stadium, LocalDateTime starDate, LocalDateTime endDate) {
        Optional<AnnouncementEnt> conflictingMatch = announcementRepository.existsConflictWithStadium(stadium.getId(), starDate.minusHours(minHoursBeforeStartAndEnd), endDate.plusHours(minHoursBeforeStartAndEnd));
        if (conflictingMatch.isPresent()) {
            throw new ConflictException("This stadium is already booked during this time! The match:\n" +
                    mapFields(new GetMatchResponseBean(), conflictingMatch.get()));
        }
    }

    @CachePut(value = "announcementCache", key = "#announcement.id")
    public AnnouncementEnt saveOrUpdate(AnnouncementEnt announcement) {
        return announcementRepository.save(announcement);
    }

    @CacheEvict(value = "announcementCache", key = "#announcementId")
    public void delete(String announcementId) {
        announcementRepository.deleteById(announcementId);
    }

    @CacheEvict(value = "announcementCache", allEntries = true)
    public void clearCache() {
    }


    @Cacheable(value = "announcementCache",key = "#announcementId")
    public AnnouncementEnt findByIdAndStateAndContactUser(String announcementId, Integer state, UserEnt user) {
        return announcementRepository.findByIdAndStateAndContactUser(announcementId, state, user)
                .orElseThrow(() -> new ObjectNotFoundException("No active announcement found with the given ID!"));
    }
}