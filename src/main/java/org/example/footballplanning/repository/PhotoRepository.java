package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.PhotoEnt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<PhotoEnt,String> {

}
