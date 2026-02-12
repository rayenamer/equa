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

    Optional<Block> findByBlockHash(String blockHash);

    Optional<Block> findByPreviousHash(String previousHash);

    List<Block> findAllByOrderByTimestampDesc();

    List<Block> findAllByOrderByTimestampAsc();

    List<Block> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Block> findByBlockSizeBetween(Long minSize, Long maxSize);

    List<Block> findByBlockSizeGreaterThanEqual(Long minSize);

    boolean existsByBlockHash(String blockHash);

    List<Block> findByCreatedAtAfter(LocalDateTime date);

    List<Block> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT b FROM Block b ORDER BY b.timestamp DESC LIMIT 1")
    Optional<Block> findLatestBlock();

    @Query("SELECT b FROM Block b WHERE SIZE(b.transaction) = :count")
    List<Block> findBlocksByTransactionCount(@Param("count") int count);

    @Query("SELECT b FROM Block b WHERE SIZE(b.transaction) > :minCount")
    List<Block> findBlocksWithMinTransactionCount(@Param("minCount") int minCount);

    @Query("SELECT COUNT(b) FROM Block b")
    long countTotalBlocks();

    @Query("SELECT AVG(b.blockSize) FROM Block b WHERE b.blockSize IS NOT NULL")
    Double getAverageBlockSize();
}