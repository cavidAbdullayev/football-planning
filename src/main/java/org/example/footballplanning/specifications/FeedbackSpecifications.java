package org.example.footballplanning.specifications;

import org.example.footballplanning.model.child.FeedbackEnt;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class FeedbackSpecifications {

    public static Specification<FeedbackEnt>afterDate(LocalDateTime date){
        return (root, query, criteriaBuilder) -> {
            if(date!=null){
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),date);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<FeedbackEnt>hasUserId(String userId){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"),userId);
    }
}
