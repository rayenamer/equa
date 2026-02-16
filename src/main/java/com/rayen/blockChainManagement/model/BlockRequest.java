package com.rayen.blockChainManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequest {

    // For block creation
    private String previousHash;

    // Optional search/filter parameters
    private String blockHash;
    private Long minBlockSize;
    private Long maxBlockSize;
    private LocalDateTime timestampAfter;
    private LocalDateTime timestampBefore;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private String sortBy; // "timestampDesc", "timestampAsc", "blockSizeDesc", "blockSizeAsc"

}
