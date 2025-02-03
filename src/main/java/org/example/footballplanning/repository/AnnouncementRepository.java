package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.AnnouncementEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEnt,String> {
    @Query(value = "SELECT CASE WHEN COUNT(ae) > 0 THEN true ELSE false END " +
            "FROM AnnouncementEnt ae " +
            "JOIN ae.stadium se " +
            "WHERE se.id=:stadiumId and :startDate <= ae.endDate AND :endDate >= ae.startDate ")
    boolean existsCrashingAnnouncementsAtStadium(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 @Param("stadiumId")String stadiumId);

    Optional<AnnouncementEnt>findByIdAndState(String id,Integer state);
    List<AnnouncementEnt>findAllByStadium_IdAndState(String stadiumId,Integer state);
    @Query("select a from AnnouncementEnt a left join fetch a.requests where (a.startDate < :now and a.state=1) or (a.startDate > :now and a.state=0)")
    List<AnnouncementEnt>findAllByStartDateIsBeforeAndActiveOrAfterAndDeactivated(@Param("now") LocalDateTime dateTime);
}
