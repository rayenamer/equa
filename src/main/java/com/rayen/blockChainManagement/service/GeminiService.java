package com.rayen.blockChainManagement.service;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

    public String analyzeBlockchain(String prompt) {
        String url = GEMINI_URL + "?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            Map response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return extractText(response);
        } catch (Exception e) {
            log.error("Gemini API error", e);
            return "Unable to analyze blockchain at this time.";
        }
    }

    public String chat(List<String> history) {
        String url = GEMINI_URL + "?key=" + apiKey;

        List<Map<String, Object>> contents = new java.util.ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "model";
            contents.add(Map.of(
                    "role", role,
                    "parts", List.of(Map.of("text", history.get(i)))
            ));
        }

        Map<String, Object> body = Map.of("contents", contents);

        try {
            Map response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return extractText(response);
        } catch (Exception e) {
            log.error("Gemini chat API error", e);
            return "Unable to process chat at this time.";
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map response) {
        List<Map> candidates = (List<Map>) response.get("candidates");
        Map content = (Map) candidates.get(0).get("content");
        List<Map> parts = (List<Map>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}