package com.example.demo.specifications;

import com.example.demo.models.Project;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ProjectSpecifications {

    public static Specification<Project> withUserAndTitleFilter(UUID userId, String titleFilter) {
        return (root, query, criteriaBuilder) -> {
            // Start with a predicate for user ID filtering
            var userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);

            // If title filter is not provided, return the user predicate only
            if (titleFilter == null || titleFilter.isEmpty()) {
                return userPredicate;
            }

            // Create a predicate for title filtering
            var titlePredicate = criteriaBuilder.like(root.get("title"), "%" + titleFilter + "%");

            // Combine user and title predicates using 'and'
            return criteriaBuilder.and(userPredicate, titlePredicate);
        };
    }
    };