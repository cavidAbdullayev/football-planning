package org.example.footballplanning.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.model.child.TeamEnt;
import org.example.footballplanning.repository.TeamRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TeamServiceHelper {
    TeamRepository teamRepository;

    @CachePut(value = "teamCache", key = "#result.id")
    public TeamEnt save(TeamEnt team) {
        return teamRepository.save(team);
    }

    @CacheEvict(value = "teamCache", key = "#teamId")
    public void deleteById(String teamId) {
        teamRepository.deleteById(teamId);
    }
}