package com.example.demo.dto;

import java.util.Date;

public class UpdateTaskReminderRequestDto {

    private Date reminderAt;

    public Date getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(Date reminderAt) {
        this.reminderAt = reminderAt;
    }
}