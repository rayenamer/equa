package com.rayen.walletManagement.controller;

import com.rayen.walletManagement.service.CurrencyExchangeService;
import com.rayen.walletManagement.service.FraudDetectionService;
import com.rayen.walletManagement.service.LoyaltyRewardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/advanced")
@Tag(name = "Advanced Business Rules", description = "Multi-currency exchange, Loyalty rewards, Fraud detection")
public class AdvancedBusinessController {

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Autowired
    private LoyaltyRewardsService loyaltyRewardsService;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    // ==================== CURRENCY EXCHANGE ====================

    @PostMapping("/exchange/{walletId}")
    @Operation(summary = "Exchange wallet currency (EUR→USD, BTC, etc.)")
    public ResponseEntity<Map<String, Object>> exchangeCurrency(
            @PathVariable Long walletId,
            @RequestParam String targetCurrency,
            @RequestParam Double amount) {
        return ResponseEntity.ok(currencyExchangeService.exchangeCurrency(walletId, targetCurrency, amount));
    }

    @PostMapping("/exchange/cross-transfer")
    @Operation(summary = "Cross-currency transfer between two wallets")
    public ResponseEntity<Map<String, Object>> crossCurrencyTransfer(
            @RequestParam Long senderWalletId,
            @RequestParam Long recipientWalletId,
            @RequestParam Double amount) {
        return ResponseEntity.ok(currencyExchangeService.crossWalletTransfer(senderWalletId, recipientWalletId, amount));
    }

    @GetMapping("/exchange/rates")
    @Operation(summary = "Get all supported exchange rates")
    public ResponseEntity<Map<String, Object>> getExchangeRates() {
        return ResponseEntity.ok(currencyExchangeService.getExchangeRates());
    }

    @GetMapping("/exchange/preview")
    @Operation(summary = "Preview currency conversion (no actual exchange)")
    public ResponseEntity<Map<String, Object>> convertPreview(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam Double amount) {
        return ResponseEntity.ok(currencyExchangeService.convertPreview(from, to, amount));
    }

    // ==================== LOYALTY REWARDS ====================

    @PostMapping("/loyalty/{walletId}/earn")
    @Operation(summary = "Earn loyalty points from a transaction amount")
    public ResponseEntity<Map<String, Object>> earnPoints(
            @PathVariable Long walletId,
            @RequestParam Double transactionAmount) {
        return ResponseEntity.ok(loyaltyRewardsService.earnPoints(walletId, transactionAmount));
    }

    @PostMapping("/loyalty/{walletId}/redeem")
    @Operation(summary = "Redeem loyalty points for cash credit")
    public ResponseEntity<Map<String, Object>> redeemPoints(
            @PathVariable Long walletId,
            @RequestParam Integer points) {
        return ResponseEntity.ok(loyaltyRewardsService.redeemPoints(walletId, points));
    }

    @GetMapping("/loyalty/{walletId}")
    @Operation(summary = "Get loyalty program status (points, tier, benefits)")
    public ResponseEntity<Map<String, Object>> getLoyaltyStatus(@PathVariable Long walletId) {
        return ResponseEntity.ok(loyaltyRewardsService.getLoyaltyStatus(walletId));
    }

    // ==================== FRAUD DETECTION ====================

    @GetMapping("/fraud/{walletId}")
    @Operation(summary = "Run fraud detection analysis on wallet transactions")
    public ResponseEntity<Map<String, Object>> detectFraud(@PathVariable Long walletId) {
        return ResponseEntity.ok(fraudDetectionService.detectFraud(walletId));
    }
}
