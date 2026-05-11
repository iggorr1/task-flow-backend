package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class CreateTaskRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 120, message = "Title is too long. Maximum 120 characters.")
    private String title;

    @Size(max = 255, message = "Description is too long. Maximum 255 characters.")
    private String description;

    private Date reminderAt;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getReminderAt() {
        return reminderAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReminderAt(Date reminderAt) {
        this.reminderAt = reminderAt;
    }
}