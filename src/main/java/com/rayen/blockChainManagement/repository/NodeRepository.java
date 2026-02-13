package com.rayen.blockChainManagement.repository;

import com.rayen.blockChainManagement.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {
    Optional<Node> findByIpAddress(String ipAddress);

    List<Node> findByNodeType(String nodeType);

    List<Node> findByStatus(String status);

    Optional<Node> findByPublicKey(String publicKey);

    List<Node> findByStatusOrderByReputationScoreDesc(String status);

    List<Node> findByReputationScoreGreaterThanEqual(Double minScore);

    List<Node> findByReputationScoreBetweenOrderByReputationScoreDesc(Double minScore, Double maxScore);

    List<Node> findByLocation(String location);

    List<Node> findByLastSeenAfter(LocalDateTime dateTime);

    List<Node> findByLastSeenBefore(LocalDateTime dateTime);

    List<Node> findByStatusAndNodeType(String status, String nodeType);

    @Query("SELECT n FROM Node n ORDER BY n.reputationScore DESC")
    List<Node> findTopNodesByReputation();

    // Get nodes with reputation score limit
    @Query("SELECT n FROM Node n WHERE n.reputationScore >= :minScore ORDER BY n.reputationScore DESC")
    List<Node> findNodesByMinimumReputation(@Param("minScore") Double minScore);

    List<Node> findByCreatedAtAfter(LocalDateTime date);

    long countByStatus(String status);

    @Query("SELECT n FROM Node n WHERE n.status = 'ONLINE' ORDER BY n.lastSeen DESC")
    List<Node> findOnlineNodes();

    @Query("SELECT n FROM Node n WHERE n.status = 'OFFLINE' ORDER BY n.lastSeen DESC")
    List<Node> findOfflineNodes();

    @Query("SELECT n FROM Node n WHERE n.transaction IS NOT NULL")
    List<Node> findValidatorNodes();

    List<Node> findByStatusAndLocation(String status, String location);
}
