package com.example.demo.services;


import com.example.demo.dto.ProjectTaskStatisticsDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Project;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.ProjectRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ProjectService {

    @Autowired
    public ProjectRepository repository;

    public Project createProject(Project project) {
        return repository.save(project);
    };

    public Project updateProject(UUID id, Project projectDetails){

        Optional<Project> projectOptional = repository.findById(id);
        if(projectOptional.isPresent()){
            Project existingProject = projectOptional.get();

            existingProject.setTitle(projectDetails.getTitle());
            existingProject.setDescription(projectDetails.getDescription());

            return repository.save(existingProject);
        } else {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
    }

    public List<ProjectTaskStatisticsDTO> getProjectTaskStatistics(UUID userId) {
        return repository.findProjectTaskStatistics(userId).stream()
                .map(projectTask -> new ProjectTaskStatisticsDTO(
                        projectTask.getProjectName(),
                        projectTask.getTaskCount()))
                .collect(Collectors.toList());
    }

    public Optional<Project> getProjectByIdAndUserId(UUID id, UUID userId) {
        return repository.findProjectByIdAndUserId(id, userId);
    }
    public void deleteProject(UUID id){
        repository.deleteById(id);
    }
    public Optional<Project> getProjectById(UUID id){
        return repository.findById(id);
    }
    public Page<Project> getAllProjects(Specification<Project> spec, Pageable pageable ){
        return repository.findAll(spec, pageable);
    }


}
