package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.entity.Block;
import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.repository.BlockRepository;
import com.rayen.blockChainManagement.repository.NodeRepository;
import com.rayen.blockChainManagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Service
@RequiredArgsConstructor
public class BlockchainChatService {

    private final GeminiService geminiService;
    private final NodeRepository nodeRepository;
    private final BlockRepository blockRepository;
    private final TransactionRepository transactionRepository;

    private final Map<String, List<String>> sessions = new ConcurrentHashMap<>();

    public String chat(String sessionId, String userMessage) {
        List<String> history = sessions.computeIfAbsent(sessionId, k -> {
            List<String> h = new ArrayList<>();
            h.add(buildContext()); // index 0 → role "user"
            h.add("Understood. I have full knowledge of this blockchain and am ready to answer your questions."); // index 1 → role "model"
            return h;
        });

        history.add(userMessage);
        String response = geminiService.chat(history);
        history.add(response);
        return response;
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    private String buildContext() {
        List<Node> nodes = nodeRepository.findAll();
        List<Block> blocks = blockRepository.findAllBlocksWithTransactions();
        long totalTransactions = transactionRepository.count();

        StringBuilder ctx = new StringBuilder();
        ctx.append("You are a blockchain analyst assistant with deep knowledge of this specific blockchain. ");
        ctx.append("Answer all questions accurately based on the data below. Be concise but insightful.\n\n");

        ctx.append("=== BLOCKCHAIN STATE ===\n");
        ctx.append("Total blocks: ").append(blocks.size()).append("\n");
        ctx.append("Total transactions: ").append(totalTransactions).append("\n\n");

        ctx.append("=== NODES ===\n");
        nodes.forEach(node -> ctx.append(String.format(
                "Node %d | type: %s | location: %s | reputation: %.1f | status: %s | validations: %d\n",
                node.getNodeId(), node.getNodeType(), node.getLocation(),
                node.getReputationScore(), node.getStatus(),
                node.getTransactions() != null ? node.getTransactions().size() : 0
        )));

        ctx.append("\n=== BLOCKS ===\n");
        blocks.forEach(block -> ctx.append(String.format(
                "Block %d | hash: %s | size remaining: %d | transactions: %d\n",
                block.getBlockId(), block.getBlockHash(),
                block.getBlockSize(),
                block.getTransaction() != null ? block.getTransaction().size() : 0
        )));

        ctx.append("\nYou have full knowledge of this blockchain. Answer any question the user asks about it.");
        return ctx.toString();
    }
}