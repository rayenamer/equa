package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyExchangeService {

    @Autowired
    private WalletRepository walletRepository;

    // Exchange rates relative to EUR (base currency)
    private static final Map<String, Double> EXCHANGE_RATES = new HashMap<>();

    static {
        EXCHANGE_RATES.put("EUR", 1.0);
        EXCHANGE_RATES.put("USD", 1.08);
        EXCHANGE_RATES.put("GBP", 0.86);
        EXCHANGE_RATES.put("BTC", 0.000016);
        EXCHANGE_RATES.put("ETH", 0.00031);
        EXCHANGE_RATES.put("TND", 3.35);
        EXCHANGE_RATES.put("CHF", 0.94);
        EXCHANGE_RATES.put("JPY", 162.5);
    }

    private static final Double MAX_EXCHANGE_AMOUNT = 100000.0;
    private static final Double EXCHANGE_FEE_RATE = 0.005; // 0.5% fee

    // ==================== CURRENCY EXCHANGE ====================

    @Transactional
    public Map<String, Object> exchangeCurrency(Long walletId, String targetCurrency, Double amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new RuntimeException("Wallet is not active. Current status: " + wallet.getStatus());
        }

        if (amount <= 0) {
            throw new RuntimeException("Exchange amount must be positive");
        }

        if (amount > MAX_EXCHANGE_AMOUNT) {
            throw new RuntimeException("Exchange amount exceeds maximum allowed: " + MAX_EXCHANGE_AMOUNT);
        }

        String sourceCurrency = wallet.getCurrency();
        if (sourceCurrency.equals(targetCurrency)) {
            throw new RuntimeException("Source and target currencies are the same: " + sourceCurrency);
        }

        if (!EXCHANGE_RATES.containsKey(sourceCurrency)) {
            throw new RuntimeException("Unsupported source currency: " + sourceCurrency);
        }
        if (!EXCHANGE_RATES.containsKey(targetCurrency)) {
            throw new RuntimeException("Unsupported target currency: " + targetCurrency);
        }

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance. Available: " + wallet.getBalance() + ", Requested: " + amount);
        }

        // Convert: source → EUR → target
        Double sourceToEur = EXCHANGE_RATES.get(sourceCurrency);
        Double eurToTarget = EXCHANGE_RATES.get(targetCurrency);
        Double amountInEur = amount / sourceToEur;
        Double convertedAmount = amountInEur * eurToTarget;

        // Apply exchange fee
        Double fee = convertedAmount * EXCHANGE_FEE_RATE;
        Double finalAmount = convertedAmount - fee;

        // Update wallet
        wallet.setBalance(wallet.getBalance() - amount + finalAmount);
        wallet.setCurrency(targetCurrency);

        wallet.getTransactionHistory().add(
                LocalDateTime.now() + " | CURRENCY_EXCHANGE | " + amount + " " + sourceCurrency
                        + " → " + String.format("%.4f", finalAmount) + " " + targetCurrency
                        + " | rate: " + String.format("%.6f", eurToTarget / sourceToEur)
                        + " | fee: " + String.format("%.4f", fee) + " " + targetCurrency
        );

        walletRepository.save(wallet);

        Map<String, Object> result = new HashMap<>();
        result.put("walletId", wallet.getWalletId());
        result.put("sourceCurrency", sourceCurrency);
        result.put("targetCurrency", targetCurrency);
        result.put("sourceAmount", amount);
        result.put("exchangeRate", eurToTarget / sourceToEur);
        result.put("fee", fee);
        result.put("feeRate", EXCHANGE_FEE_RATE);
        result.put("convertedAmount", finalAmount);
        result.put("newBalance", wallet.getBalance());
        result.put("timestamp", LocalDateTime.now().toString());
        return result;
    }

    @Transactional
    public Map<String, Object> crossWalletTransfer(Long senderWalletId, Long recipientWalletId, Double amount) {
        Wallet sender = walletRepository.findById(senderWalletId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet recipient = walletRepository.findById(recipientWalletId)
                .orElseThrow(() -> new RuntimeException("Recipient wallet not found"));

        if (!"ACTIVE".equals(sender.getStatus()) || !"ACTIVE".equals(recipient.getStatus())) {
            throw new RuntimeException("Both wallets must be active for cross-currency transfer");
        }

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance for cross-currency transfer");
        }

        String senderCurrency = sender.getCurrency();
        String recipientCurrency = recipient.getCurrency();

        // Convert amount from sender's currency to recipient's currency
        Double senderToEur = EXCHANGE_RATES.get(senderCurrency);
        Double eurToRecipient = EXCHANGE_RATES.get(recipientCurrency);
        Double amountInEur = amount / senderToEur;
        Double convertedAmount = amountInEur * eurToRecipient;

        Double fee = convertedAmount * EXCHANGE_FEE_RATE;
        Double finalAmount = convertedAmount - fee;

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + finalAmount);

        sender.getTransactionHistory().add(
                LocalDateTime.now() + " | CROSS_CURRENCY_SEND | -" + amount + " " + senderCurrency
                        + " → " + String.format("%.4f", finalAmount) + " " + recipientCurrency
                        + " | to: " + recipient.getPublicKey()
        );
        recipient.getTransactionHistory().add(
                LocalDateTime.now() + " | CROSS_CURRENCY_RECEIVE | +" + String.format("%.4f", finalAmount) + " " + recipientCurrency
                        + " | from: " + sender.getPublicKey()
        );

        walletRepository.save(sender);
        walletRepository.save(recipient);

        Map<String, Object> result = new HashMap<>();
        result.put("senderWalletId", senderWalletId);
        result.put("recipientWalletId", recipientWalletId);
        result.put("sentAmount", amount);
        result.put("sentCurrency", senderCurrency);
        result.put("receivedAmount", finalAmount);
        result.put("receivedCurrency", recipientCurrency);
        result.put("exchangeRate", eurToRecipient / senderToEur);
        result.put("fee", fee);
        return result;
    }

    // ==================== EXCHANGE RATE INFO ====================

    public Map<String, Object> getExchangeRates() {
        Map<String, Object> result = new HashMap<>();
        result.put("baseCurrency", "EUR");
        result.put("rates", new HashMap<>(EXCHANGE_RATES));
        result.put("feeRate", EXCHANGE_FEE_RATE);
        result.put("maxExchangeAmount", MAX_EXCHANGE_AMOUNT);
        result.put("supportedCurrencies", EXCHANGE_RATES.keySet());
        result.put("timestamp", LocalDateTime.now().toString());
        return result;
    }

    public Map<String, Object> convertPreview(String from, String to, Double amount) {
        if (!EXCHANGE_RATES.containsKey(from) || !EXCHANGE_RATES.containsKey(to)) {
            throw new RuntimeException("Unsupported currency pair: " + from + " → " + to);
        }
        Double fromToEur = EXCHANGE_RATES.get(from);
        Double eurToTarget = EXCHANGE_RATES.get(to);
        Double rate = eurToTarget / fromToEur;
        Double converted = amount * rate;
        Double fee = converted * EXCHANGE_FEE_RATE;

        Map<String, Object> result = new HashMap<>();
        result.put("from", from);
        result.put("to", to);
        result.put("amount", amount);
        result.put("exchangeRate", rate);
        result.put("convertedAmount", converted);
        result.put("fee", fee);
        result.put("finalAmount", converted - fee);
        return result;
    }
}
