package com.igor.taskflow.dto;

import java.util.Date;

public class TelegramLinkResponseDto {

    private String link;
    private Date expiresAt;

    public TelegramLinkResponseDto(String link, Date expiresAt) {
        this.link = link;
        this.expiresAt = expiresAt;
    }

    public String getLink() {
        return link;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }
}