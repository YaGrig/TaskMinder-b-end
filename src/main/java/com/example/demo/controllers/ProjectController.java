package com.example.demo.controllers;


import com.example.demo.config.JwtService;
import com.example.demo.dto.ProjectTaskStatisticsDTO;
import com.example.demo.models.Project;
import com.example.demo.models.ProjectInput;
import com.example.demo.models.User;
import com.example.demo.services.ProjectService;
import com.example.demo.services.UserService;
import com.example.demo.specifications.ProjectSpecifications;
import com.example.demo.specifications.UserSpecifications;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/api")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final JwtService jwtService;
    private final UserService userService;
    private final HttpServletRequest request;

    @MutationMapping(value = "createProject")
    public Project createProject(@Argument Project project) {
        try {
            UUID userId = jwtService.getUserIdFromRequest(request);
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            project.setUser(user);

            List<User> members = project.getMembers();
            if (members == null) {
                members = new ArrayList<>();
                project.setMembers(members);
            }
            members.add(user);

            return projectService.createProject(project);
        } catch (Exception e) {
            // Логирование ошибки
            e.printStackTrace();
            // Вместо возврата null, лучше бросить исключение или вернуть ошибку
            throw new RuntimeException("Ошибка при создании проекта: " + e.getMessage());
        }
    }

    public Project updateProject(@Argument UUID id, @Argument ProjectInput input) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        if (userId != null) {
            Optional<Project> existingProject = projectService.getProjectByIdAndUserId(id, userId);
            if (existingProject.isPresent()) {
                Project project = existingProject.get();
                if (input.getTitle() != null) project.setTitle(input.getTitle());
                if (input.getDescription() != null) project.setDescription(input.getDescription());
                Project updatedProject = projectService.updateProject(project.getId(), project);
                return updatedProject;
            } else {
                // Handle the case where the project is not found
                return null;
            }
        } else {
            // Handle the case where the user is not authenticated
            return null;
        }
    }

    @MutationMapping(value = "deleteProject")
    public ResponseEntity<Void> deleteProject(@Argument UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @QueryMapping(value = "projectById")
    public Optional<Project> getProjectById(@Argument UUID id) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        Optional<Project> project = projectService.getProjectByIdAndUserId(id,userId);
        return project;
    }

    @QueryMapping(value = "ProjectTaskStatistics")
    public List<ProjectTaskStatisticsDTO> getProjectTaskStatistics() {
        UUID userId = jwtService.getUserIdFromRequest(request);
        return projectService.getProjectTaskStatistics(userId);
    }

    @QueryMapping(value = "allProjects")
    public Page<Project> getAllProjects(@Argument String filter, @Argument Integer offset, @Argument Integer limit) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        Pageable pageable = PageRequest.of(offset, limit);
        Specification<Project> spec = ProjectSpecifications.withUserAndTitleFilter(userId,filter);
        Page<Project> projects = projectService.getAllProjects(spec, pageable);
        return projects;
    }
}
