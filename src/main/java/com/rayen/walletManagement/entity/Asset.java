package com.rayen.walletManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "WalletAsset")
@Table(name = "wallet_assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private String assetType; // REAL_ESTATE, VEHICLE, EQUIPMENT, DIGITAL, OTHER

    @Column(nullable = false)
    private Double value = 0.0;

    private String status = "REGISTERED"; // REGISTERED, TRANSFERRED, DEVALUED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    @JsonIgnore
    private Wallet wallet;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean registerAsset(String ownerId, String assetType, Double value) {
        if (value <= 0 || ownerId == null || assetType == null) {
            return false;
        }
        this.ownerId = ownerId;
        this.assetType = assetType;
        this.value = value;
        this.status = "REGISTERED";
        return true;
    }

    public void updateAssetValue(Double newValue) {
        if (newValue >= 0) {
            this.value = newValue;
        }
    }

    public boolean transferAsset(String newOwnerId) {
        if (newOwnerId == null || newOwnerId.isBlank()) {
            return false;
        }
        this.ownerId = newOwnerId;
        this.status = "TRANSFERRED";
        return true;
    }
}
