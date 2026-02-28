package com.rayen.blockChainManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "from_wallet", nullable = false)
    private String fromWallet;

    @Column(name = "to_wallet", nullable = false)
    private String toWallet;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "transaction_hash", unique = false)
    private String transactionHash;

    @Column(name = "fee")
    private BigDecimal fee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_node_id")
    private Node validatorNode;

    //OneToMany with Token
    //TODO


}
