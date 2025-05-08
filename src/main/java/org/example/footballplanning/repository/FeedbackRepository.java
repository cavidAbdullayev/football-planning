package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.FeedbackEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEnt,String>, JpaSpecificationExecutor<FeedbackEnt> {

}
