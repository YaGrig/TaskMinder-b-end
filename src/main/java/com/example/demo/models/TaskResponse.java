package com.example.demo.models;

import com.example.demo.models.Task;
import org.springframework.data.domain.Page;

import java.util.List;

public class TaskResponse {
    private Page<Task> tasks;
    private long totalCount;

    public TaskResponse(Page<Task> page, long totalCount) {
        this.tasks = page;
        this.totalCount = totalCount;
    }

    public Page<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Page<Task> tasks) {
        this.tasks = tasks;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
