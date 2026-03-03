package com.rayen.financialMarketManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Integer assetId;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType;

    @Column(name = "value", nullable = false)
    private Float value;

    @Column(name = "demand")
    private Integer demand;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_bought")
    private Integer totalBought;

    @Column(name = "total_sold")
    private Integer totalSold;

    @Column(name = "initial_value")
    private Float initialValue;

    @PrePersist
    public void onCreate() {
        if (this.initialValue == null) {
            this.initialValue = this.value;
        }
    }
}
