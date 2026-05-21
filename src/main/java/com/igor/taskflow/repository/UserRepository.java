package com.igor.taskflow.repository;

import com.igor.taskflow.entity.User;
import com.igor.taskflow.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
