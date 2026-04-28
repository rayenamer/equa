package com.rayen.financialMarketManagement.repository;

import com.rayen.financialMarketManagement.entity.AssetFinancial;
import com.rayen.financialMarketManagement.entity.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetFinancialRepository extends JpaRepository<AssetFinancial, Long> {
    List<AssetFinancial> findByStatus(AssetStatus status);
    Optional<AssetFinancial> findByTicker(String ticker);
}
