package com.igor.taskflow.repository;

import com.igor.taskflow.entity.TelegramLinkCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramLinkCodeRepository extends JpaRepository<TelegramLinkCode, Long> {

    Optional<TelegramLinkCode> findByToken(String token);
}