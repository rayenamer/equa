package com.rayen.walletManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import com.rayen.blockChainManagement.entity.Business;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", unique = true, nullable = false)
    private Business business;

    private String status;
    private Float equaAmount = 0f;
    private LocalDateTime lastActivityAt;
}
