package com.rayen.walletManagement.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetDTO {

    private Long assetId;
    private String ownerId;
    private String assetType;
    private Double value;
    private String status;
    private Long walletId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
