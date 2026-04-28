package com.rayen.financialMarketManagement.repository;

import com.rayen.financialMarketManagement.entity.HedgePortfolioItemFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HedgePortfolioItemFinancialRepository extends JpaRepository<HedgePortfolioItemFinancial, Long> {
    Optional<HedgePortfolioItemFinancial> findByPortfolioIdAndAssetId(Long portfolioId, Long assetId);
}
