package com.example.demo.specifications;

import com.example.demo.models.Task;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TaskSpecifications {

    public static Specification<Task> withUserAndTitleFilter(UUID userId, String titleFilter) {
        return (root, query, criteriaBuilder) -> {

            if (userId == null) {
                throw new IllegalArgumentException("UserId не может быть null");
            }

            Predicate userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);

            if (titleFilter == null || titleFilter.isEmpty()) {
                return userPredicate;
            }

            Predicate titlePredicate = criteriaBuilder.like(root.get("title"), "%" + titleFilter + "%");

            return criteriaBuilder.and(userPredicate, titlePredicate);
        };
    }
}