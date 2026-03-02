package com.rayen.blockChainManagement.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "nodes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "node_id")
    private Integer nodeId;

    @Column(name = "node_type", nullable = false, length = 50)
    private String nodeType;

    @Column(name = "ip_address", nullable = false, unique = true, length = 45)
    private String ipAddress;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "public_key", length = 500)
    private String publicKey;

    @Column(name = "reputation_score")
    private Double reputationScore;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "node_blockchain_records", joinColumns = @JoinColumn(name = "node_id"))
    @Column(name = "block_record", columnDefinition = "TEXT")
    private List<String> blockchainRecord = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "validatorNode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "storageNode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Dinar> storedDinars = new ArrayList<>();
}