package com.rayen.financialMarketManagement.controller;

import com.rayen.AuthContextService;
import com.rayen.financialMarketManagement.dto.*;
import com.rayen.financialMarketManagement.service.PortfolioFinancialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/financial")
@RequiredArgsConstructor
public class PortfolioFinancialController {

    private final PortfolioFinancialService portfolioService;

    private final AuthContextService authContextService;

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponseFinancial> portfolio() {

        String userId = authContextService.getLoggedInUserId().toString();
        return ResponseEntity.ok(portfolioService.getPortfolio(Long.valueOf(userId)));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseFinancial>> transactions() {

        String userId = authContextService.getLoggedInUserId().toString();
        return ResponseEntity.ok(portfolioService.getTransactions(Long.valueOf(userId)));
    }
}
