package com.example.demo.models;

public enum TaskSort {
    NAME("title"),
    DATE("dueDate"),
    STATUS("status");

    private final String field;

    private TaskSort(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
