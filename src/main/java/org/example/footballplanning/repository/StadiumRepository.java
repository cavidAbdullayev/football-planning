package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.StadiumEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StadiumRepository extends JpaRepository<StadiumEnt,String>, JpaSpecificationExecutor<StadiumEnt> {
    Optional<StadiumEnt>findByName(String name);
    void deleteByName(String name);
    boolean existsByName(String name);
    Optional<StadiumEnt>findByIdAndState(String id,Integer state);

}
