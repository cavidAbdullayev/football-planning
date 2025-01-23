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
//    @Query(value = """
//    SELECT COUNT(*) > 0
//    FROM AnnouncementEnt senderAnnouncement
//    WHERE senderAnnouncement.contactUser.id = :fromUserId
//      AND NOT (senderAnnouncement.endDate < :startDate OR senderAnnouncement.startDate > :endDate)
//    """)
//    boolean isCrashAnnouncement(@Param("fromUserId") String fromUserId,
//                                @Param("startDate") LocalDateTime startDate,
//                                @Param("endDate") LocalDateTime endDate);


    Optional<AnnouncementEnt>findByIdAndState(String id,Integer state);
    List<AnnouncementEnt>findAllByStadium_IdAndState(String id,Integer state);
}
