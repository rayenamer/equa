package com.rayen.financialMarketManagement.repository;

import com.rayen.financialMarketManagement.entity.FinancialMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialMarketRepository extends JpaRepository<FinancialMarket, Integer> {

    List<FinancialMarket> findByMarketName(String marketName);
}
