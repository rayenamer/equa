package com.rayen.financialMarketManagement.controller;

import com.rayen.financialMarketManagement.service.FinancialMarketAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/financial/ai-insights")
@RequiredArgsConstructor
public class FinancialMarketAnalysisController {

    private final FinancialMarketAnalysisService financialMarketAnalysisService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMarketInsights() {
        return ResponseEntity.ok(financialMarketAnalysisService.analyzeMarket());
    }
}
