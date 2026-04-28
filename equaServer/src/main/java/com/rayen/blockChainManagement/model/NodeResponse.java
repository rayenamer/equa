package com.rayen.blockChainManagement.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeResponse {

    private Integer nodeId;
    private String nodeType;
    private String ipAddress;
    private String status;
    private String publicKey;
    private Double reputationScore;
    private LocalDateTime lastSeen;
    private String location;
    private Integer connectedNodesCount;
    private boolean hasTransaction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}