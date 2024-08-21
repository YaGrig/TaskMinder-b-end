package com.example.demo.services;

import com.example.demo.models.Comment;
import com.example.demo.models.User;
import com.example.demo.repositories.CommentRepository;
import io.micrometer.observation.ObservationFilter;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment) {
            return commentRepository.save(comment);
    }

    public void deleteComment(UUID id) {
            commentRepository.deleteById(id);
    }

    public Optional<Comment> getCommentByIdAndUser(UUID id, UUID userId) {
        return commentRepository.findById(id)
                .filter(comment -> comment.getId().equals(userId));
    }

    public List<Comment> getCommentsByUser(UUID userId) {
        return commentRepository.findByUserId(userId);
    }

    public boolean isCommentOwner(UUID commentId, UUID userId) {
        return getCommentByIdAndUser(commentId, userId)
                .map(Comment::getUser)
                .map(User::getId)
                .stream().anyMatch(userId::equals); // Используйте anyMatch вместо filter и isPresent
    }

    public List<Comment>  getCommentByTaskId(UUID id) {
        return commentRepository.getByTaskId(id);
    }

    public Optional<Comment> findById(UUID id) {
        return commentRepository.findById(id);
    };
}
