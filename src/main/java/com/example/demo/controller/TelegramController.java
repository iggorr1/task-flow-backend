package com.example.demo.controller;

import com.example.demo.dto.TelegramLinkResponseDto;
import com.example.demo.dto.TelegramStatusResponseDto;
import com.example.demo.service.TelegramService;
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
}