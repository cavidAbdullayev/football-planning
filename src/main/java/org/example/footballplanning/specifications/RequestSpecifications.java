package org.example.footballplanning.specifications;

import org.example.footballplanning.model.child.RequestEnt;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class RequestSpecifications {

    public static Specification<RequestEnt> hasDate(LocalDateTime date) {

        return (root, query, criteriaBuilder) -> {
            if (date != null) {
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), date);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<RequestEnt> hasToUserId(String toUserId){
        return (root, query, criteriaBuilder) -> {
            if(toUserId !=null&&!toUserId.isEmpty()){
                criteriaBuilder.equal(root.get("to").get("id"), toUserId);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<RequestEnt> hasUserId(String userId){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("from"),userId);
    }

    public static Specification<RequestEnt>hasFromUserId(String fromUserId){
        return (root, query, criteriaBuilder) -> {
            if(fromUserId!=null&&fromUserId.isEmpty()){
                return criteriaBuilder.equal(root.get("from").get("id"),fromUserId);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<RequestEnt>hasState(Integer state){
        return (root, query, criteriaBuilder) -> {
            if(state!=null){
                return criteriaBuilder.equal(root.get("state"),state);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<RequestEnt>hasId(String id){
        return (root, query, criteriaBuilder) -> {
            if(id!=null&&!id.isEmpty()){
                return criteriaBuilder.equal(root.get("id"),id);
            }
            return criteriaBuilder.conjunction();
        };
    }

}
