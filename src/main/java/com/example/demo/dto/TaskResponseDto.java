package com.example.demo.dto;

import java.util.Date;

public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private Date createdAt;

    public TaskResponseDto() {
    }

    public TaskResponseDto(Long id, String title, String description, Date createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}