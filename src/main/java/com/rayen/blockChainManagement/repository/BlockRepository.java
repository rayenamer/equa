package com.rayen.blockChainManagement.repository;

import com.rayen.blockChainManagement.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Integer> {

    boolean existsByBlockHash(String blockHash);

    Optional<Block> findByBlockHash(String blockHash);

    @Query("SELECT b FROM Block b ORDER BY b.timestamp DESC LIMIT 1")
    Optional<Block> findLatestBlock();

    // **UNIFIED SEARCH METHOD** - Replace all the fragmented methods below
    @Query("SELECT b FROM Block b WHERE " +
            "(:blockHash IS NULL OR b.blockHash = :blockHash) AND " +
            "(:previousHash IS NULL OR b.previousHash = :previousHash) AND " +
            "(:minBlockSize IS NULL OR b.blockSize >= :minBlockSize) AND " +
            "(:maxBlockSize IS NULL OR b.blockSize <= :maxBlockSize) AND " +
            "(:minTransactionCount IS NULL OR SIZE(b.transaction) >= :minTransactionCount) AND " +
            "(:maxTransactionCount IS NULL OR SIZE(b.transaction) <= :maxTransactionCount) AND " +
            "(COALESCE(:timestampAfter, b.timestamp) = b.timestamp OR b.timestamp >= :timestampAfter) AND " +
            "(COALESCE(:timestampBefore, b.timestamp) = b.timestamp OR b.timestamp <= :timestampBefore) AND " +
            "(COALESCE(:createdAfter, b.createdAt) = b.createdAt OR b.createdAt >= :createdAfter) AND " +
            "(COALESCE(:createdBefore, b.createdAt) = b.createdAt OR b.createdAt <= :createdBefore) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'timestampDesc' THEN b.timestamp END DESC, " +
            "CASE WHEN :sortBy = 'timestampAsc' THEN b.timestamp END ASC, " +
            "CASE WHEN :sortBy = 'blockSizeDesc' THEN b.blockSize END DESC, " +
            "CASE WHEN :sortBy = 'blockSizeAsc' THEN b.blockSize END ASC, " +
            "b.timestamp DESC")
    List<Block> findBlocksByOptionalParams(
            @Param("blockHash") String blockHash,
            @Param("previousHash") String previousHash,
            @Param("minBlockSize") Long minBlockSize,
            @Param("maxBlockSize") Long maxBlockSize,
            @Param("minTransactionCount") Integer minTransactionCount,
            @Param("maxTransactionCount") Integer maxTransactionCount,
            @Param("timestampAfter") LocalDateTime timestampAfter,
            @Param("timestampBefore") LocalDateTime timestampBefore,
            @Param("createdAfter") LocalDateTime createdAfter,
            @Param("createdBefore") LocalDateTime createdBefore,
            @Param("sortBy") String sortBy  // "timestampDesc", "timestampAsc", "blockSizeDesc", "blockSizeAsc"
    );

    @Query("SELECT COUNT(b) FROM Block b")
    long countTotalBlocks();

    @Query("SELECT AVG(b.blockSize) FROM Block b WHERE b.blockSize IS NOT NULL")
    Double getAverageBlockSize();

    @Query("SELECT SUM(SIZE(b.transaction)) FROM Block b")
    Long getTotalTransactionCount();

    @Query("SELECT b FROM Block b WHERE b.previousHash = '0000000000000000000000000000000000000000000000000000000000000000'")
    Block findGenesisBlock();

    List<Block> findAllByOrderByTimestampDesc();
}