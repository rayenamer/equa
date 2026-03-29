package com.rayen.walletManagement.repository;

import com.rayen.walletManagement.entity.CashbackReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CashbackRewardRepository extends JpaRepository<CashbackReward, Long> {

    List<CashbackReward> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    List<CashbackReward> findByWalletIdAndRewardType(Long walletId, String rewardType);

    @Query("SELECT COALESCE(SUM(c.cashbackAmount), 0) FROM CashbackReward c WHERE c.walletId = :walletId")
    Double getTotalCashbackByWalletId(@Param("walletId") Long walletId);

    @Query("SELECT COUNT(c) FROM CashbackReward c WHERE c.walletId = :walletId AND c.createdAt >= :since")
    Long countTransactionsSince(@Param("walletId") Long walletId, @Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(c.transactionAmount), 0) FROM CashbackReward c WHERE c.walletId = :walletId AND c.createdAt >= :since")
    Double getTotalVolumeSince(@Param("walletId") Long walletId, @Param("since") LocalDateTime since);
}
