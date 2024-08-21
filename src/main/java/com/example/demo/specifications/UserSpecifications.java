package com.example.demo.specifications;

import com.example.demo.models.Task;
import com.example.demo.models.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class UserSpecifications {

    public static Specification<User> withFilter(String emailFilter) {
        return (root, query, criteriaBuilder) -> {

            if (emailFilter == null || emailFilter.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            // Create a predicate for title filtering
            return criteriaBuilder.like(root.get("email"), "%" + emailFilter + "%");
        };
    };
};