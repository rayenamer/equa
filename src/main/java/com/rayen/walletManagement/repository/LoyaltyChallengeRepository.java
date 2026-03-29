package com.rayen.walletManagement.repository;

import com.rayen.walletManagement.entity.LoyaltyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoyaltyChallengeRepository extends JpaRepository<LoyaltyChallenge, Long> {

    List<LoyaltyChallenge> findByActiveTrue();

    List<LoyaltyChallenge> findByChallengeTypeAndActiveTrue(String challengeType);
}
