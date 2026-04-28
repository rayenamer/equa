package com.rayen.walletManagement.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundsRequest {
    private CurrencyCode currency;
    private BigDecimal amount;
}
