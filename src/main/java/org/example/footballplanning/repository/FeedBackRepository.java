package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.FeedbackEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedBackRepository extends JpaRepository<FeedbackEnt,String> {

}
