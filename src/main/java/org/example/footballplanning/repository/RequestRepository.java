package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.RequestEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestEnt,String> {
}
