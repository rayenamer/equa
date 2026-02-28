package com.rayen.blockChainManagement.service;


import com.rayen.blockChainManagement.entity.Transaction;
import com.rayen.blockChainManagement.entity.TransactionStatus;
import com.rayen.blockChainManagement.model.TransactionMapper;
import com.rayen.blockChainManagement.model.TransactionRequest;
import com.rayen.blockChainManagement.model.TransactionResponse;
import com.rayen.blockChainManagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;


    //26 letters × 10 digits = **260 possible combinations**
    private String generateHash() {
        Random random = new Random();
        char letter = (char) ('a' + random.nextInt(26));
        int number = random.nextInt(10);
        return String.valueOf(letter) + number;
    }

    // Create
    public TransactionResponse createTransaction(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .fromWallet(request.getFromWallet())
                .toWallet(request.getToWallet())
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now())
                .status(TransactionStatus.PENDING)
                .transactionHash(generateHash())
                .fee(request.getAmount().multiply(BigDecimal.valueOf(0.01)))
                .build();
        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created with id: {}", saved.getTransactionId());
        return transactionMapper.toResponse(saved);
    }

    // Get by ID
    public Optional<TransactionResponse> getTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponse);
    }

    // Get all
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get by fromWallet
    public List<TransactionResponse> getByFromWallet(String fromWallet) {
        return transactionRepository.findByFromWallet(fromWallet)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get by toWallet
    public List<TransactionResponse> getByToWallet(String toWallet) {
        return transactionRepository.findByToWallet(toWallet)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get by wallet (sent or received)
    public List<TransactionResponse> getByWallet(String wallet) {
        return transactionRepository.findByWallet(wallet)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get by date range
    public List<TransactionResponse> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByTimestampBetween(start, end)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get all ordered by timestamp desc
    public List<TransactionResponse> getAllOrderedByTimestamp() {
        return transactionRepository.findAllByOrderByTimestampDesc()
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get all ordered by amount desc
    public List<TransactionResponse> getAllOrderedByAmount() {
        return transactionRepository.findAllByOrderByAmountDesc()
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get pending transactions (no block)
    public List<TransactionResponse> getPendingTransactions() {
        return transactionRepository.findPendingTransactions()
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get confirmed blockchain transactions
    public List<TransactionResponse> getBlockchainTransactions() {
        return transactionRepository.findBlockchainTransactions()
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get by validator node
    public List<TransactionResponse> getByValidatorNodeId(Integer nodeId) {
        return transactionRepository.findByValidatorNodeId(nodeId)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get transactions after date
    public List<TransactionResponse> getCreatedAfter(LocalDateTime date) {
        return transactionRepository.findByCreatedAtAfter(date)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get high-value transactions
    public List<TransactionResponse> getHighValueTransactions(BigDecimal threshold) {
        return transactionRepository.findHighValueTransactions(threshold)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get transactions between two wallets
    public List<TransactionResponse> getTransactionsBetweenWallets(String wallet1, String wallet2) {
        return transactionRepository.findTransactionsBetweenWallets(wallet1, wallet2)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Update status
    public Optional<TransactionResponse> updateStatus(Integer id, TransactionStatus status) {
        return transactionRepository.findById(id).map(transaction -> {
            transaction.setStatus(status);
            Transaction updated = transactionRepository.save(transaction);
            log.info("Transaction {} status updated to {}", id, status);
            return transactionMapper.toResponse(updated);
        });
    }

    // Delete
    public void deleteTransaction(Integer id) {
        transactionRepository.deleteById(id);
        log.info("Transaction {} deleted", id);
    }

    public long countByWallet(String wallet) {
        return transactionRepository.countByWallet(wallet);
    }

    public BigDecimal getTotalSentByWallet(String wallet) {
        return transactionRepository.getTotalSentByWallet(wallet);
    }

    public BigDecimal getTotalReceivedByWallet(String wallet) {
        return transactionRepository.getTotalReceivedByWallet(wallet);
    }

    public BigDecimal getTotalFeesCollected() {
        return transactionRepository.getTotalFeesCollected();
    }

    public BigDecimal getAverageTransactionAmount() {
        return transactionRepository.getAverageTransactionAmount();
    }

    public List<TransactionResponse> getByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status)
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public long countByStatus(TransactionStatus status) {
        return transactionRepository.countByStatus(status);
    }
}
