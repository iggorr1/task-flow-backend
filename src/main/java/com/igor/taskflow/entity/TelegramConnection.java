package com.igor.taskflow.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "telegram_connections")
public class TelegramConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramChatId;

    @Column(nullable = false)
    private Long telegramUserId;

    private String telegramUsername;

    @Column(nullable = false)
    private Date connectedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Long getId() {
        return id;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public Long getTelegramUserId() {
        return telegramUserId;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public Date getConnectedAt() {
        return connectedAt;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTelegramChatId(Long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public void setTelegramUserId(Long telegramUserId) {
        this.telegramUserId = telegramUserId;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public void setConnectedAt(Date connectedAt) {
        this.connectedAt = connectedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }
}