package com.rayen.blockChainManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "blocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Integer blockId;

    @JsonIgnoreProperties({"previousBlock", "transactions", "createdAt", "updatedAt"})
    @Column(name = "previous_hash", length = 64)
    private String previousHash;

    @Column(name = "block_hash", nullable = false, unique = true, length = 64)
    private String blockHash;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "block_size")
    private Long blockSize;

    // One-to-One self-reference: This block knows the previous block
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_block_id")
    private Block previousBlock;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private List<Transaction> transaction;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}