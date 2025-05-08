package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.child.MatchScheduleEnt;
import org.example.footballplanning.repository.MatchScheduleRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class MatchScheduleServiceHelper {
    MatchScheduleRepository matchScheduleRepository;

    @CachePut(value = "matchScheduleCache", key = "#matchSchedule.id")
    public MatchScheduleEnt save(MatchScheduleEnt matchSchedule) {
        return matchScheduleRepository.save(matchSchedule);
    }
}