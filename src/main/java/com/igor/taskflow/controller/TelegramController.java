package com.igor.taskflow.controller;

import com.igor.taskflow.dto.TelegramLinkResponseDto;
import com.igor.taskflow.dto.TelegramStatusResponseDto;
import com.igor.taskflow.service.TelegramService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/telegram")
public class TelegramController {

    private final TelegramService telegramService;

    public TelegramController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping("/link")
    public TelegramLinkResponseDto createLink() {
        return telegramService.createLink();
    }

    @GetMapping("/status")
    public TelegramStatusResponseDto getStatus() {
        return telegramService.getStatus();
    }

    @DeleteMapping("/disconnect")
    public void disconnect() {
        telegramService.disconnect();
    }

    @PostMapping("/dev/connect")
    public void devConnectTelegram(
            @RequestParam String token,
            @RequestParam Long chatId,
            @RequestParam Long telegramUserId,
            @RequestParam(required = false) String username
    ) {
        telegramService.connectTelegramByToken(token, chatId, telegramUserId, username);
    }
}