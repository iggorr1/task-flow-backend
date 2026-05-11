package com.example.demo.service;

import com.example.demo.dto.TelegramLinkResponseDto;
import com.example.demo.dto.TelegramStatusResponseDto;
import com.example.demo.entity.TelegramConnection;
import com.example.demo.entity.TelegramLinkCode;
import com.example.demo.entity.User;
import com.example.demo.repository.TelegramConnectionRepository;
import com.example.demo.repository.TelegramLinkCodeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Service
public class TelegramService {

    private static final long LINK_TOKEN_TTL_MS = 10 * 60 * 1000;
    private static final String BOT_USERNAME = "taskflow_reminders_ik_bot"; // later move to env

    private final TelegramLinkCodeRepository telegramLinkCodeRepository;
    private final TelegramConnectionRepository telegramConnectionRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public TelegramService(
            TelegramLinkCodeRepository telegramLinkCodeRepository,
            TelegramConnectionRepository telegramConnectionRepository,
            UserRepository userRepository
    ) {
        this.telegramLinkCodeRepository = telegramLinkCodeRepository;
        this.telegramConnectionRepository = telegramConnectionRepository;
        this.userRepository = userRepository;
    }

    public TelegramLinkResponseDto createLink() {
        User user = getCurrentUser();

        String token = generateToken();

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + LINK_TOKEN_TTL_MS);

        TelegramLinkCode linkCode = new TelegramLinkCode();
        linkCode.setUser(user);
        linkCode.setToken(token);
        linkCode.setCreatedAt(now);
        linkCode.setExpiresAt(expiresAt);
        linkCode.setUsed(false);

        telegramLinkCodeRepository.save(linkCode);

        String link = "https://t.me/" + BOT_USERNAME + "?start=" + token;

        return new TelegramLinkResponseDto(link, expiresAt);
    }

    public TelegramStatusResponseDto getStatus() {
        User user = getCurrentUser();

        return telegramConnectionRepository.findByUser(user)
                .map(connection -> new TelegramStatusResponseDto(
                        true,
                        connection.getTelegramUsername(),
                        connection.getConnectedAt()
                ))
                .orElse(new TelegramStatusResponseDto(false, null, null));
    }

    public void disconnect() {
        User user = getCurrentUser();
        telegramConnectionRepository.deleteByUser(user);
    }

    public void connectTelegramByToken(
            String token,
            Long telegramChatId,
            Long telegramUserId,
            String telegramUsername
    ) {
        TelegramLinkCode linkCode = telegramLinkCodeRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Telegram link token"));

        if (linkCode.isUsed()) {
            throw new RuntimeException("Telegram link token already used");
        }

        if (linkCode.getExpiresAt().before(new Date())) {
            throw new RuntimeException("Telegram link token expired");
        }

        User user = linkCode.getUser();

        telegramConnectionRepository.findByUser(user)
                .ifPresent(existingConnection -> telegramConnectionRepository.delete(existingConnection));

        TelegramConnection connection = new TelegramConnection();
        connection.setUser(user);
        connection.setTelegramChatId(telegramChatId);
        connection.setTelegramUserId(telegramUserId);
        connection.setTelegramUsername(telegramUsername);
        connection.setConnectedAt(new Date());

        telegramConnectionRepository.save(connection);

        linkCode.setUsed(true);
        telegramLinkCodeRepository.save(linkCode);
    }

    private User getCurrentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}