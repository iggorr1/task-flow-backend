package com.igor.taskflow.service;

import com.igor.taskflow.dto.TelegramLinkResponseDto;
import com.igor.taskflow.dto.TelegramStatusResponseDto;
import com.igor.taskflow.entity.TelegramConnection;
import com.igor.taskflow.entity.TelegramLinkCode;
import com.igor.taskflow.entity.User;
import com.igor.taskflow.repository.TelegramConnectionRepository;
import com.igor.taskflow.repository.TelegramLinkCodeRepository;
import com.igor.taskflow.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.igor.taskflow.dto.TelegramUpdateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Service
public class TelegramService {

    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.webhook.secret}")
    private String telegramWebhookSecret;

    private static final long LINK_TOKEN_TTL_MS = 10 * 60 * 1000;


    private final TelegramLinkCodeRepository telegramLinkCodeRepository;
    private final TelegramConnectionRepository telegramConnectionRepository;
    private final UserRepository userRepository;
    private final TelegramMessageService telegramMessageService;
    private final SecureRandom secureRandom = new SecureRandom();

    public TelegramService(
            TelegramLinkCodeRepository telegramLinkCodeRepository,
            TelegramConnectionRepository telegramConnectionRepository,
            UserRepository userRepository,
            TelegramMessageService telegramMessageService
    ) {
        this.telegramLinkCodeRepository = telegramLinkCodeRepository;
        this.telegramConnectionRepository = telegramConnectionRepository;
        this.userRepository = userRepository;
        this.telegramMessageService = telegramMessageService;
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

        String link = "https://t.me/" + botUsername + "?start=" + token;

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

    @Transactional
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
        try {
            telegramMessageService.sendMessage(
                    telegramChatId,
                    "Telegram connected to TaskFlow.\nYou will now receive task reminders here."
            );
        } catch (Exception e) {
            log.warn("Failed to send Telegram connection confirmation", e);
        }
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

    public void handleWebhookUpdate(TelegramUpdateDto update) {
        if (update == null) {
            return;
        }

        if (update.getMessage() == null) {
            return;
        }

        String text = update.getMessage().getText();

        if (text == null) {
            return;
        }

        if (!text.startsWith("/start ")) {
            return;
        }

        String token = text.substring("/start ".length()).trim();

        if (token.isBlank()) {
            return;
        }

        Long chatId = update.getMessage().getChat().getId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUsername();

        connectTelegramByToken(token, chatId, telegramUserId, username);
    }


    public boolean isValidWebhookSecret(String secretHeader) {
        return telegramWebhookSecret != null
                && !telegramWebhookSecret.isBlank()
                && telegramWebhookSecret.equals(secretHeader);
    }

}