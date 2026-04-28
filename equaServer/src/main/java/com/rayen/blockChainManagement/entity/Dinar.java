package com.rayen.blockChainManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dinars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dinar {

    @Id
    @Column(name = "dinar_id")
    private String dinarId;

    @Column(name = "origin", length = 100)
    private String origin;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnoreProperties({"dinars", "storedDinars", "transactions", "blockchainRecord", "hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private DinarWallet wallet;

    @JsonIgnoreProperties({"storedDinars", "transactions", "blockchainRecord", "hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private Node storageNode;
}