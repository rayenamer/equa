package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.entity.Block;
import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.repository.BlockRepository;
import com.rayen.blockChainManagement.repository.NodeRepository;
import com.rayen.blockChainManagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockchainAnalysisService {

    private final NodeRepository nodeRepository;
    private final BlockRepository blockRepository;
    private final TransactionRepository transactionRepository;
    private final GeminiService geminiService;

    public String analyzeBlockchainState() {
        List<Node> nodes = nodeRepository.findAll();
        List<Block> blocks = blockRepository.findAllBlocksWithTransactions();
        long totalTransactions = transactionRepository.count();

        // Build context for Gemini
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a blockchain analyst. Analyze this blockchain state and give a concise, insightful summary in 3-4 sentences.\n\n");
        prompt.append("=== BLOCKCHAIN STATE ===\n");
        prompt.append("Total blocks: ").append(blocks.size()).append("\n");
        prompt.append("Total transactions: ").append(totalTransactions).append("\n\n");

        prompt.append("=== NODES ===\n");
        nodes.forEach(node -> prompt.append(String.format(
                "Node %d | type: %s | location: %s | reputation: %.1f | status: %s | validations: %d\n",
                node.getNodeId(), node.getNodeType(), node.getLocation(),
                node.getReputationScore(),  node.getStatus(),
                node.getTransactions() != null ? node.getTransactions().size() : 0
        )));

        prompt.append("\n=== BLOCKS ===\n");
        blocks.forEach(block -> prompt.append(String.format(
                "Block %d | hash: %s | size remaining: %d | transactions: %d\n",
                block.getBlockId(), block.getBlockHash(),
                block.getBlockSize(),
                block.getTransaction() != null ? block.getTransaction().size() : 0
        )));

        prompt.append("\nDescribe the health, activity, and notable patterns of this blockchain.");

        return geminiService.analyzeBlockchain(prompt.toString());
    }
}
