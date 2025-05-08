package org.example.footballplanning.specifications;

import org.example.footballplanning.model.child.StadiumEnt;
import org.springframework.data.jpa.domain.Specification;

public class StadiumSpecifications {

    //Search for title
    public static Specification<StadiumEnt> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name != null && !name.isEmpty()) {
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }

    //Search for location
    public static Specification<StadiumEnt> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> {
            if (location != null && !location.isEmpty()) {
                return criteriaBuilder.like(root.get("location"), "%" + location + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }

    //Search fpr hourly rate
    public static Specification<StadiumEnt> hasHourlyRateBetween(Double minHourlyRate, Double maxHourlyRate) {
        return (root, query, criteriaBuilder) -> {
            if (minHourlyRate != null && maxHourlyRate != null) {
                return criteriaBuilder.between(root.get("hourlyRate"), minHourlyRate, maxHourlyRate);
            }
            return criteriaBuilder.conjunction();
        };
    }
}