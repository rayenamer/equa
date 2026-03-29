package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FraudDetectionService {

    @Autowired
    private WalletRepository walletRepository;

    // Fraud detection thresholds
    private static final int MAX_TRANSACTIONS_PER_HOUR = 10;
    private static final int MAX_TRANSACTIONS_PER_DAY = 50;
    private static final Double UNUSUAL_AMOUNT_MULTIPLIER = 5.0;
    private static final Double SUSPICIOUS_LARGE_AMOUNT = 25000.0;
    private static final int RAPID_TRANSACTION_SECONDS = 30;

    // ==================== FRAUD DETECTION ====================

    public Map<String, Object> detectFraud(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        List<String> history = wallet.getTransactionHistory();
        if (history == null) history = new ArrayList<>();

        List<Map<String, Object>> alerts = new ArrayList<>();
        int riskScore = 0;

        // Check 1: Velocity — too many transactions in short time
        int velocityResult = checkVelocity(history);
        if (velocityResult > 0) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "VELOCITY_ANOMALY");
            alert.put("severity", velocityResult > 2 ? "HIGH" : "MEDIUM");
            alert.put("description", "Unusual transaction frequency detected: " + velocityResult + " rapid transactions");
            alert.put("rule", "Max " + MAX_TRANSACTIONS_PER_HOUR + " transactions/hour, min " + RAPID_TRANSACTION_SECONDS + "s between transactions");
            alerts.add(alert);
            riskScore += velocityResult * 15;
        }

        // Check 2: Amount anomaly — unusually large transactions
        List<Double> amountAnomalies = checkAmountAnomalies(history);
        if (!amountAnomalies.isEmpty()) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "AMOUNT_ANOMALY");
            alert.put("severity", "HIGH");
            alert.put("description", "Unusual transaction amounts detected: " + amountAnomalies);
            alert.put("rule", "Amounts exceeding " + UNUSUAL_AMOUNT_MULTIPLIER + "x average or > " + SUSPICIOUS_LARGE_AMOUNT);
            alerts.add(alert);
            riskScore += amountAnomalies.size() * 20;
        }

        // Check 3: Round-trip detection — money going out and coming back suspiciously
        int roundTrips = checkRoundTrips(history);
        if (roundTrips > 0) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "ROUND_TRIP_SUSPICIOUS");
            alert.put("severity", roundTrips > 3 ? "CRITICAL" : "MEDIUM");
            alert.put("description", "Potential money laundering pattern: " + roundTrips + " round-trip transactions detected");
            alert.put("rule", "Multiple SEND followed by RECEIVE of similar amounts");
            alerts.add(alert);
            riskScore += roundTrips * 25;
        }

        // Check 4: Dormant account sudden activity
        boolean dormantActivity = checkDormantAccountActivity(history);
        if (dormantActivity) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "DORMANT_ACCOUNT_ACTIVITY");
            alert.put("severity", "MEDIUM");
            alert.put("description", "Sudden activity on previously dormant account");
            alert.put("rule", "Large transactions after period of inactivity");
            alerts.add(alert);
            riskScore += 30;
        }

        // Check 5: Multiple currency exchanges in short period
        int currencyExchanges = countRecentCurrencyExchanges(history);
        if (currencyExchanges > 3) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "CURRENCY_EXCHANGE_ABUSE");
            alert.put("severity", "HIGH");
            alert.put("description", "Excessive currency exchanges: " + currencyExchanges + " in recent history");
            alert.put("rule", "More than 3 currency exchanges may indicate arbitrage abuse");
            alerts.add(alert);
            riskScore += currencyExchanges * 10;
        }

        // Overall fraud risk classification
        String fraudRisk;
        String action;
        if (riskScore >= 80) {
            fraudRisk = "CRITICAL";
            action = "BLOCK_ACCOUNT - Immediate review required. Suspend all transactions.";
        } else if (riskScore >= 50) {
            fraudRisk = "HIGH";
            action = "FLAG_FOR_REVIEW - Manual review required before next transaction.";
        } else if (riskScore >= 20) {
            fraudRisk = "MEDIUM";
            action = "MONITOR - Enhanced monitoring activated. Continue with caution.";
        } else {
            fraudRisk = "LOW";
            action = "CLEAR - No suspicious activity detected.";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("walletId", walletId);
        result.put("customerId", wallet.getCustomerId());
        result.put("fraudRiskScore", riskScore);
        result.put("fraudRiskLevel", fraudRisk);
        result.put("recommendedAction", action);
        result.put("alerts", alerts);
        result.put("totalAlertsCount", alerts.size());
        result.put("totalTransactionsAnalyzed", history.size());
        result.put("walletStatus", wallet.getStatus());
        result.put("assessmentDate", LocalDateTime.now().toString());

        // Summary statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeposits", history.stream().filter(t -> t.contains("DEPOSIT")).count());
        stats.put("totalWithdrawals", history.stream().filter(t -> t.contains("WITHDRAW")).count());
        stats.put("totalSends", history.stream().filter(t -> t.contains("SEND")).count());
        stats.put("totalReceives", history.stream().filter(t -> t.contains("RECEIVE")).count());
        stats.put("totalExchanges", history.stream().filter(t -> t.contains("EXCHANGE")).count());
        result.put("transactionStats", stats);

        return result;
    }

    // ==================== DETECTION ALGORITHMS ====================

    private int checkVelocity(List<String> history) {
        if (history.size() < 3) return 0;

        int rapidCount = 0;
        List<String> recent = history.subList(Math.max(0, history.size() - 10), history.size());

        for (int i = 1; i < recent.size(); i++) {
            try {
                String ts1 = recent.get(i - 1).split(" \\| ")[0].trim();
                String ts2 = recent.get(i).split(" \\| ")[0].trim();
                LocalDateTime t1 = LocalDateTime.parse(ts1);
                LocalDateTime t2 = LocalDateTime.parse(ts2);
                long seconds = ChronoUnit.SECONDS.between(t1, t2);
                if (Math.abs(seconds) < RAPID_TRANSACTION_SECONDS) {
                    rapidCount++;
                }
            } catch (Exception e) {
                // Skip unparseable entries
            }
        }
        return rapidCount;
    }

    private List<Double> checkAmountAnomalies(List<String> history) {
        List<Double> amounts = new ArrayList<>();
        List<Double> anomalies = new ArrayList<>();

        for (String entry : history) {
            try {
                String[] parts = entry.split("\\|");
                if (parts.length >= 3) {
                    String amountStr = parts[2].trim().replaceAll("[^0-9.]", "");
                    if (!amountStr.isEmpty()) {
                        amounts.add(Double.parseDouble(amountStr));
                    }
                }
            } catch (Exception e) {
                // Skip
            }
        }

        if (amounts.size() < 3) return anomalies;

        double avg = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        for (Double amount : amounts) {
            if (amount > avg * UNUSUAL_AMOUNT_MULTIPLIER || amount > SUSPICIOUS_LARGE_AMOUNT) {
                anomalies.add(amount);
            }
        }
        return anomalies;
    }

    private int checkRoundTrips(List<String> history) {
        int roundTrips = 0;
        List<Double> sends = new ArrayList<>();
        List<Double> receives = new ArrayList<>();

        for (String entry : history) {
            try {
                String[] parts = entry.split("\\|");
                if (parts.length >= 3) {
                    String type = parts[1].trim();
                    String amountStr = parts[2].trim().replaceAll("[^0-9.]", "");
                    if (!amountStr.isEmpty()) {
                        double amount = Double.parseDouble(amountStr);
                        if (type.contains("SEND")) sends.add(amount);
                        if (type.contains("RECEIVE")) receives.add(amount);
                    }
                }
            } catch (Exception e) {
                // Skip
            }
        }

        // Check for similar amounts in sends and receives (within 5% tolerance)
        for (Double sent : sends) {
            for (Double received : receives) {
                if (Math.abs(sent - received) / sent < 0.05) {
                    roundTrips++;
                }
            }
        }
        return roundTrips;
    }

    private boolean checkDormantAccountActivity(List<String> history) {
        if (history.size() < 5) return false;

        // Check if recent transactions are large compared to historical pattern
        List<String> recent = history.subList(Math.max(0, history.size() - 3), history.size());
        List<String> older = history.subList(0, Math.max(0, history.size() - 3));

        long recentLargeTransactions = recent.stream()
                .filter(t -> {
                    try {
                        String[] parts = t.split("\\|");
                        String amountStr = parts[2].trim().replaceAll("[^0-9.]", "");
                        return !amountStr.isEmpty() && Double.parseDouble(amountStr) > SUSPICIOUS_LARGE_AMOUNT;
                    } catch (Exception e) {
                        return false;
                    }
                }).count();

        return older.size() < 3 && recentLargeTransactions >= 2;
    }

    private int countRecentCurrencyExchanges(List<String> history) {
        return (int) history.stream()
                .filter(t -> t.contains("CURRENCY_EXCHANGE") || t.contains("CROSS_CURRENCY"))
                .count();
    }
}
