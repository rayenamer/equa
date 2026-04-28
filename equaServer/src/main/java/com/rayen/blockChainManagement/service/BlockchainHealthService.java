package com.rayen.blockChainManagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayen.blockChainManagement.entity.Block;
import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.model.HealthScore;
import com.rayen.blockChainManagement.repository.BlockRepository;
import com.rayen.blockChainManagement.repository.NodeRepository;
import com.rayen.blockChainManagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainHealthService {

    private final GeminiService geminiService;
    private final NodeRepository nodeRepository;
    private final BlockRepository blockRepository;
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    public HealthScore getHealthScore() {
        List<Node> nodes = nodeRepository.findAll();
        List<Block> blocks = blockRepository.findAllBlocksWithTransactions();
        long totalTransactions = transactionRepository.count();

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a blockchain analyst. Analyze this blockchain and return ONLY a valid JSON object with no markdown, no backticks, no explanation outside the JSON.\n\n");
        prompt.append("Return exactly this structure:\n");
        prompt.append("{\n");
        prompt.append("  \"decentralizationScore\": <0-100>,\n");
        prompt.append("  \"decentralizationExplanation\": \"<one sentence>\",\n");
        prompt.append("  \"activityScore\": <0-100>,\n");
        prompt.append("  \"activityExplanation\": \"<one sentence>\",\n");
        prompt.append("  \"nodeDiversityScore\": <0-100>,\n");
        prompt.append("  \"nodeDiversityExplanation\": \"<one sentence>\",\n");
        prompt.append("  \"overallScore\": <0-100>,\n");
        prompt.append("  \"overallExplanation\": \"<one sentence>\"\n");
        prompt.append("}\n\n");

        prompt.append("=== BLOCKCHAIN STATE ===\n");
        prompt.append("Total blocks: ").append(blocks.size()).append("\n");
        prompt.append("Total transactions: ").append(totalTransactions).append("\n\n");

        prompt.append("=== NODES ===\n");
        nodes.forEach(node -> prompt.append(String.format(
                "Node %d | type: %s | location: %s | reputation: %.1f | status: %s | validations: %d\n",
                node.getNodeId(), node.getNodeType(), node.getLocation(),
                node.getReputationScore(), node.getStatus(),
                node.getTransactions() != null ? node.getTransactions().size() : 0
        )));

        prompt.append("\n=== BLOCKS ===\n");
        blocks.forEach(block -> prompt.append(String.format(
                "Block %d | hash: %s | size remaining: %d | transactions: %d\n",
                block.getBlockId(), block.getBlockHash(),
                block.getBlockSize(),
                block.getTransaction() != null ? block.getTransaction().size() : 0
        )));

        String raw = geminiService.analyzeBlockchain(prompt.toString());

        try {
            // Strip markdown backticks just in case Gemini adds them anyway
            String clean = raw.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(clean, HealthScore.class);
        } catch (Exception e) {
            log.error("Failed to parse health score JSON: {}", raw, e);
            HealthScore fallback = new HealthScore();
            fallback.setOverallExplanation("Failed to parse Gemini response.");
            return fallback;
        }
    }
}