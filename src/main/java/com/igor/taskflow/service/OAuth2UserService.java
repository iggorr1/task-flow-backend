package com.igor.taskflow.service;

import com.igor.taskflow.entity.AuthProvider;
import com.igor.taskflow.entity.Role;
import com.igor.taskflow.entity.User;
import com.igor.taskflow.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrCreateGoogleUser(OAuth2User oauthUser) {
        String providerId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        if (providerId == null || email == null) {
            throw new IllegalArgumentException("Google account must provide subject and email");
        }

        return userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, providerId)
                .or(() -> userRepository.findByEmail(email))
                .map(user -> updateGoogleUser(user, providerId, name))
                .orElseGet(() -> createGoogleUser(providerId, email, name));
    }

    private User updateGoogleUser(User user, String providerId, String name) {
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);

        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        return userRepository.save(user);
    }

    private User createGoogleUser(String providerId, String email, String name) {
        User user = new User();
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);
        user.setEmail(email);
        user.setLogin(email);
        user.setName(name != null && !name.isBlank() ? name : email);
        user.setPassword("");
        user.setRole(Role.USER);
        user.setCreatedAt(new Date());

        return userRepository.save(user);
    }
}
