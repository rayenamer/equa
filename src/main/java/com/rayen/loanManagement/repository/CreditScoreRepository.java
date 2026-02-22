package com.rayen.loanManagement.repository;

import com.rayen.loanManagement.entity.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {
}
