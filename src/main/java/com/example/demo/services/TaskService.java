package com.example.demo.services;


import com.example.demo.dto.TaskStatisticsDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Project;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.models.TaskStatus;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.specifications.TaskSpecifications;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class TaskService {

    @Autowired
    public TaskRepository repository;
    public TaskSpecifications taskSpecifications;

    public Task createTask(Task task) {
        Integer maxOrd = this.getHighestOrdValue();
        // Corrected ternary operator syntax
        Integer ord = (maxOrd != null) ? maxOrd + 1 : 0;
        task.setOrd(ord);
        return repository.save(task);
    }

    public Task updateTask(UUID id, UUID userId, Task taskDetails) {
        Optional<Task> existingTask = this.getTaskByIdAndUserId(id, userId);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            if (taskDetails.getTitle() != null) task.setTitle(taskDetails.getTitle());
            if (taskDetails.getTitle() != null) {
                task.setTitle(taskDetails.getTitle());
            }
            if (taskDetails.getDescription() != null) {
                task.setDescription(taskDetails.getDescription());
            }
            if (taskDetails.getType() != null) {
                task.setType(taskDetails.getType());
            }
            if (taskDetails.getStatus() != null) {
                task.setStatus(taskDetails.getStatus());
            }
            if (taskDetails.getPriority() != null) {
                task.setPriority(taskDetails.getPriority());
            }
            if (taskDetails.getDueDate() != null) {
                task.setDueDate(taskDetails.getDueDate());
            }
//            if (taskDetails.getProject() != null) {
//                Optional<Project> project = projectService.getProjectById(input.getProject());
//                task.setProject(project.get());
//            }
//                if (input.getAssignedTo() != null) {
//                    task.setAssignedTo(input.getAssignedTo());
//                }
            return repository.save(task);
        }
        return null;
    }

    public boolean isTaskOwner(UUID taskId, UUID userId) {
        return repository.findById(taskId)
                .map(Task::getUser)
                .filter(id -> id.equals(userId))
                .isPresent();
    }

    public long getCount(Specification<Task> spec) {
        return repository.count(spec);
    }

    public Optional<Task> getTaskByIdAndUserId(UUID id, UUID userId) {
        return repository.findTaskByIdAndUserId(id, userId);
    }

//    public List<Task> getTasksByUserId(UUID userId) {
//        return repository.findAllByUser(userId);
//    }
    public Integer getHighestOrdValue() {
        return repository.findMaxOrd();
    }

    public List<Task> getAllTasksOrderedByOrd() {
        return repository.findAllByOrderByOrdAsc();
    }

    public void saveAll(List<Task> tasks) {
        repository.saveAll(tasks);
    }

    public void deleteTask(UUID id){
        repository.deleteById(id);
    }
    public Optional<Task> getTaskById(UUID id){
        return repository.findById(id);
    }

//    Specification<Task> spec, Pageable pageable

    public List<TaskStatisticsDto> getTaskStatistics(UUID userId) {
        List<TaskStatisticsDto> list = repository.findTaskStatistics(userId);
        return list;
    }
    public List<Task> getTasksByStatus(TaskStatus status){
        return repository.findByStatus(status);
    }

    public Page<Task> findAllTasks(Specification<Task> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }
    public List<Task> getAllTasksByProjectId(UUID id) {
        return repository.findAllByProject(id);
    }
}
