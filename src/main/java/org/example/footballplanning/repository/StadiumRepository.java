package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.StadiumEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StadiumRepository extends JpaRepository<StadiumEnt,String> {
    Optional<StadiumEnt>findByName(String name);
    void deleteByName(String name);
    boolean existsByName(String name);
    Optional<StadiumEnt>findByIdAndState(String id,Integer state);

}
