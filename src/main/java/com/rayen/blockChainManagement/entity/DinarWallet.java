package com.rayen.blockChainManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dinar_wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DinarWallet {

    @Id
    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "balance", nullable = false, precision = 15, scale = 3)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DinarWalletStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dinar> dinars = new ArrayList<>();
}