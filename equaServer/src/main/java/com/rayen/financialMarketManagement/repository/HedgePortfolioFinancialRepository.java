package com.rayen.financialMarketManagement.repository;

import com.rayen.financialMarketManagement.entity.HedgePortfolioFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HedgePortfolioFinancialRepository extends JpaRepository<HedgePortfolioFinancial, Long> {
    Optional<HedgePortfolioFinancial> findByUserId(Long userId);
}
