package com.rayen.walletManagement.controller;

import com.rayen.walletManagement.model.WalletDTO;
import com.rayen.walletManagement.service.AnalyticsService;
import com.rayen.walletManagement.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics & Export", description = "Dashboard analytics, scheduled reports, and CSV export")
public class AnalyticsExportController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private WalletService walletService;

    // ==================== ANALYTICS ====================

    @GetMapping("/report")
    @Operation(summary = "Get full platform analytics report")
    public ResponseEntity<Map<String, Object>> getFullReport() {
        return ResponseEntity.ok(analyticsService.generateFullReport());
    }

    @GetMapping("/report/latest")
    @Operation(summary = "Get latest scheduled analytics report")
    public ResponseEntity<Map<String, Object>> getLatestReport() {
        return ResponseEntity.ok(analyticsService.getLatestReport());
    }

    @GetMapping("/wallet/{walletId}")
    @Operation(summary = "Get detailed analytics for a specific wallet")
    public ResponseEntity<Map<String, Object>> getWalletAnalytics(@PathVariable Long walletId) {
        return ResponseEntity.ok(analyticsService.getWalletAnalytics(walletId));
    }

    // ==================== CSV EXPORT ====================

    @GetMapping(value = "/export/wallets", produces = "text/csv")
    @Operation(summary = "Export all wallets data as CSV file")
    public ResponseEntity<String> exportWalletsCsv() {
        List<WalletDTO> wallets = walletService.getAllWallets();

        StringBuilder csv = new StringBuilder();
        csv.append("walletId,customerId,balance,currency,status,loyaltyPoints,loyaltyTier,publicKey,tokensCount,assetsCount,transactionsCount,createdAt\n");

        for (WalletDTO w : wallets) {
            csv.append(w.getWalletId()).append(",");
            csv.append(w.getCustomerId()).append(",");
            csv.append(w.getBalance()).append(",");
            csv.append(w.getCurrency() != null ? w.getCurrency() : "EUR").append(",");
            csv.append(w.getStatus()).append(",");
            csv.append(w.getLoyaltyPoints() != null ? w.getLoyaltyPoints() : 0).append(",");
            csv.append(w.getLoyaltyTier() != null ? w.getLoyaltyTier() : "BRONZE").append(",");
            csv.append(w.getPublicKey()).append(",");
            csv.append(w.getTokens() != null ? w.getTokens().size() : 0).append(",");
            csv.append(w.getAssets() != null ? w.getAssets().size() : 0).append(",");
            csv.append(w.getTransactionHistory() != null ? w.getTransactionHistory().size() : 0).append(",");
            csv.append(w.getCreatedAt() != null ? w.getCreatedAt().toString() : "").append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wallets_export.csv");

        return ResponseEntity.ok().headers(headers).body(csv.toString());
    }

    @GetMapping(value = "/export/transactions/{walletId}", produces = "text/csv")
    @Operation(summary = "Export transaction history for a wallet as CSV")
    public ResponseEntity<String> exportTransactionsCsv(@PathVariable Long walletId) {
        WalletDTO wallet = walletService.getWalletById(walletId);

        StringBuilder csv = new StringBuilder();
        csv.append("walletId,customerId,transaction\n");

        if (wallet.getTransactionHistory() != null) {
            for (String tx : wallet.getTransactionHistory()) {
                csv.append(wallet.getWalletId()).append(",");
                csv.append(wallet.getCustomerId()).append(",");
                csv.append("\"").append(tx.replace("\"", "\"\"")).append("\"\n");
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions_wallet_" + walletId + ".csv");

        return ResponseEntity.ok().headers(headers).body(csv.toString());
    }
}
