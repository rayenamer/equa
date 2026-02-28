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
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    private void validateTransaction(Integer transactionId) throws BadRequestException {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BadRequestException("Transaction not found: " + transactionId));

        List<Node> nodes = nodeRepository.findAll();

        // Pick a random node as the validator
        Node validatorNode = nodes.get(new Random().nextInt(nodes.size()));
        validatorNode.setReputationScore(validatorNode.getReputationScore() - 1);
        nodeRepository.save(validatorNode);

        transaction.setStatus(TransactionStatus.VALID);
        transaction.setValidatorNode(validatorNode);
        transactionRepository.save(transaction);
    }

    private void addToBlock(Integer transactionId){
        Block block = blockRepository.findLatestBlock()
                .orElseThrow(() -> new IllegalStateException("No blocks found in blockchain"));
        if(block.getBlockSize()==0){
            Block newBlock = blockService.generateBlock();
            blockService.addTransactionToBlock(transactionId,newBlock.getBlockId());
        }
        else{
            blockService.addTransactionToBlock(transactionId,block.getBlockId());
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
                .collect(Collectors.toList()); // ← fix: mutable list, not .toList()

        List<Node> nodes = nodeRepository.findAll();
        nodes.forEach(node -> node.setBlockchainRecord(blockchainJson));
        nodeRepository.saveAll(nodes);
    }

    public TransactionResponse processTransaction(TransactionRequest request) throws BadRequestException, JsonProcessingException {
        TransactionResponse response = transactionService.createTransaction(request);
        //TODO : VALIDATE TOKENS NUMBER FROM WALLETS
        validateTransaction(response.getTransactionId());
        addToBlock(response.getTransactionId());
        updateNodes();
        return response;
    }
    public List<Node> getAllNodesWithBlockchain() {
        return nodeRepository.findAll();
    }



}
