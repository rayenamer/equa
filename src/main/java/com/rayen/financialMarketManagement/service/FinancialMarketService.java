package com.rayen.financialMarketManagement.service;

import com.rayen.financialMarketManagement.entity.FinancialMarket;
import com.rayen.financialMarketManagement.repository.FinancialMarketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialMarketService {

    private final FinancialMarketRepository financialMarketRepository;

    @Transactional
    public FinancialMarket createFinancialMarket(FinancialMarket financialMarket) {
        log.info("Creating new financial market: {}", financialMarket.getMarketName());
        return financialMarketRepository.save(financialMarket);
    }

    @Transactional(readOnly = true)
    public FinancialMarket getFinancialMarketById(Integer marketId) {
        return financialMarketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Financial market not found with ID: " + marketId));
    }

    @Transactional(readOnly = true)
    public List<FinancialMarket> getAllFinancialMarkets() {
        return financialMarketRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<FinancialMarket> getFinancialMarketsByName(String marketName) {
        return financialMarketRepository.findByMarketName(marketName);
    }

    @Transactional
    public FinancialMarket updateFinancialMarket(Integer marketId, FinancialMarket updatedMarket) {
        FinancialMarket existing = financialMarketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Financial market not found with ID: " + marketId));

        existing.setMarketName(updatedMarket.getMarketName());
        existing.setExchangeRate(updatedMarket.getExchangeRate());
        existing.setAvailableAssets(updatedMarket.getAvailableAssets());
        

        log.info("Updating financial market with ID: {}", marketId);
        return financialMarketRepository.save(existing);
    }

    @Transactional
    public void deleteFinancialMarket(Integer marketId) {
        if (!financialMarketRepository.existsById(marketId)) {
            throw new IllegalArgumentException("Financial market not found with ID: " + marketId);
        }
        log.info("Deleting financial market with ID: {}", marketId);
        financialMarketRepository.deleteById(marketId);
    }
}
