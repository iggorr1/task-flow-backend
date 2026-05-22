package com.igor.taskflow.service;

import com.igor.taskflow.config.TurnstileProperties;
import com.igor.taskflow.exception.CaptchaVerificationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TurnstileVerificationServiceTest {

    @Test
    void shouldSkipVerificationWhenTurnstileIsDisabled() {
        TurnstileVerificationService service = new TurnstileVerificationService(disabledProperties());

        assertDoesNotThrow(() -> service.verify(""));
    }

    @Test
    void shouldRejectMissingTokenWhenTurnstileIsEnabled() {
        TurnstileVerificationService service = new TurnstileVerificationService(enabledProperties(), new RestTemplate());

        assertThrows(CaptchaVerificationException.class, () -> service.verify(""));
    }

    @Test
    void shouldPassWhenCloudflareAcceptsToken() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        server.expect(once(), requestTo("https://turnstile.test/siteverify"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string("secret=test-secret&response=valid-token"))
                .andRespond(withSuccess("{\"success\":true}", MediaType.APPLICATION_JSON));

        TurnstileVerificationService service = new TurnstileVerificationService(enabledProperties(), restTemplate);

        assertDoesNotThrow(() -> service.verify("valid-token"));
        server.verify();
    }

    @Test
    void shouldRejectWhenCloudflareRejectsToken() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        server.expect(once(), requestTo("https://turnstile.test/siteverify"))
                .andRespond(withSuccess("{\"success\":false}", MediaType.APPLICATION_JSON));

        TurnstileVerificationService service = new TurnstileVerificationService(enabledProperties(), restTemplate);

        assertThrows(CaptchaVerificationException.class, () -> service.verify("invalid-token"));
        server.verify();
    }

    private TurnstileProperties enabledProperties() {
        TurnstileProperties properties = new TurnstileProperties();
        properties.setEnabled(true);
        properties.setSecretKey("test-secret");
        properties.setVerifyUrl("https://turnstile.test/siteverify");
        return properties;
    }

    private TurnstileProperties disabledProperties() {
        TurnstileProperties properties = new TurnstileProperties();
        properties.setEnabled(false);
        return properties;
    }
}
