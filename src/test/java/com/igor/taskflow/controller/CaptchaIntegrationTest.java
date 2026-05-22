package com.igor.taskflow.controller;

import com.igor.taskflow.TaskFlowApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TaskFlowApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "turnstile.enabled=true",
        "turnstile.secret-key=test-secret"
})
class CaptchaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectRegistrationWithoutTurnstileToken() throws Exception {
        String registerJson = """
                {
                  "name": "Captcha User",
                  "email": "captcha_user@test.com",
                  "login": "captcha_user",
                  "password": "123456"
                }
                """;

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Captcha verification failed"));
    }
}
