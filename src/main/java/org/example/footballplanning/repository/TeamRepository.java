package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.TeamEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamEnt,String> {
}
