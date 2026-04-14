package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTaskRequestDto {

    @NotBlank
    private String title;

    private String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}