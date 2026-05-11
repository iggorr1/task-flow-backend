package com.example.demo.dto;

import com.example.demo.entity.TaskStatus;

import java.util.Date;

public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private Date createdAt;
    private boolean completed;
    private TaskStatus status;
    private boolean pinned;
    private Date reminderAt;
    private boolean reminderSent;

    public TaskResponseDto(Long id, String title, String description, Date createdAt, boolean completed, boolean pinned, TaskStatus status,Date reminderAt, boolean reminderSent) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.completed = completed;
        this.pinned = pinned;
        this.status = status;
        this.reminderAt = reminderAt;
        this.reminderSent = reminderSent;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public Date getReminderAt() {
        return reminderAt;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }
}