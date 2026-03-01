package com.rayen.walletManagement.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDTO {

    private Long tokenId;
    private Double value;
    private Long customerId;
    private Double conversionRate;
    private Integer totalSupply;
    private Long walletId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
