package com.example.demo.specifications;

import com.example.demo.models.Task;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TaskSpecifications {

    public static Specification<Task> withUserAndTitleFilter(UUID userId, String titleFilter) {
        return (root, query, criteriaBuilder) -> {
            // Проверяем, что userId не null
            if (userId == null) {
                throw new IllegalArgumentException("UserId не может быть null");
            }

            // Создаем предикат для фильтрации по пользователю
            Predicate userPredicate = criteriaBuilder.equal(root.get("user").get("id"), userId);

            // Если фильтр по заголовку не предоставлен, возвращаем предикат пользователя
            if (titleFilter == null || titleFilter.isEmpty()) {
                return userPredicate;
            }

            // Создаем предикат для фильтрации по заголовку
            Predicate titlePredicate = criteriaBuilder.like(root.get("title"), "%" + titleFilter + "%");

            // Комбинируем предикаты пользователя и заголовка
            return criteriaBuilder.and(userPredicate, titlePredicate);
        };
    }
}