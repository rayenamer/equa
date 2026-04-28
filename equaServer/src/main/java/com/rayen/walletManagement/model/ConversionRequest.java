package com.rayen.walletManagement.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConversionRequest {
    private CurrencyCode fromCurrency;
    private CurrencyCode toCurrency;
    private BigDecimal amount;
}
