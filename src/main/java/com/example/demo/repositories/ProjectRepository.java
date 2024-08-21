package com.example.demo.repositories;


import com.example.demo.dto.ProjectTaskStatisticsDTO;
import com.example.demo.models.Project;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE p.id = :projectId AND p.user.id = :userId")
    Optional<Project> findProjectByIdAndUserId(@Param("projectId") UUID projectId, @Param("userId") UUID userId);

    Page<Project> findAll(Specification<Project> spec, Pageable pageable);
    @Query("SELECT new com.example.demo.dto.ProjectTaskStatisticsDTO(p.title, COUNT(t.id)) " +
            "FROM Project p " +
            "JOIN p.members m " +
            "LEFT JOIN p.tasks t " + // Используйте LEFT JOIN, чтобы включить проекты без задач
            "WHERE m.id = :userId " +
            "GROUP BY p.id, p.title, p.description") // Убедитесь, что группировка включает все необходимые поля
    List<ProjectTaskStatisticsDTO> findProjectTaskStatistics(@Param("userId") UUID userId);

    List<Project> findByMembersContains(User user);
    // Other custom queries based on your requirements
}