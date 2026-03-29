package com.rayen.walletManagement.repository;

import com.rayen.walletManagement.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByWalletIdOrderByEarnedAtDesc(Long walletId);

    List<UserBadge> findByWalletIdAndCategory(Long walletId, String category);

    boolean existsByWalletIdAndBadgeName(Long walletId, String badgeName);

    boolean existsByWalletIdAndChallengeId(Long walletId, Long challengeId);

    Long countByWalletId(Long walletId);
}
