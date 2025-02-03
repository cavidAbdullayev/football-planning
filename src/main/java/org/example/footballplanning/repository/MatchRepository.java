package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.MatchEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEnt,String> {
    List<MatchEnt>findAllByMatchDateIsBeforeAndState(LocalDateTime dateTime, Integer state);
}

