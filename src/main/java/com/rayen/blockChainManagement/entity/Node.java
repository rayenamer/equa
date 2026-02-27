package com.rayen.blockChainManagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nodes")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "node_connections",
            joinColumns = @JoinColumn(name = "node_id"),
            inverseJoinColumns = @JoinColumn(name = "connected_node_id")
    )
    private Set<Node> connectedNodes = new HashSet<>();



    // One-to-One relationship with Transaction (winning node validates the transaction)
    @OneToOne(mappedBy = "validatorNode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Transaction transaction;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}