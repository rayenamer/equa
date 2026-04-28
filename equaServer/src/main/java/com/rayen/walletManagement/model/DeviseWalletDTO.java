package com.rayen.walletManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviseWalletDTO {
    private Long id;
    private Map<CurrencyCode, BigDecimal> balances;
}
