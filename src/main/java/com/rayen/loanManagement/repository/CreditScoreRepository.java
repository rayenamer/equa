package com.rayen.loanManagement.repository;

import com.rayen.loanManagement.entity.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {

    Optional<CreditScore> findByUserId(Long userId);
}
