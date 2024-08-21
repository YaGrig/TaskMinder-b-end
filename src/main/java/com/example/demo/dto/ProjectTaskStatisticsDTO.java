package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProjectTaskStatisticsDTO {
    private String projectName; // Используется как 'name' в данных диаграммы
    private Long taskCount;     // Используется как 'pv' в данных диаграммы
    // Другие поля, если они вам нужны для диаграммы
}
