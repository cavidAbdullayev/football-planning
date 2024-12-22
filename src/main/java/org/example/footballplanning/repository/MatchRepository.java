package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.MatchEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<MatchEnt,String> {
}
