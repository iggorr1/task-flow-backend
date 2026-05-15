package com.example.demo.dto;

import com.example.demo.entity.Role;

import java.util.Date;

public class AdminUserResponseDto {

    private Long id;
    private String name;
    private String login;
    private String email;
    private Date createdAt;
    private Role role;
    private Long telegramChatId;
    private String telegramUsername;
    private Date telegramConnectedAt;

    public AdminUserResponseDto(
            Long id,
            String name,
            String login,
            String email,
            Date createdAt,
            Role role,
            Long telegramChatId,
            String telegramUsername,
            Date telegramConnectedAt
    ) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.email = email;
        this.createdAt = createdAt;
        this.role = role;
        this.telegramChatId = telegramChatId;
        this.telegramUsername = telegramUsername;
        this.telegramConnectedAt = telegramConnectedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Role getRole() {
        return role;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public Date getTelegramConnectedAt() {
        return telegramConnectedAt;
    }
}