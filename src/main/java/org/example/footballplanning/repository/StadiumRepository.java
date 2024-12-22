package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.StadiumEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StadiumRepository extends JpaRepository<StadiumEnt,String> {

}
