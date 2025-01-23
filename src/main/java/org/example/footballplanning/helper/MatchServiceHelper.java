package org.example.footballplanning.helper;

import org.example.footballplanning.model.child.MatchEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MatchServiceHelper {
    public boolean checkIsConflict(UserEnt user,LocalDateTime startDate,LocalDateTime endDate){
        List<MatchEnt> futureMatches=user.getFutureMatches();
        return futureMatches.stream().anyMatch(match->{
            LocalDateTime startDateFutureMatch=match.getMatchDate();
            LocalDateTime endDateFutureMatch=startDateFutureMatch.plusMinutes(match.getDurationInMinutes());
            return !(endDateFutureMatch.isBefore(startDate.plusHours(6))||startDateFutureMatch.isAfter(endDate.minusHours(6)));
        });
    }
}
