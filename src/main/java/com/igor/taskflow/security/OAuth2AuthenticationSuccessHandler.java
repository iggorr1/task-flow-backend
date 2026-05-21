package com.igor.taskflow.security;

import com.igor.taskflow.entity.Role;
import com.igor.taskflow.entity.User;
import com.igor.taskflow.service.OAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2UserService oAuth2UserService;
    private final JwtService jwtService;
    private final String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(
            OAuth2UserService oAuth2UserService,
            JwtService jwtService,
            @Value("${frontend.url}") String frontendUrl
    ) {
        this.oAuth2UserService = oAuth2UserService;
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.Authentication authentication
    ) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        User user = oAuth2UserService.findOrCreateGoogleUser(oauthUser);
        String token = jwtService.generateToken(toUserDetails(user));
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/oauth/success")
                .queryParam("token", token)
                .queryParam("login", user.getLogin())
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private UserDetails toUserDetails(User user) {
        Role role = user.getRole() != null ? user.getRole() : Role.USER;

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword() != null ? user.getPassword() : "",
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
    }
}
