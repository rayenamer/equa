package com.rayen.financialMarketManagement.controller;

import com.rayen.financialMarketManagement.entity.FinancialMarket;
import com.rayen.financialMarketManagement.service.FinancialMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/financial-markets")
@RequiredArgsConstructor
@Slf4j
public class FinancialMarketController {

    private final FinancialMarketService financialMarketService;

    @PostMapping
    public ResponseEntity<FinancialMarket> createFinancialMarket(@RequestBody FinancialMarket financialMarket) {
        log.info("REST request to create financial market");
        FinancialMarket created = financialMarketService.createFinancialMarket(financialMarket);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialMarket> getFinancialMarketById(@PathVariable Integer id) {
        log.info("REST request to get financial market by ID: {}", id);
        FinancialMarket financialMarket = financialMarketService.getFinancialMarketById(id);
        return ResponseEntity.ok(financialMarket);
    }

    @GetMapping
    public ResponseEntity<List<FinancialMarket>> getAllFinancialMarkets() {
        log.info("REST request to get all financial markets");
        List<FinancialMarket> markets = financialMarketService.getAllFinancialMarkets();
        return ResponseEntity.ok(markets);
    }

    @GetMapping("/name/{marketName}")
    public ResponseEntity<List<FinancialMarket>> getFinancialMarketsByName(@PathVariable String marketName) {
        log.info("REST request to get financial markets by name: {}", marketName);
        List<FinancialMarket> markets = financialMarketService.getFinancialMarketsByName(marketName);
        return ResponseEntity.ok(markets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialMarket> updateFinancialMarket(@PathVariable Integer id, @RequestBody FinancialMarket financialMarket) {
        log.info("REST request to update financial market with ID: {}", id);
        FinancialMarket updated = financialMarketService.updateFinancialMarket(id, financialMarket);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinancialMarket(@PathVariable Integer id) {
        log.info("REST request to delete financial market with ID: {}", id);
        financialMarketService.deleteFinancialMarket(id);
        return ResponseEntity.noContent().build();
    }
}
