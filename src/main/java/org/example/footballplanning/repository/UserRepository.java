package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.UserEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEnt,String> {
    boolean existsByUsernameAndState(String username, Integer state);
    Optional<UserEnt>findByEmailAndPhoneNumberAndUsernameAndState(String email,String phoneNumber,String username,Integer state);
    boolean existsByEmailAndState(String email, Integer state);
    boolean existsByPhoneNumberAndState(String phoneNumber, Integer state);
    Optional<UserEnt>findByEmailAndState(String email,Integer state);
    Optional<UserEnt>findByUsernameAndState(String username,Integer state);
    List<UserEnt>findAllByDebtGreaterThan(Double debt);
    Optional<UserEnt>findByIdAndState(String id,Integer state);
    @Modifying
    @Query("update UserEnt u set u.state= :state where u.id= :userId")
    void updateUserState(@Param("userId") String userId, @Param("state") Integer state);
}
