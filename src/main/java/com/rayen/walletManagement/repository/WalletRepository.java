package com.rayen.walletManagement.repository;

import com.rayen.walletManagement.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByCustomerId(Long customerId);

    Optional<Wallet> findByPublicKey(String publicKey);

    List<Wallet> findByStatus(String status);

    @Query("SELECT w FROM Wallet w WHERE w.balance >= :minBalance")
    List<Wallet> findByMinBalance(@Param("minBalance") Double minBalance);

    @Query("SELECT w FROM Wallet w WHERE w.balance < :threshold AND w.status = 'ACTIVE'")
    List<Wallet> findLowBalanceWallets(@Param("threshold") Double threshold);

    boolean existsByCustomerId(Long customerId);
}
