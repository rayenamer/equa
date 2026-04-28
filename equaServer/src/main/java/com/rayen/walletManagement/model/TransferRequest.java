package com.rayen.walletManagement.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Long sourceWalletId;
    private CurrencyCode sourceCurrency;
    private Long targetWalletId;
    private CurrencyCode targetCurrency;
    private BigDecimal amount;
}
