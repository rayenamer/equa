package com.rayen.blockChainManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "conversions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The wallet that performed the conversion */
    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    /** How many Dinars were exchanged */
    @Column(name = "dinar_amount", nullable = false, precision = 19, scale = 8)
    private BigDecimal dinarAmount;

    /** How many EQUAs were received */
    @Column(name = "equa_amount", nullable = false, precision = 19, scale = 8)
    private BigDecimal equaAmount;

    /** The rate at the moment of conversion — useful for audit/history */
    @Column(name = "rate_at_conversion", nullable = false, precision = 19, scale = 8)
    private BigDecimal rateAtConversion;

    /** When it happened */
    @Column(name = "converted_at", nullable = false, updatable = false)
    private Instant convertedAt;

    @PrePersist
    protected void onCreate() {
        this.convertedAt = Instant.now();
    }
}
