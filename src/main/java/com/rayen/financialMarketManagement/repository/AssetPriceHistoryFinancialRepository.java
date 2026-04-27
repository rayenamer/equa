package com.rayen.financialMarketManagement.repository;

import com.rayen.financialMarketManagement.entity.AssetPriceHistoryFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssetPriceHistoryFinancialRepository extends JpaRepository<AssetPriceHistoryFinancial, Long> {
    List<AssetPriceHistoryFinancial> findByAssetIdOrderByRecordedAtAsc(Long assetId);
    List<AssetPriceHistoryFinancial> findAllByOrderByRecordedAtAsc();
}
