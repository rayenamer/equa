package com.rayen.blockChainManagement.controller;

import com.rayen.blockChainManagement.entity.Transaction;
import com.rayen.blockChainManagement.entity.TransactionStatus;
import com.rayen.blockChainManagement.model.TransactionRequest;
import com.rayen.blockChainManagement.model.TransactionResponse;
import com.rayen.blockChainManagement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j

public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        log.info("REST request to create transaction");
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Integer id) {
        log.info("REST request to get transaction by ID: {}", id);
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        log.info("REST request to get all transactions");
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/from/{fromWallet}")
    public ResponseEntity<List<TransactionResponse>> getByFromWallet(@PathVariable String fromWallet) {
        log.info("REST request to get transactions from wallet: {}", fromWallet);
        return ResponseEntity.ok(transactionService.getByFromWallet(fromWallet));
    }

    @GetMapping("/to/{toWallet}")
    public ResponseEntity<List<TransactionResponse>> getByToWallet(@PathVariable String toWallet) {
        log.info("REST request to get transactions to wallet: {}", toWallet);
        return ResponseEntity.ok(transactionService.getByToWallet(toWallet));
    }

    @GetMapping("/wallet/{wallet}")
    public ResponseEntity<List<TransactionResponse>> getByWallet(@PathVariable String wallet) {
        log.info("REST request to get transactions for wallet: {}", wallet);
        return ResponseEntity.ok(transactionService.getByWallet(wallet));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>> getByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        log.info("REST request to get transactions between {} and {}", start, end);
        return ResponseEntity.ok(transactionService.getByDateRange(start, end));
    }

    @GetMapping("/ordered/timestamp")
    public ResponseEntity<List<TransactionResponse>> getAllOrderedByTimestamp() {
        log.info("REST request to get all transactions ordered by timestamp");
        return ResponseEntity.ok(transactionService.getAllOrderedByTimestamp());
    }

    @GetMapping("/ordered/amount")
    public ResponseEntity<List<TransactionResponse>> getAllOrderedByAmount() {
        log.info("REST request to get all transactions ordered by amount");
        return ResponseEntity.ok(transactionService.getAllOrderedByAmount());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponse>> getPendingTransactions() {
        log.info("REST request to get pending transactions");
        return ResponseEntity.ok(transactionService.getPendingTransactions());
    }

    @GetMapping("/blockchain")
    public ResponseEntity<List<TransactionResponse>> getBlockchainTransactions() {
        log.info("REST request to get blockchain transactions");
        return ResponseEntity.ok(transactionService.getBlockchainTransactions());
    }

    @GetMapping("/validator/{nodeId}")
    public ResponseEntity<List<TransactionResponse>> getByValidatorNodeId(@PathVariable Integer nodeId) {
        log.info("REST request to get transactions by validator node: {}", nodeId);
        return ResponseEntity.ok(transactionService.getByValidatorNodeId(nodeId));
    }

    @GetMapping("/created-after")
    public ResponseEntity<List<TransactionResponse>> getCreatedAfter(@RequestParam LocalDateTime date) {
        log.info("REST request to get transactions created after: {}", date);
        return ResponseEntity.ok(transactionService.getCreatedAfter(date));
    }

    @GetMapping("/high-value")
    public ResponseEntity<List<TransactionResponse>> getHighValueTransactions(@RequestParam BigDecimal threshold) {
        log.info("REST request to get high-value transactions above: {}", threshold);
        return ResponseEntity.ok(transactionService.getHighValueTransactions(threshold));
    }

    @GetMapping("/between-wallets")
    public ResponseEntity<List<TransactionResponse>> getTransactionsBetweenWallets(
            @RequestParam String wallet1,
            @RequestParam String wallet2) {
        log.info("REST request to get transactions between wallets: {} and {}", wallet1, wallet2);
        return ResponseEntity.ok(transactionService.getTransactionsBetweenWallets(wallet1, wallet2));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TransactionResponse> updateStatus(
            @PathVariable Integer id,
            @RequestParam TransactionStatus status) {
        log.info("REST request to update status of transaction {} to {}", id, status);
        return transactionService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        log.info("REST request to delete transaction: {}", id);
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count/wallet/{wallet}")
    public ResponseEntity<Long> countByWallet(@PathVariable String wallet) {
        log.info("REST request to count transactions for wallet: {}", wallet);
        return ResponseEntity.ok(transactionService.countByWallet(wallet));
    }

    @GetMapping("/stats/sent/{wallet}")
    public ResponseEntity<BigDecimal> getTotalSentByWallet(@PathVariable String wallet) {
        log.info("REST request to get total sent by wallet: {}", wallet);
        return ResponseEntity.ok(transactionService.getTotalSentByWallet(wallet));
    }

    @GetMapping("/stats/received/{wallet}")
    public ResponseEntity<BigDecimal> getTotalReceivedByWallet(@PathVariable String wallet) {
        log.info("REST request to get total received by wallet: {}", wallet);
        return ResponseEntity.ok(transactionService.getTotalReceivedByWallet(wallet));
    }

    @GetMapping("/stats/fees")
    public ResponseEntity<BigDecimal> getTotalFeesCollected() {
        log.info("REST request to get total fees collected");
        return ResponseEntity.ok(transactionService.getTotalFeesCollected());
    }

    @GetMapping("/stats/average-amount")
    public ResponseEntity<BigDecimal> getAverageTransactionAmount() {
        log.info("REST request to get average transaction amount");
        return ResponseEntity.ok(transactionService.getAverageTransactionAmount());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionResponse>> getByStatus(@PathVariable TransactionStatus status) {
        return ResponseEntity.ok(transactionService.getByStatus(status));
    }

    @GetMapping("/stats/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable TransactionStatus status) {
        return ResponseEntity.ok(transactionService.countByStatus(status));
    }
}
