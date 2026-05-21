package com.igor.taskflow.service;

import com.igor.taskflow.entity.AuthProvider;
import com.igor.taskflow.entity.Role;
import com.igor.taskflow.entity.User;
import com.igor.taskflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OAuth2UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final OAuth2UserService service = new OAuth2UserService(userRepository);

    @Test
    void createsGoogleUserWhenAccountIsNew() {
        OAuth2User oauthUser = googleUser("google-123", "google@example.com", "Google User");

        when(userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, "google-123"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("google@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.findOrCreateGoogleUser(oauthUser);

        assertThat(user.getProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(user.getProviderId()).isEqualTo("google-123");
        assertThat(user.getEmail()).isEqualTo("google@example.com");
        assertThat(user.getLogin()).isEqualTo("google@example.com");
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void linksExistingUserByEmail() {
        User existingUser = new User();
        existingUser.setEmail("local@example.com");
        existingUser.setLogin("local");
        existingUser.setRole(Role.USER);

        OAuth2User oauthUser = googleUser("google-456", "local@example.com", "Linked User");

        when(userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, "google-456"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("local@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User user = service.findOrCreateGoogleUser(oauthUser);

        assertThat(user.getProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(user.getProviderId()).isEqualTo("google-456");
        assertThat(user.getLogin()).isEqualTo("local");
        assertThat(user.getName()).isEqualTo("Linked User");
    }

    private OAuth2User googleUser(String subject, String email, String name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", subject);
        attributes.put("email", email);
        attributes.put("name", name);

        return new DefaultOAuth2User(Collections.emptyList(), attributes, "sub");
    }
}
