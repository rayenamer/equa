package com.rayen.walletManagement.repository;

import com.rayen.walletManagement.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(w.equaAmount) FROM Wallet w")
    Double sumTotalEqua();
}
