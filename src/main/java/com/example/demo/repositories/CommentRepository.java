    package com.example.demo.repositories;
    
    
    import com.example.demo.models.Comment;
    import com.example.demo.models.Task;
    import com.example.demo.models.User;
    import io.micrometer.observation.ObservationFilter;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;
    
    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;

    public interface CommentRepository extends JpaRepository<Comment, UUID> {
        List<Comment> findByTask(Task task);

        List<Comment> findByUser(User user);

        List<Comment> findByUserId(UUID userId);

        Optional<Comment> findByIdAndUser(UUID id, User user);

        @Query("SELECT c FROM Comment c WHERE c.task.id = :taskId")
        List<Comment>  getByTaskId(@Param("taskId") UUID id);
        // Other custom queries based on your requirements
    }
