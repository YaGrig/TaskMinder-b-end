package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskInput {

    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private TaskTypes type;
    private LocalDate dueDate;
    private Integer ord;
    private String assignedUserEmail;
    private UUID project;
}
