package com.igor.taskflow.controller;

import com.igor.taskflow.dto.TelegramUpdateDto;
import com.igor.taskflow.service.TelegramService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/telegram/webhook")
public class TelegramWebhookController {

    private final TelegramService telegramService;

    public TelegramWebhookController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping
    public void handleUpdate(
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretHeader,
            @RequestBody TelegramUpdateDto update
    ) {
        if (!telegramService.isValidWebhookSecret(secretHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Telegram webhook secret");
        }

        telegramService.handleWebhookUpdate(update);
    }
}