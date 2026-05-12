package com.example.demo.controller;

import com.example.demo.dto.TelegramUpdateDto;
import com.example.demo.service.TelegramService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/telegram/webhook")
public class TelegramWebhookController {

    private final TelegramService telegramService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelegramWebhookController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping
    public void handleUpdate(
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretHeader,
            @RequestBody String rawBody
    ) {
        System.out.println("Telegram webhook received");
        System.out.println("Telegram raw update body: " + rawBody);

        if (!telegramService.isValidWebhookSecret(secretHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Telegram webhook secret");
        }

        try {
            TelegramUpdateDto update = objectMapper.readValue(rawBody, TelegramUpdateDto.class);
            telegramService.handleWebhookUpdate(update);
        } catch (Exception e) {
            System.out.println("Failed to parse Telegram update: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Telegram update");
        }
    }
}