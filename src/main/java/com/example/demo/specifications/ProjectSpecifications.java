package com.example.demo.specifications;

import com.example.demo.models.Project;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ProjectSpecifications {

    public static Specification<Project> withUserAndTitleFilter(UUID userId, String titleFilter) {
        return (root, query, criteriaBuilder) -> {
            var userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);

            if (titleFilter == null || titleFilter.isEmpty()) {
                return userPredicate;
            }

            var titlePredicate = criteriaBuilder.like(root.get("title"), "%" + titleFilter + "%");

            return criteriaBuilder.and(userPredicate, titlePredicate);
        };
    }
    };