package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.UserEnt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEnt,String> {
    boolean existsByUsernameAndState(String username, Integer state);
    boolean existsByEmailAndState(String email, Integer state);
    boolean existsByPhoneNumberAndState(String phoneNumber, Integer state);
    Optional<UserEnt>findByUsernameOrEmailOrPhoneNumberAndState(String username,String email,String phoneNumber,Integer state);
    Optional<UserEnt>findByUsernameAndState(String username,Integer state);

}
