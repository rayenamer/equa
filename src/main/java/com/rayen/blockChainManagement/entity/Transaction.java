package com.rayen.blockChainManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "from_wallet", nullable = false, length = 100)
    private String fromWallet;

    @Column(name = "to_wallet", nullable = false, length = 100)
    private String toWallet;

    @Column(name = "amount", nullable = false, precision = 18, scale = 8)
    private BigDecimal amount;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;

    @Column(name = "transaction_hash", unique = true, length = 64)
    private String transactionHash;

    @Column(name = "fee", precision = 18, scale = 8)
    private BigDecimal fee;

    @Column(name = "signature", length = 500)
    private String signature;

    @Column(name = "confirmation_count")
    private Integer confirmationCount;

    @Column(name = "gas_used")
    private Long gasUsed;

    @Column(name = "notes", length = 500)
    private String notes;

    // One-to-One relationship with Block (one transaction belongs to one block)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", unique = true)
    private Block block;

    // One-to-One relationship with Node (one winning node validates this transaction)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_node_id", unique = true)
    private Node validatorNode;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
