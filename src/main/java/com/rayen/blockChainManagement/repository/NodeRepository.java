package com.rayen.blockChainManagement.repository;

import com.rayen.blockChainManagement.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {


    @Query("SELECT n FROM Node n WHERE " +
            "(:status IS NULL OR n.status = :status) AND " +
            "(:publicKey IS NULL OR n.publicKey = :publicKey) AND " +
            "(:nodeType IS NULL OR n.nodeType = :nodeType) AND " +
            "(:location IS NULL OR n.location = :location) AND " +
            "(:ipAddress IS NULL OR n.ipAddress = :ipAddress) AND " +
            "(:minReputationScore IS NULL OR n.reputationScore >= :minReputationScore) AND " +
            "(:maxReputationScore IS NULL OR n.reputationScore <= :maxReputationScore) AND " +
            "(:lastSeenAfter IS NULL OR n.lastSeen > :lastSeenAfter) AND " +
            "(:lastSeenBefore IS NULL OR n.lastSeen < :lastSeenBefore) AND " +
            "(:createdAfter IS NULL OR n.createdAt > :createdAfter) AND " +
            "(:hasTransaction IS NULL OR (:hasTransaction = true AND n.transaction IS NOT NULL) OR (:hasTransaction = false AND n.transaction IS NULL)) " +
            "ORDER BY n.reputationScore DESC, n.lastSeen DESC")
    List<Node> findNodesByOptionalParams(
            @Param("status") String status,
            @Param("publicKey") String publicKey,
            @Param("nodeType") String nodeType,
            @Param("location") String location,
            @Param("ipAddress") String ipAddress,
            @Param("minReputationScore") Double minReputationScore,
            @Param("maxReputationScore") Double maxReputationScore,
            @Param("lastSeenAfter") LocalDateTime lastSeenAfter,
            @Param("lastSeenBefore") LocalDateTime lastSeenBefore,
            @Param("createdAfter") LocalDateTime createdAfter,
            @Param("hasTransaction") Boolean hasTransaction
    );

    @Query("SELECT n FROM Node n ORDER BY n.reputationScore DESC")
    List<Node> findTopNodesByReputation();

    long countByStatus(String status);

    @Query("SELECT n FROM Node n WHERE n.status = 'ONLINE' ORDER BY n.lastSeen DESC")
    List<Node> findOnlineNodes();
}