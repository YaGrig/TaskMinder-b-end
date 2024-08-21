package com.example.demo.controllers;

import com.example.demo.config.JwtService;
import com.example.demo.dto.TaskStatisticsDto;
import com.example.demo.models.*;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.services.ProjectService;
import com.example.demo.services.TaskService;
import com.example.demo.services.UserService;
import com.example.demo.specifications.TaskSpecifications;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.*;

@Controller
@RequestMapping("/api")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final UserService userService;
    private final ProjectService projectService;

    @MutationMapping(value = "createTask")
    public void createTask(@Argument UUID projectId,@Argument TaskInput task) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        Optional<User> userOptional = userService.getUserById(userId);
        Optional<User> userAssigned = userService.getUserByEmail(task.getAssignedUserEmail());
        User user = userOptional.get();
        Optional<Project> project = projectService.getProjectById(projectId);
        userService.getUserByEmail(task.getAssignedUserEmail());
        Task newTask = taskService.createTask(Task.builder().assignedUserId(userAssigned.get()).user(user).priority(task.getPriority()).project(project.get()).status(task.getStatus()).type(task.getType()).title(task.getTitle()).description(task.getDescription()).dueDate(task.getDueDate()).build());
        ResponseEntity.ok(newTask);
    }

    @MutationMapping(value = "updateTask")
    public Task updateTask(@Argument UUID id , @Argument TaskInput input) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        if (userId != null) {
            Task updatedTask = taskService.updateTask(id, userId,Task.builder().status(input.getStatus()).ord(input.getOrd()).description(input.getDescription()).title(input.getTitle()).priority(input.getPriority()).dueDate(input.getDueDate()).build());
            return null;
        } else {
            return null;
        }
    }

    @MutationMapping(value = "deleteTask")
    public ResponseEntity<Void> deleteTask(@Argument UUID id) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        if (userId != null && taskService.isTaskOwner(id, userId)) {
            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @QueryMapping(value = "taskById")
    public Optional<Task> getTaskById(@Argument UUID id) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        if (userId != null) {
            Optional<Task> task = taskService.getTaskByIdAndUserId(id, userId);
            return task;
        }
        return null;
    }

    @MutationMapping(value = "reorderTask")
    public Task reorderTask(@Argument UUID taskId, @Argument int newOrd, @Argument String newStatus ) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        if (userId == null) {
            return null;
        }

        Optional<Task> taskOptional = taskService.getTaskByIdAndUserId(taskId, userId);
        if (!taskOptional.isPresent()) {
            return null;
        }

        Task taskToMove = taskOptional.get();
        if(!Objects.equals(taskToMove.getStatus().toString(), newStatus)){
            this.updateTask(taskId, TaskInput.builder().status(TaskStatus.valueOf(newStatus)).build());
        }

        List<Task> tasks = taskService.getAllTasksByProjectId(taskToMove.getProject().getId());

        if (newOrd < 0 || newOrd > tasks.size()) {
            return null;
        }

        tasks.remove(taskToMove);
        tasks.add(newOrd, taskToMove);

        // Обновляем порядок задач в базе данных
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            task.setOrd(i);
            taskService.updateTask(task.getId(), userId, task);
        }

        return taskToMove;
    }

    @QueryMapping("tasksStatistics")
    public List<TaskStatisticsDto> getTaskStatistics() {
        UUID userId = jwtService.getUserIdFromRequest(request);

        List<TaskStatisticsDto> statistics = taskService.getTaskStatistics(userId);
        return statistics;
    }

    @QueryMapping(value = "allTasks")
    public Page<Task> allTasks(@Argument String filter,@Argument  Integer offset,@Argument  Integer limit, @Argument TaskSort sortBy) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        Pageable pageable;
        if (sortBy != null) {
            pageable = PageRequest.of(offset, limit, Sort.by(sortBy.getField()));
        } else {
            pageable = PageRequest.of(offset, limit);
        }
        Specification<Task> spec = TaskSpecifications.withUserAndTitleFilter(userId,filter);
        Page<Task> tasks =  taskService.findAllTasks(spec, pageable);
        return tasks;
    }

//    @QueryMapping(value = "getTaskCount")
//    public long getTaskCount(){
//        return taskService.getCount();
//    }
}
