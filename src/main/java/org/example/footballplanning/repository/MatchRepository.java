package org.example.footballplanning.repository;

import org.example.footballplanning.model.child.MatchEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEnt,String> {
    List<MatchEnt>findAllByMatchDateIsBeforeAndState(LocalDateTime dateTime, Integer state);
    //TODO: update this to specifications
    @Query("select match " +
            "from MatchEnt match " +
            "where match.announcement.contactUser.id = :userId " +
            "and match.state = 1 " +
            "and not (:adjustedStartDate > match.announcement.endDate " +
            "or :adjustedEndDate < match.announcement.startDate) " +
            "order by match.announcement.startDate asc")
    Optional<MatchEnt>getConflictingMatchByUser(@Param("userId")String userId,
                                                   @Param("adjustedStartDate") LocalDateTime adjustedStartDate,
                                                   @Param("adjustedEndDate")LocalDateTime adjustedEndDate);
}

