package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.TeamEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamEnt,String> {
boolean existsByTeamName(String teamName);
Optional<TeamEnt>findByTeamName(String teamName);
@Modifying
@Query("update TeamEnt t set t.state = :state where t.id = :teamId")
void updateTeamState(@Param("teamId") String teamId, @Param("state") Integer state);
}
