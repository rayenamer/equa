package com.rayen.financialMarketManagement.repository;

import com.rayen.financialMarketManagement.entity.HedgeTransactionFinancial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HedgeTransactionFinancialRepository extends JpaRepository<HedgeTransactionFinancial, Long> {
    List<HedgeTransactionFinancial> findByUserIdOrderByCreatedAtDesc(Long userId);
}
