package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.RequestEnt;
import org.example.footballplanning.model.child.StadiumEnt;
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
public interface RequestRepository extends JpaRepository<RequestEnt,String>, JpaSpecificationExecutor<RequestEnt> {
    @Query("update RequestEnt r set r.state = :state where r.from.id = :userId or r.to.id = :userId")
    @Modifying
    void updateRequestsStateBySenderOrReceiver(String userId, Integer state);
    @Query("select req " +
            "from RequestEnt req " +
            "where req.from.id = :userId " +
            "and req.state  = 1 " +
            "and not (:adjustedStartDate > req.announcement.startDate " +
            "or :adjustedEndDate < req.announcement.endDate) " +
            "order by req.announcement.startDate asc")
    Optional<RequestEnt> existsConflictWithSentRequests(@Param("userId")String userId,
                                                             @Param("adjustedStartDate") LocalDateTime adjustedStartDate,
                                                             @Param("adjustedEndDate") LocalDateTime adjustedEndDate);
    Optional<RequestEnt>findByIdAndState(String requestId,Integer state);

    @Query("select r " +
            "from RequestEnt r "+
            "where r.to.id = :userId " +
            "and r.state = :state")
    List<RequestEnt>findAllReceivedByUserIdAndState(@Param("userId") String userId,
                                            @Param("state") Integer state);

    @Query("select r " +
            "from RequestEnt r " +
            "where r.from.id = :userId " +
            "and r.state = :state")
    List<RequestEnt>findAllSentByUserIdAndState(@Param("userId") String userId,
                                                @Param("state") Integer state);

    @Query("select r " +
            "from RequestEnt r " +
            "where r.id = :id " +
            "and r.from.id = :userId " +
            "and r.state = :state")
    Optional<RequestEnt>findByIdAndUserIdAndState(@Param("id") String id,
                                                  @Param("userId") String userId,
                                                  @Param("state") Integer state);

}
