package org.example.footballplanning.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.child.MatchEnt;
import org.example.footballplanning.repository.MatchRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CheckMatchScheduler {
    MatchRepository matchRepository;

    @Scheduled(cron = "*/3 * * * * ?")
    public void checkMatchState() {
        List<MatchEnt> matches = matchRepository.findAllByMatchDateIsBeforeAndState(LocalDateTime.now(), 1);
        matches.forEach(match -> match.setState(0));
        matchRepository.saveAll(matches);
    }
}