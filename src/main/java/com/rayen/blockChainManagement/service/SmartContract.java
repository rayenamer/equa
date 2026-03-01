package com.rayen.blockChainManagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rayen.blockChainManagement.entity.Block;
import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.entity.Transaction;
import com.rayen.blockChainManagement.entity.TransactionStatus;
import com.rayen.blockChainManagement.model.BlockDTO;
import com.rayen.blockChainManagement.model.BlockMAPPER;
import com.rayen.blockChainManagement.model.TransactionRequest;
import com.rayen.blockChainManagement.model.TransactionResponse;
import com.rayen.blockChainManagement.repository.BlockRepository;
import com.rayen.blockChainManagement.repository.NodeRepository;
import com.rayen.blockChainManagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContract {
    private final NodeRepository nodeRepository;
    private final TransactionRepository transactionRepository;
    private  final BlockRepository blockRepository;
    private final BlockService blockService;
    private  final TransactionService transactionService;

    private String guessHash() {
        Random random = new Random();
        char letter = (char) ('a' + random.nextInt(6)); // a, b, c, d, e, f
        return String.valueOf(letter);
    }

    private void validateTransaction(Integer transactionId) throws BadRequestException, InterruptedException, ExecutionException {
        log.info("================================================================ VALIDATION START ================================================================");
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BadRequestException("Transaction not found: " + transactionId));
        List<Node> nodes = nodeRepository.findAll();
        String target = transaction.getTransactionHash();

        log.info("🎯 Target hash: '{}' | {} nodes competing", target, nodes.size());
        log.info("---------------------------------------------------------------- RACE BEGINS -----------------------------------------------------------------");

        ExecutorService executor = Executors.newFixedThreadPool(nodes.size());
        AtomicBoolean found = new AtomicBoolean(false);

        List<Future<Node>> futures = nodes.stream().map(node -> executor.submit(() -> {
            int round = 0;
            while (!found.get()) {
                round++;
                long wait = node.getReputationScore().longValue();
                log.info("⏳ [Node {}] round {} | waiting {}ms (reputation: {})", node.getNodeId(), round, wait, node.getReputationScore());
                Thread.sleep(wait);

                String guess = guessHash();
                log.info("🎲 [Node {}] round {} | guessed '{}' vs target '{}' → {}", node.getNodeId(), round, guess, target, guess.equals(target) ? "✅ CORRECT" : "❌ wrong");

                if (guess.equals(target) && found.compareAndSet(false, true)) {
                    log.info("🏆 [Node {}] WON on round {} | total wait: {}ms", node.getNodeId(), round, wait * round);
                    return node;
                }
            }
            log.info("🛑 [Node {}] stopped — another node won", node.getNodeId());
            return null;
        })).toList();

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        Node winner = futures.stream()
                .map(f -> { try { return f.get(); } catch (Exception e) { return null; } })
                .filter(n -> n != null)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Validation failed unexpectedly"));

        log.info("---------------------------------------------------------------- RACE ENDS -------------------------------------------------------------------");
        log.info("✅ Transaction {} validated by Node {} | reputation: {} → {}", transactionId, winner.getNodeId(), winner.getReputationScore(), winner.getReputationScore() - 1);
        log.info("================================================================ VALIDATION END ==============================================================");

        if(winner.getReputationScore()==0){
            winner.setReputationScore(50.0);
        }else{
            winner.setReputationScore(winner.getReputationScore() - 0.5);
        }
        nodeRepository.save(winner);
        transaction.setStatus(TransactionStatus.VALID);
        transaction.setValidatorNode(winner);
        transactionRepository.save(transaction);
    }

    private void addToBlock(Integer transactionId){
        Block block = blockRepository.findLatestBlock()
                .orElseThrow(() -> new IllegalStateException("No blocks found in blockchain"));
        if(block.getBlockSize()==0){
            Block newBlock = blockService.generateBlock();
            blockService.addTransactionToBlock(newBlock.getBlockId(), transactionId);
            newBlock.setBlockSize(newBlock.getBlockSize()-1);
            blockRepository.save(newBlock);
        }
        else{
            blockService.addTransactionToBlock(block.getBlockId(), transactionId);
            block.setBlockSize(block.getBlockSize()-1);
            blockRepository.save(block);
        }
    }

    private void updateNodes() throws JsonProcessingException {
        List<Block> blockchainRecord = blockRepository.findAllBlocksWithTransactions();

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        List<String> blockchainJson = blockchainRecord.stream()
                .map(block -> {
                    try {
                        BlockDTO blockDTO = BlockMAPPER.toBlockDTO(block);
                        return objectMapper.writeValueAsString(blockDTO);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to serialize block: " + block.getBlockId(), e);
                    }
                })
                .collect(Collectors.toList());

        List<Node> nodes = nodeRepository.findAll();
        nodes.forEach(node -> node.setBlockchainRecord(blockchainJson));
        nodeRepository.saveAll(nodes);
    }

    public TransactionResponse processTransaction(TransactionRequest request) throws BadRequestException, JsonProcessingException, ExecutionException, InterruptedException {
        TransactionResponse response = transactionService.createTransaction(request);
        //TODO : VALIDATE TOKEN BALANCE FROM WALLETS
        validateTransaction(response.getTransactionId());
        addToBlock(response.getTransactionId());
        updateNodes();
        return response;
    }
    public List<Node> getAllNodesWithBlockchain() {
        return nodeRepository.findAll();
    }



}
