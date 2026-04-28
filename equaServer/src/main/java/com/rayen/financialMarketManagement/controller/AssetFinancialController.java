package com.rayen.financialMarketManagement.controller;

import com.rayen.AuthContextService;
import com.rayen.financialMarketManagement.dto.*;
import com.rayen.financialMarketManagement.service.AssetFinancialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/financial/assets")
@RequiredArgsConstructor
public class AssetFinancialController {

    private final AssetFinancialService assetService;
    private final AuthContextService authContextService;

    @GetMapping
    public ResponseEntity<List<AssetResponseFinancial>> getAll() {
        return ResponseEntity.ok(assetService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponseFinancial> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AssetResponseFinancial> create(
            @RequestBody AssetRequestFinancial req) {

        String userId = authContextService.getLoggedInUserId().toString();
        return ResponseEntity.ok(assetService.create(req, Long.valueOf(userId)));
    }

    @GetMapping("/{id}/price-history")
    public ResponseEntity<List<PriceHistoryResponseFinancial>> priceHistory(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getPriceHistory(id));
    }
    @GetMapping("/aggregated")
    public ResponseEntity<List<PriceHistoryResponseFinancial>> getAggregatedPriceHistory() {
        return ResponseEntity.ok(assetService.getAggregatedPriceHistory());
    }
    @GetMapping("/market-summary")
    public ResponseEntity<AssetMarketSummaryResponse> getMarketSummary() {
        return ResponseEntity.ok(assetService.getMarketSummary());
    }
}
