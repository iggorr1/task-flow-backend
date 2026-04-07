package com.example.demo.dto;

import java.util.Date;

public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private String login;
    private Date createdAt;

    public UserResponseDto(Long id, String name, String email, String login, Date createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.createdAt = createdAt;
    }

    public UserResponseDto() {

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getLogin() {
        return login;
    }

    public Long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

}