package com.igor.taskflow.dto;

import java.util.Date;

public class TelegramStatusResponseDto {

    private boolean connected;
    private String telegramUsername;
    private Date connectedAt;

    public TelegramStatusResponseDto(boolean connected, String telegramUsername, Date connectedAt) {
        this.connected = connected;
        this.telegramUsername = telegramUsername;
        this.connectedAt = connectedAt;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public Date getConnectedAt() {
        return connectedAt;
    }
}