package com.igor.taskflow.dto;

import com.igor.taskflow.entity.TaskStatus;

import java.util.Date;

public class AdminTaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private Date createdAt;
    private boolean completed;
    private boolean pinned;
    private TaskStatus status;
    private Date reminderAt;
    private boolean reminderSent;
    private Long userId;
    private String userLogin;

    public AdminTaskResponseDto(
            Long id,
            String title,
            String description,
            Date createdAt,
            boolean completed,
            boolean pinned,
            TaskStatus status,
            Date reminderAt,
            boolean reminderSent,
            Long userId,
            String userLogin
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.completed = completed;
        this.pinned = pinned;
        this.status = status;
        this.reminderAt = reminderAt;
        this.reminderSent = reminderSent;
        this.userId = userId;
        this.userLogin = userLogin;
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

    public boolean isPinned() {
        return pinned;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Date getReminderAt() {
        return reminderAt;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserLogin() {
        return userLogin;
    }
}