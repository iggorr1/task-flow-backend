package com.example.demo.dto;

import com.example.demo.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskStatusRequestDto {

    @NotNull
    private TaskStatus status;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}