package com.rayen.blockChainManagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.model.*;
import com.rayen.blockChainManagement.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/smartContract")
@RequiredArgsConstructor
@Slf4j
public class SmartContractController {
    private final SmartContract smartContract;
    private final BlockchainAnalysisService blockchainAnalysisService;
    private final BlockchainChatService blockchainChatService;
    private final BlockchainHealthService blockchainHealthService;
    private final ValidatorPredictionService validatorPredictionService;
    @PostMapping("/process")
    public ResponseEntity<TransactionResponse> processTransaction(@RequestBody TransactionRequest request)
            throws BadRequestException, JsonProcessingException, ExecutionException, InterruptedException {
        log.info("Processing transaction from wallet: {}", request.getFromWallet());
        TransactionResponse response = smartContract.processTransaction(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<Node>> getAllNodes() {
        log.info("REST request to get all nodes with blockchain");
        return ResponseEntity.ok(smartContract.getAllNodesWithBlockchain());
    }

    @GetMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyze() {
        String analysis = blockchainAnalysisService.analyzeBlockchainState();
        return ResponseEntity.ok(Map.of("analysis", analysis));
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        String response = blockchainChatService.chat(request.getSessionId(), request.getMessage());
        return ResponseEntity.ok(Map.of(
                "sessionId", request.getSessionId(),
                "response", response
        ));
    }
    @GetMapping("/health")
    public ResponseEntity<HealthScore> getHealthScore() {
        return ResponseEntity.ok(blockchainHealthService.getHealthScore());
    }

    @DeleteMapping("/chat/{sessionId}")
    public ResponseEntity<Void> clearChat(@PathVariable String sessionId) {
        blockchainChatService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/predict")
    public ResponseEntity<ValidatorPrediction> predictNextValidator() {
        return ResponseEntity.ok(validatorPredictionService.predictNextValidator());
    }
}


