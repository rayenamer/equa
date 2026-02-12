package com.rayen.blockChainManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Integer blockId;

    @Column(name = "previous_hash", length = 64)
    private String previousHash;

    @Column(name = "block_hash", nullable = false, unique = true, length = 64)
    private String blockHash;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "nonce", nullable = false)
    private Integer nonce;

    @Column(name = "merkle_root", length = 64)
    private String merkleRoot;

    @Column(name = "difficulty")
    private Integer difficulty;

    @Column(name = "block_size")
    private Long blockSize;

    @Column(name = "block_height")
    private Integer blockHeight;

    // One-to-One self-reference: This block knows the previous block
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_block_id")
    private Block previousBlock;

    // One-to-One: This block knows the next block (inverse side)
    @OneToOne(mappedBy = "previousBlock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Block nextBlock;

    // One-to-One relationship with Transaction (one block has one transaction)
    @OneToOne(mappedBy = "block", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Transaction transaction;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}