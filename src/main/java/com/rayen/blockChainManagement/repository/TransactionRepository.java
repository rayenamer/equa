package com.rayen.blockChainManagement.repository;

import com.rayen.blockChainManagement.entity.Transaction;
import com.rayen.blockChainManagement.entity.TransactionStatus;
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

    List<Transaction> findByFromWallet(String fromWallet);

    List<Transaction> findByToWallet(String toWallet);

    @Query("SELECT t FROM Transaction t WHERE t.fromWallet = :wallet OR t.toWallet = :wallet ORDER BY t.timestamp DESC")
    List<Transaction> findByWallet(@Param("wallet") String wallet);

    List<Transaction> findByStatus(TransactionStatus status);

     List<Transaction> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findAllByOrderByTimestampDesc();

    List<Transaction> findAllByOrderByAmountDesc();

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
    @Query("SELECT t FROM Transaction t WHERE t.timestamp > :date ORDER BY t.timestamp DESC")
    List<Transaction> findByCreatedAtAfter(@Param("date") LocalDateTime date);

    // Count transactions by status
    long countByStatus(TransactionStatus status);

    // Count transactions for a wallet
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromWallet = :wallet OR t.toWallet = :wallet")
    long countByWallet(@Param("wallet") String wallet);

    // Find high-value transactions
    @Query("SELECT t FROM Transaction t WHERE t.amount >= :threshold ORDER BY t.amount DESC")
    List<Transaction> findHighValueTransactions(@Param("threshold") BigDecimal threshold);

    // Find transactions between two wallets
    @Query("SELECT t FROM Transaction t WHERE (t.fromWallet = :wallet1 AND t.toWallet = :wallet2) OR (t.fromWallet = :wallet2 AND t.toWallet = :wallet1) ORDER BY t.timestamp DESC")
    List<Transaction> findTransactionsBetweenWallets(@Param("wallet1") String wallet1, @Param("wallet2") String wallet2);


    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.fromWallet = :wallet AND t.status = com.rayen.blockChainManagement.entity.TransactionStatus.VALID")
    BigDecimal getTotalSentByWallet(@Param("wallet") String wallet);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.toWallet = :wallet AND t.status = com.rayen.blockChainManagement.entity.TransactionStatus.VALID")
    BigDecimal getTotalReceivedByWallet(@Param("wallet") String wallet);

    @Query("SELECT SUM(t.fee) FROM Transaction t WHERE t.status = com.rayen.blockChainManagement.entity.TransactionStatus.VALID")
    BigDecimal getTotalFeesCollected();

    @Query("SELECT AVG(t.amount) FROM Transaction t WHERE t.status = com.rayen.blockChainManagement.entity.TransactionStatus.VALID")
    BigDecimal getAverageTransactionAmount();
}