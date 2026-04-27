package com.rayen.financialMarketManagement.controller;

import com.rayen.AuthContextService;
import com.rayen.financialMarketManagement.dto.*;
import com.rayen.financialMarketManagement.service.TradeFinancialService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/financial/trade")
@RequiredArgsConstructor
public class TradeFinancialController {

    private final TradeFinancialService tradeService;
    private final AuthContextService authContextService;
    @PostMapping("/buy")
    public ResponseEntity<TradeResponseFinancial> buy(
            @RequestBody TradeRequestFinancial req) throws BadRequestException {

        String userId = authContextService.getLoggedInUserId().toString();
        return ResponseEntity.ok(tradeService.buy(req, Long.valueOf(userId)));
    }

    @PostMapping("/sell")
    public ResponseEntity<TradeResponseFinancial> sell(
            @RequestBody TradeRequestFinancial req) {

        String userId = authContextService.getLoggedInUserId().toString();
        return ResponseEntity.ok(tradeService.sell(req, Long.valueOf(userId)));
    }
}
