package com.example.demo.controllers;

import com.example.demo.config.JwtService;
import com.example.demo.models.Comment;
import com.example.demo.models.CommentInput;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.services.CommentService;

import com.example.demo.services.TaskService;
import com.example.demo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final UserService userService;
    private final TaskService taskService;

    @MutationMapping(value = "createComment")
    public Comment createComment(@Argument UUID taskId, @Argument Comment comment) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setTask(task);
        Comment savedComment = commentService.createComment(comment);
        return savedComment;
    }

    @MutationMapping(value = "updateComment")
    public Comment updateComment(@Argument UUID id, @Argument CommentInput commentInput) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        Optional<Comment> existingComment = commentService.findById(id);
        if (!existingComment.isPresent()) {
            return null;
        }
        Comment commentToUpdate = existingComment.get();
        commentToUpdate.setContent(commentInput.getContent());

        Comment updatedComment = commentService.updateComment(commentToUpdate);
        return updatedComment;
    }
    @MutationMapping(value = "deleteComment")
    public Void deleteComment(@Argument UUID id) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        commentService.deleteComment(id);
        return null;
    }

    @QueryMapping(value = "commentById")
    public ResponseEntity<Comment> getCommentById(@Argument UUID id) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        return commentService.getCommentByIdAndUser(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @QueryMapping(value = "commentsByTask")
    public List<Comment> getCommentsByTask(@Argument UUID id) {
        return commentService.getCommentByTaskId(id);
    }

    @QueryMapping(value = "allComments")
    public ResponseEntity<List<Comment>> getAllComments() {
        UUID userId = jwtService.getUserIdFromRequest(request);
        List<Comment> comments = commentService.getCommentsByUser(userId);
        return ResponseEntity.ok(comments);
    }
}
