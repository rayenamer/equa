package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class WalletBalanceValidator {

    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid amount: {}", amount);
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public void validateWalletActive(Wallet wallet) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet is required");
        }
        if (wallet.getStatus() == null || wallet.getStatus().equalsIgnoreCase("SUSPENDED")) {
            throw new IllegalStateException("Wallet is not active");
        }
    }

    public void ensureSufficientMainBalance(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance() == null || BigDecimal.valueOf(wallet.getBalance()).compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient main wallet balance");
        }
    }
}
