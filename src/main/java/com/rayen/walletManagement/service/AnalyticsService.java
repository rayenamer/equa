package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.repository.AssetRepository;
import com.rayen.walletManagement.repository.TokenRepository;
import com.rayen.walletManagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AssetRepository assetRepository;

    private Map<String, Object> latestReport = new HashMap<>();

    // ==================== SCHEDULED ANALYTICS (every 5 minutes) ====================

    @Scheduled(fixedRate = 300000)
    public void generatePeriodicReport() {
        latestReport = generateFullReport();
        System.out.println("[ANALYTICS] Report generated at " + LocalDateTime.now());
    }

    // ==================== ON-DEMAND ANALYTICS ====================

    public Map<String, Object> generateFullReport() {
        List<Wallet> wallets = walletRepository.findAll();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportDate", LocalDateTime.now().toString());
        report.put("totalWallets", wallets.size());

        // Balance statistics
        DoubleSummaryStatistics balanceStats = wallets.stream()
                .mapToDouble(Wallet::getBalance)
                .summaryStatistics();

        Map<String, Object> balanceReport = new LinkedHashMap<>();
        balanceReport.put("totalBalance", balanceStats.getSum());
        balanceReport.put("averageBalance", Math.round(balanceStats.getAverage() * 100.0) / 100.0);
        balanceReport.put("maxBalance", balanceStats.getMax());
        balanceReport.put("minBalance", balanceStats.getMin());
        report.put("balanceStatistics", balanceReport);

        // Wallet status distribution
        Map<String, Long> statusDistribution = wallets.stream()
                .collect(Collectors.groupingBy(Wallet::getStatus, Collectors.counting()));
        report.put("statusDistribution", statusDistribution);

        // Currency distribution
        Map<String, Long> currencyDistribution = wallets.stream()
                .collect(Collectors.groupingBy(Wallet::getCurrency, Collectors.counting()));
        report.put("currencyDistribution", currencyDistribution);

        // Loyalty tier distribution
        Map<String, Long> tierDistribution = wallets.stream()
                .collect(Collectors.groupingBy(Wallet::getLoyaltyTier, Collectors.counting()));
        report.put("loyaltyTierDistribution", tierDistribution);

        // Transaction activity
        int totalTransactions = wallets.stream()
                .mapToInt(w -> w.getTransactionHistory() != null ? w.getTransactionHistory().size() : 0)
                .sum();
        report.put("totalTransactions", totalTransactions);

        double avgTransactions = wallets.isEmpty() ? 0 :
                (double) totalTransactions / wallets.size();
        report.put("avgTransactionsPerWallet", Math.round(avgTransactions * 100.0) / 100.0);

        // Top 5 wallets by balance
        List<Map<String, Object>> topWallets = wallets.stream()
                .sorted(Comparator.comparingDouble(Wallet::getBalance).reversed())
                .limit(5)
                .map(w -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("walletId", w.getWalletId());
                    m.put("customerId", w.getCustomerId());
                    m.put("balance", w.getBalance());
                    m.put("currency", w.getCurrency());
                    m.put("loyaltyTier", w.getLoyaltyTier());
                    return m;
                })
                .collect(Collectors.toList());
        report.put("topWalletsByBalance", topWallets);

        // Low balance wallets (< 100)
        long lowBalanceCount = wallets.stream()
                .filter(w -> w.getBalance() < 100)
                .count();
        report.put("lowBalanceWallets", lowBalanceCount);

        // Asset summary
        Map<String, Object> assetSummary = new LinkedHashMap<>();
        assetSummary.put("totalAssets", assetRepository.count());
        report.put("assetSummary", assetSummary);

        // Token summary
        Map<String, Object> tokenSummary = new LinkedHashMap<>();
        tokenSummary.put("totalTokens", tokenRepository.count());
        report.put("tokenSummary", tokenSummary);

        return report;
    }

    public Map<String, Object> getWalletAnalytics(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("walletId", walletId);
        analytics.put("customerId", wallet.getCustomerId());
        analytics.put("balance", wallet.getBalance());
        analytics.put("currency", wallet.getCurrency());
        analytics.put("status", wallet.getStatus());
        analytics.put("loyaltyTier", wallet.getLoyaltyTier());
        analytics.put("loyaltyPoints", wallet.getLoyaltyPoints());

        List<String> history = wallet.getTransactionHistory();
        if (history != null && !history.isEmpty()) {
            Map<String, Long> transactionTypes = new LinkedHashMap<>();
            transactionTypes.put("deposits", history.stream().filter(t -> t.contains("DEPOSIT")).count());
            transactionTypes.put("withdrawals", history.stream().filter(t -> t.contains("WITHDRAW")).count());
            transactionTypes.put("sends", history.stream().filter(t -> t.contains("SEND")).count());
            transactionTypes.put("receives", history.stream().filter(t -> t.contains("RECEIVE")).count());
            transactionTypes.put("exchanges", history.stream().filter(t -> t.contains("EXCHANGE")).count());
            transactionTypes.put("assetOperations", history.stream().filter(t -> t.contains("ASSET")).count());
            transactionTypes.put("loyaltyOperations", history.stream().filter(t -> t.contains("LOYALTY")).count());
            analytics.put("transactionBreakdown", transactionTypes);
            analytics.put("totalTransactions", history.size());
        }

        // Token value
        Double tokenValue = tokenRepository.getTotalConvertedValueByWalletId(walletId);
        analytics.put("totalTokenValue", tokenValue != null ? tokenValue : 0.0);

        // Asset value
        Double assetValue = assetRepository.getTotalAssetValueByWalletId(walletId);
        analytics.put("totalAssetValue", assetValue != null ? assetValue : 0.0);

        // Net worth
        double netWorth = wallet.getBalance()
                + (tokenValue != null ? tokenValue : 0.0)
                + (assetValue != null ? assetValue : 0.0);
        analytics.put("netWorth", Math.round(netWorth * 100.0) / 100.0);

        analytics.put("generatedAt", LocalDateTime.now().toString());
        return analytics;
    }

    public Map<String, Object> getLatestReport() {
        if (latestReport.isEmpty()) {
            return generateFullReport();
        }
        return latestReport;
    }
}
