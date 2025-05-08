package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.MatchScheduleEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchScheduleRepository extends JpaRepository<MatchScheduleEnt,String> {

}
