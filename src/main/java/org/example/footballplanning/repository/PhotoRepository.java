package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.PhotoEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoEnt,String> {

}
