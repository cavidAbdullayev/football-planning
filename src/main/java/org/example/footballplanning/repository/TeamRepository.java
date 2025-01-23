package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.TeamEnt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamEnt,String> {
boolean existsByTeamName(String teamName);
Optional<TeamEnt>findByTeamName(String teamName);
}
