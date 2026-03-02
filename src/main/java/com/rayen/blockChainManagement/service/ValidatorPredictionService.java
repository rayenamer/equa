package com.rayen.blockChainManagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.model.ValidatorPrediction;
import com.rayen.blockChainManagement.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidatorPredictionService {

    private final GeminiService geminiService;
    private final NodeRepository nodeRepository;
    private final ObjectMapper objectMapper;

    public ValidatorPrediction predictNextValidator() {
        List<Node> nodes = nodeRepository.findAll();

        StringBuilder prompt = new StringBuilder();
        prompt.append("""
            You are a blockchain analyst. Based on the node data below, predict which node is most likely
            to win the next validation race. Remember: lower reputation score = shorter wait time = higher chance of winning first.
            Return ONLY valid JSON with no markdown, no backticks.

            Return exactly this structure:
            {
              "predictedNodeId": <nodeId>,
              "location": "<location>",
              "nodeType": "<type>",
              "reputation": <reputationScore>,
              "winProbability": <0-100>,
              "reasoning": "<2 sentences explaining why>",
              "allNodeOdds": [
                { "nodeId": <id>, "location": "<location>", "winChance": <0-100>, "reason": "<one sentence>" },
                ...
              ]
            }

            === NODES ===
            """);

        nodes.forEach(node -> prompt.append(String.format(
                "Node %d | type: %s | location: %s | reputation: %.1f | status: %s | total validations: %d\n",
                node.getNodeId(), node.getNodeType(), node.getLocation(),
                node.getReputationScore(), node.getStatus(),
                node.getTransactions() != null ? node.getTransactions().size() : 0
        )));

        prompt.append("""

            Rules of the system:
            - Each node waits reputationScore milliseconds before guessing
            - Lower reputation = faster guess = higher win chance
            - Hash is random (a-f), so luck matters but speed is the main factor
            - Winner gets -1 reputation (becomes faster next time = privilege)
            """);

        String raw = geminiService.analyzeBlockchain(prompt.toString());

        try {
            String clean = raw.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(clean, ValidatorPrediction.class);
        } catch (Exception e) {
            log.error("Failed to parse prediction JSON: {}", raw, e);
            ValidatorPrediction fallback = new ValidatorPrediction();
            fallback.setReasoning("Failed to parse Gemini response.");
            return fallback;
        }
    }
}