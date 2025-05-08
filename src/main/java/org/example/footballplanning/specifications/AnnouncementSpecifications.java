package org.example.footballplanning.specifications;

import org.example.footballplanning.model.child.AnnouncementEnt;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AnnouncementSpecifications {

    public static Specification<AnnouncementEnt>hasContactUserId(String userId){
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("contactUser").get("id"),userId);
    }

    public static Specification<AnnouncementEnt>hasState(Integer state){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"),state);
    }

    public static Specification<AnnouncementEnt>startDateAfter(LocalDateTime startDate){
        return (root, query, criteriaBuilder) -> {
            if(startDate!=null){
                criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"),startDate);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<AnnouncementEnt>hasId(String id){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"),id);
    }
}
