package com.rayen.blockChainManagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
     private Integer transactionId;
     private String fromWallet;
     private String toWallet;
     private BigDecimal amount;
     private LocalDateTime timestamp;
     private String status;
     private String transactionHash;
     private BigDecimal fee;
}
