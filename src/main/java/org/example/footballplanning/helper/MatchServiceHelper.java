package org.example.footballplanning.helper;

import org.example.footballplanning.bean.match.get.GetMatchResponse;
import org.example.footballplanning.model.child.MatchEnt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.footballplanning.helper.GeneralHelper.*;

@Component
public class MatchServiceHelper {
    public Optional<MatchEnt> checkIsConflictWithMatch(List<MatchEnt> matches, LocalDateTime startDate, LocalDateTime endDate) {
        return matches.stream().filter(match -> {
            LocalDateTime matchStartDate = match.getMatchDate();
            LocalDateTime matchEndDate = match.getMatchDate().plusMinutes(match.getDurationInMinutes());
            return match.getState() == 1 && !(startDate.isAfter(matchEndDate.plusHours(6)) || endDate.isBefore(matchStartDate.minusHours(6)));
        }).findFirst();
    }

    public String mapFromMatchResponseToJson(MatchEnt match) {
        GetMatchResponse matchResponse = GetMatchResponse.builder()
                .matchDate(dateTimeToStr(match.getMatchDate()))
                .durationInMinutes(match.getDurationInMinutes())
                .costPerPlayer(match.getCostPerPlayer())
                .announcementId(match.getAnnouncement().getId())
                .build();
        return createJson(matchResponse);
    }
}