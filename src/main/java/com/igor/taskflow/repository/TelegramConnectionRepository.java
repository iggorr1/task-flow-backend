package com.igor.taskflow.repository;

import com.igor.taskflow.entity.TelegramConnection;
import com.igor.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramConnectionRepository extends JpaRepository<TelegramConnection, Long> {

    Optional<TelegramConnection> findByUser(User user);

    boolean existsByUser(User user);

    void deleteByUser(User user);
}