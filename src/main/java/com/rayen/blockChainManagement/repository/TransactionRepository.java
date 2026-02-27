package com.rayen.blockChainManagement.repository;

import com.rayen.blockChainManagement.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Optional<Transaction> findByTransactionHash(String transactionHash);

    List<Transaction> findByFromWallet(String fromWallet);

    List<Transaction> findByToWallet(String toWallet);

    @Query("SELECT t FROM Transaction t WHERE t.fromWallet = :wallet OR t.toWallet = :wallet ORDER BY t.timestamp DESC")
    List<Transaction> findByWallet(@Param("wallet") String wallet);

    List<Transaction> findByStatus(String status);

    List<Transaction> findByTransactionType(String transactionType);

    List<Transaction> findByStatusAndTransactionType(String status, String transactionType);

    List<Transaction> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findAllByOrderByTimestampDesc();

    List<Transaction> findAllByOrderByAmountDesc();

    List<Transaction> findByAmountGreaterThanEqual(BigDecimal minAmount);

    @Query("SELECT t FROM Transaction t WHERE t.confirmationCount = 0 OR t.confirmationCount IS NULL ORDER BY t.timestamp ASC")
    List<Transaction> findUnconfirmedTransactions();

    // Find confirmed transactions
    @Query("SELECT t FROM Transaction t WHERE t.confirmationCount > 0 ORDER BY t.timestamp DESC")
    List<Transaction> findConfirmedTransactions();

    boolean existsByTransactionHash(String transactionHash);

    // Find transactions without a block (pending)
    @Query("SELECT t FROM Transaction t WHERE t.block IS NULL ORDER BY t.timestamp ASC")
    List<Transaction> findPendingTransactions();

    // Find transactions with a block (confirmed in blockchain)
    @Query("SELECT t FROM Transaction t WHERE t.block IS NOT NULL ORDER BY t.timestamp DESC")
    List<Transaction> findBlockchainTransactions();

    // Find transactions by validator node
    @Query("SELECT t FROM Transaction t WHERE t.validatorNode.nodeId = :nodeId")
    List<Transaction> findByValidatorNodeId(@Param("nodeId") Integer nodeId);

    // Find transactions created after a specific date
    List<Transaction> findByCreatedAtAfter(LocalDateTime date);

    // Count transactions by status
    long countByStatus(String status);

    // Count transactions by type
    long countByTransactionType(String transactionType);

    // Count transactions for a wallet
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromWallet = :wallet OR t.toWallet = :wallet")
    long countByWallet(@Param("wallet") String wallet);

    // Get total transaction amount by wallet (sent)
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.fromWallet = :wallet AND t.status = 'CONFIRMED'")
    BigDecimal getTotalSentByWallet(@Param("wallet") String wallet);

    // Get total transaction amount by wallet (received)
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.toWallet = :wallet AND t.status = 'CONFIRMED'")
    BigDecimal getTotalReceivedByWallet(@Param("wallet") String wallet);

    // Get total fees collected
    @Query("SELECT SUM(t.fee) FROM Transaction t WHERE t.status = 'CONFIRMED'")
    BigDecimal getTotalFeesCollected();

    // Find high-value transactions
    @Query("SELECT t FROM Transaction t WHERE t.amount >= :threshold ORDER BY t.amount DESC")
    List<Transaction> findHighValueTransactions(@Param("threshold") BigDecimal threshold);

    // Find transactions between two wallets
    @Query("SELECT t FROM Transaction t WHERE (t.fromWallet = :wallet1 AND t.toWallet = :wallet2) OR (t.fromWallet = :wallet2 AND t.toWallet = :wallet1) ORDER BY t.timestamp DESC")
    List<Transaction> findTransactionsBetweenWallets(@Param("wallet1") String wallet1, @Param("wallet2") String wallet2);

    // Get average transaction amount
    @Query("SELECT AVG(t.amount) FROM Transaction t WHERE t.status = 'CONFIRMED'")
    BigDecimal getAverageTransactionAmount();

    // Get average transaction fee
    @Query("SELECT AVG(t.fee) FROM Transaction t WHERE t.status = 'CONFIRMED' AND t.fee IS NOT NULL")
    BigDecimal getAverageTransactionFee();
}