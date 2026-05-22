package com.igor.taskflow.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.igor.taskflow.config.TurnstileProperties;
import com.igor.taskflow.exception.CaptchaVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class TurnstileVerificationService {

    private final TurnstileProperties properties;
    private final RestTemplate restTemplate;

    @Autowired
    public TurnstileVerificationService(TurnstileProperties properties) {
        this(properties, new RestTemplate());
    }

    TurnstileVerificationService(TurnstileProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public void verify(String token) {
        if (!properties.isEnabled()) {
            return;
        }

        if (!StringUtils.hasText(token) || !StringUtils.hasText(properties.getSecretKey())) {
            throw new CaptchaVerificationException();
        }

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", properties.getSecretKey());
        body.add("response", token);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        org.springframework.http.HttpEntity<LinkedMultiValueMap<String, String>> request =
                new org.springframework.http.HttpEntity<>(body, headers);

        try {
            TurnstileVerificationResponse response = restTemplate.postForObject(
                    properties.getVerifyUrl(),
                    request,
                    TurnstileVerificationResponse.class
            );

            if (response == null || !response.success()) {
                throw new CaptchaVerificationException();
            }
        } catch (RestClientException ex) {
            throw new CaptchaVerificationException();
        }
    }

    private record TurnstileVerificationResponse(
            boolean success,
            @JsonProperty("error-codes") String[] errorCodes
    ) {
    }
}
