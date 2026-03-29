package com.rayen.walletManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Service (Python)", description = "Proxy vers le microservice IA Python — Scoring de credit, prediction de defaut, explication du modele")
public class AIServiceController {

    @Value("${AI_SERVICE_URL:${ai.service.url:http://localhost:5000}}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // ==================== HEALTH ====================

    @GetMapping("/health")
    @Operation(summary = "Verifier le statut du service IA", description = "Retourne le statut du microservice Python IA (UP/DOWN)")
    public ResponseEntity<?> health() {
        try {
            String response = restTemplate.getForObject(aiServiceUrl + "/health", String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI Service unavailable: " + e.getMessage()));
        }
    }

    // ==================== MODEL INFO ====================

    @GetMapping("/model-info")
    @Operation(summary = "Informations du modele IA", description = "Retourne les details du modele ML : algorithme, features, importance des features, metriques de performance")
    public ResponseEntity<?> modelInfo() {
        try {
            String response = restTemplate.getForObject(aiServiceUrl + "/api/ai/model-info", String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI Service unavailable: " + e.getMessage()));
        }
    }

    // ==================== PREDICT ====================

    @PostMapping("/predict")
    @Operation(summary = "Prediction de risque pour un client",
            description = "Envoie les donnees financieres d'un client au modele IA et retourne : credit_score (300-850), default_probability, risk_level (LOW/MEDIUM/HIGH/CRITICAL), recommendation, max_allowed_transaction")
    public ResponseEntity<?> predict(@RequestBody Map<String, Object> customerData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(customerData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServiceUrl + "/api/ai/predict", request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI Service unavailable: " + e.getMessage()));
        }
    }

    // ==================== PREDICT BATCH ====================

    @PostMapping("/predict/batch")
    @Operation(summary = "Predictions par lot",
            description = "Envoie plusieurs clients au modele IA pour des predictions groupees. Body: [ {...}, {...} ]")
    public ResponseEntity<?> predictBatch(@RequestBody java.util.List<Map<String, Object>> batchData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<java.util.List<Map<String, Object>>> request = new HttpEntity<>(batchData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServiceUrl + "/api/ai/predict/batch", request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI Service unavailable: " + e.getMessage()));
        }
    }

    // ==================== EXPLAIN ====================

    @PostMapping("/explain")
    @Operation(summary = "Prediction + Explication detaillee",
            description = "Retourne la prediction + les facteurs de risque les plus importants avec leur contribution au score. Utile pour comprendre POURQUOI un client est a risque.")
    public ResponseEntity<?> explain(@RequestBody Map<String, Object> customerData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(customerData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServiceUrl + "/api/ai/explain", request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI Service unavailable: " + e.getMessage()));
        }
    }

    // ==================== SCORE ====================

    @PostMapping("/score")
    @Operation(summary = "Score de credit uniquement (leger)",
            description = "Retourne uniquement le credit_score (300-850) et le risk_level. Plus rapide que /predict car moins de donnees retournees.")
    public ResponseEntity<?> score(@RequestBody Map<String, Object> customerData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(customerData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServiceUrl + "/api/ai/score", request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI Service unavailable: " + e.getMessage()));
        }
    }

    // ==================== TRAIN ====================

    @PostMapping("/train")
    @Operation(summary = "Re-entrainer le modele IA",
            description = "Force le re-entrainement du modele ML sur de nouvelles donnees synthetiques. Utile apres un changement de parametres.")
    public ResponseEntity<?> train() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>("{}", headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServiceUrl + "/api/ai/train", request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "AI Service unavailable: " + e.getMessage()));
        }
    }
}
