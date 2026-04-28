package com.rayen.financialMarketManagement.dto;

import com.rayen.financialMarketManagement.entity.TradeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeResponseFinancial {
    private Long transactionId;
    private TradeType type;
    private String ticker;
    private BigDecimal quantity;
    private BigDecimal pricePerUnitEqua;
    private BigDecimal totalEqua;
    private BigDecimal feesEqua;
    private LocalDateTime createdAt;
}

