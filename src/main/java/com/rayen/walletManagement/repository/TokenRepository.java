package com.rayen.walletManagement.repository;

import com.rayen.walletManagement.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByCustomerId(Long customerId);

    List<Token> findByWalletWalletId(Long walletId);

    @Query("SELECT SUM(t.value) FROM Token t WHERE t.customerId = :customerId")
    Double getTotalValueByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT SUM(t.value * t.conversionRate) FROM Token t WHERE t.wallet.walletId = :walletId")
    Double getTotalConvertedValueByWalletId(@Param("walletId") Long walletId);
}
