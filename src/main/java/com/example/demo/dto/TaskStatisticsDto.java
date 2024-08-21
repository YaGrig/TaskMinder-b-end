package com.example.demo.dto;

import com.example.demo.models.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStatisticsDto {
    private TaskStatus name;
    private Integer value;

}
