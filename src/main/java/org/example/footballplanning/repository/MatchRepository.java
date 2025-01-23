package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.MatchEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
@Repository
public interface MatchRepository extends JpaRepository<MatchEnt,String> {

    }

