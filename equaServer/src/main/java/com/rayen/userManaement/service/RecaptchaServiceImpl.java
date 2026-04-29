package com.rayen.userManaement.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Vérifie le token reCAPTCHA v2 via l'API Google siteverify.
 * Si aucune clé secrète n'est configurée, la vérification est désactivée (mode dev).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecaptchaServiceImpl implements RecaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.recaptcha.secret-key:}")
    private String secretKey;

    @Override
    public boolean verify(String token) {
        String key = secretKey != null ? secretKey.trim() : "";
        if (key.isEmpty()) {
            log.debug("reCAPTCHA désactivé (pas de clé secrète)");
            return true;
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("reCAPTCHA requis. Complétez « Je ne suis pas un robot ».");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", key);
        body.add("response", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                VERIFY_URL,
                HttpMethod.POST,
                request,
                JsonNode.class
        );

        if (response.getBody() == null) {
            throw new IllegalArgumentException("Erreur reCAPTCHA: pas de réponse.");
        }
        JsonNode json = response.getBody();
        boolean success = json.has("success") && json.get("success").asBoolean();
        if (!success) {
            String errors = json.has("error-codes") ? json.get("error-codes").toString() : "unknown";
            log.warn("reCAPTCHA échoué: {}", errors);
            throw new IllegalArgumentException("Verification reCAPTCHA echouee (" + errors + ").");
        }
        return true;
    }
}
