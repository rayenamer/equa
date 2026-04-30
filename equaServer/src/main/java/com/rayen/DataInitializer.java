package com.rayen;

import com.rayen.blockChainManagement.entity.CharginCard;
import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.repository.BlockRepository;
import com.rayen.blockChainManagement.repository.CharginCardRepository;
import com.rayen.blockChainManagement.repository.NodeRepository;
import com.rayen.blockChainManagement.service.BlockService;
import com.rayen.blockChainManagement.model.BlockRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final NodeRepository nodeRepository;
    private final BlockRepository blockRepository;
    private final BlockService blockService;

    @Override
    public void run(ApplicationArguments args) {
        log.info(
                "================================================================ DATA INIT START ================================================================");
        initNodes();
        initGenesisBlock();
        initChargingCards();
        log.info(
                "================================================================ DATA INIT END ==================================================================");
    }

    private void initNodes() {
        if (nodeRepository.count() > 0) {
            log.info("✅ Nodes already exist — skipping node creation");
            return;
        }

        log.info("🌐 Creating 5 default nodes...");

        List<Node> nodes = List.of(
                buildNode("MINER", "192.168.1.1", "node-pubkey-001", "Paris"),
                buildNode("MINER", "192.168.1.2", "node-pubkey-002", "London"),
                buildNode("MINER", "192.168.1.3", "node-pubkey-003", "Berlin"),
                buildNode("MINER", "192.168.1.4", "node-pubkey-004", "Tunis"),
                buildNode("MINER", "192.168.1.5", "node-pubkey-005", "Tokyo"));

        nodeRepository.saveAll(nodes);
        log.info("✅ 5 nodes created successfully");
    }

    private void initGenesisBlock() {
        if (blockRepository.countTotalBlocks() > 0) {
            log.info("✅ Blockchain already exists — skipping genesis block creation");
            return;
        }

        log.info("🧱 Creating genesis block...");
        blockService.generateBlock();
        log.info("✅ Genesis block created successfully");
    }

    private Node buildNode(String type, String ip, String publicKey, String location) {
        Node node = new Node();
        LocalDateTime now = LocalDateTime.now();
        node.setNodeType(type);
        node.setIpAddress(ip);
        node.setPublicKey(publicKey);
        node.setLocation(location);
        node.setStatus("ONLINE");
        node.setReputationScore(20.0);
        node.setCreatedAt(now);
        node.setUpdatedAt(now);
        node.setLastSeen(now);
        return node;
    }

    private final CharginCardRepository charginCardRepository;

    private void initChargingCards() {
        if (charginCardRepository.count() > 0) {
            log.info("✅ Charging cards already exist — skipping card creation");
            return;
        }

        log.info("💳 Creating default charging cards...");

        List<CharginCard> cards = List.of(
                CharginCard.builder().cardCode("#4H5SSH").dinarAmount(100).consumed(false).build(),
                CharginCard.builder().cardCode("KJF4#21").dinarAmount(200).consumed(false).build(),
                CharginCard.builder().cardCode("GBF6##@").dinarAmount(500).consumed(false).build(),
                CharginCard.builder().cardCode("NB#RR78").dinarAmount(1000).consumed(false).build(),
                CharginCard.builder().cardCode("NJ#NS}}").dinarAmount(50).consumed(false).build());

        charginCardRepository.saveAll(cards);
        log.info("✅ {} charging cards created successfully", cards.size());
    }
}
