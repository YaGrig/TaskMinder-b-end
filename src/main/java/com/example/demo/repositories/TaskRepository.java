package com.example.demo.repositories;


import com.example.demo.dto.TaskStatisticsDto;
import com.example.demo.models.Task;
import com.example.demo.models.TaskStatus;
import com.example.demo.models.User;
import jakarta.persistence.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    List<Task> findByStatus(TaskStatus status);
    @Query("SELECT MAX(t.ord) FROM Task t")
    Integer findMaxOrd();

    List<Task> findAllByOrderByOrdAsc();

    @Query("SELECT new com.example.demo.dto.TaskStatisticsDto(t.status, CAST(COUNT(t) AS integer)) " +
                "FROM Task t WHERE t.user.id = :userId " +
            "GROUP BY t.status")
    List<TaskStatisticsDto> findTaskStatistics(@Param("userId") UUID userId);


    @Query("SELECT t FROM Task t WHERE t.id = :taskId AND t.user.id = :userId")
    Optional<Task> findTaskByIdAndUserId(@Param("taskId") UUID taskId, @Param("userId") UUID userId);



    Page<Task> findAllByUser(Specification<Task> spec, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.project.id = :id")
    List<Task> findAllByProject(UUID id);
}