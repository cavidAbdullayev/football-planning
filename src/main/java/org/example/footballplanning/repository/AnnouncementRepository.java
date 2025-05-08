package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.AnnouncementEnt;
import org.example.footballplanning.model.child.UserEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<AnnouncementEnt,String>, JpaSpecificationExecutor<AnnouncementEnt> {
    @Query(value = "SELECT CASE WHEN COUNT(ae) > 0 THEN true ELSE false END " +
            "FROM AnnouncementEnt ae " +
            "JOIN ae.stadium se " +
            "WHERE se.id=:stadiumId and :startDate <= ae.endDate AND :endDate >= ae.startDate ")
    boolean existsCrashingAnnouncementsAtStadium(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 @Param("stadiumId") String stadiumId);

    Optional<AnnouncementEnt> findByIdAndState(String id, Integer state);

    Optional<AnnouncementEnt> findByIdAndStateAndContactUser(String announcementId, Integer state, UserEnt user);

    List<AnnouncementEnt> findAllByStadium_IdAndState(String stadiumId, Integer state);

    @Query("select a from AnnouncementEnt a left join fetch a.requests where (a.startDate < :now and a.state=1) or (a.startDate > :now and a.state=0)")
    List<AnnouncementEnt> findAllByStartDateIsBeforeAndActiveOrAfterAndDeactivated(@Param("now") LocalDateTime dateTime);

    @Query("update AnnouncementEnt a set a.state = :state where a.contactUser.id = :userId")
    @Modifying
    void updateAnnouncementsStateByUser(@Param("userId") String userId, @Param("state") Integer state);

    boolean existsByContactUserAndStateAndStartDateBetween(UserEnt user, Integer state, LocalDateTime firstBound, LocalDateTime secondBound);

    @Query("select a " +
            "from AnnouncementEnt a " +
            "where a.contactUser.id = :userId " +
            "and a.state = 1 " +
            "and not (:adjustedStartDate > a.endDate " +
            "or :adjustedEndDate < a.startDate) " +
            "order by a.startDate asc")
    Optional<AnnouncementEnt> existsConflictWithUserAnnouncement(@Param("userId") String userId,
                                                                 @Param("adjustedStartDate") LocalDateTime adjustedStartDate,
                                                                 @Param("adjustedEndDate") LocalDateTime adjustedEndDate);

    @Query("select a " +
            "from AnnouncementEnt a " +
            "where a.stadium.id = :stadiumId " +
            "and a.state = 1 " +
            "and not (:adjustedStartDate > a.endDate " +
            "or :adjustedEndDate < a.startDate) " +
            "order by a.startDate asc")
    Optional<AnnouncementEnt> existsConflictWithStadium(@Param("stadiumId") String stadiumId,
                                                        @Param("adjustedStartDate") LocalDateTime adjustedStartDate,
                                                        @Param("adjustedEndDate") LocalDateTime adjustedEndDate);

    @Query("select a " +
            "from AnnouncementEnt a " +
            "where a.contactUser.id = :contactUserId " +
            "and a.state = :state")
    List<AnnouncementEnt> findAllByContactUserIdAndState(@Param("contactUserId") String contactUserId,
                                                         @Param("state") Integer state);

    @Query("select a " +
            "from AnnouncementEnt a " +
            "where a.id = :announcementId " +
            "and a.contactUser.id = :contactUserId " +
            "and a.state = :state")
    Optional<AnnouncementEnt> getAnnouncementEntByIdAndContactUserIdAndState(@Param("contactUserId") String contactUserId,
                                                                   @Param("announcementId") String announcementId,
                                                                   @Param("state") Integer state);

    boolean existsByIdAndContactUser_Id(String id,String contactUserId);
}