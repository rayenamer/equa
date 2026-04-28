package com.rayen.blockChainManagement.service;

import com.rayen.AuthContextService;
import com.rayen.blockChainManagement.entity.*;
import com.rayen.blockChainManagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DinarWalletService {

    private final DinarWalletRepository dinarWalletRepository;
    private final DinarRepository dinarRepository;
    private final NodeRepository nodeRepository;
    private final AuthContextService authContextService;

    @Transactional
    public DinarWallet createWallet() {
        String userId = authContextService.getLoggedInUserId().toString();
        if (dinarWalletRepository.existsByUserId(userId))
            throw new IllegalArgumentException("User already has a DinarWallet");

        DinarWallet wallet = DinarWallet.builder()
                .walletId("DW-" + UUID.randomUUID())
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .status(DinarWalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        log.info("================================================================");
        log.info("👛 DinarWallet created for user {}", userId);
        log.info("🔑 Wallet ID: {}", wallet.getWalletId());
        log.info("================================================================");

        return dinarWalletRepository.save(wallet);
    }

    @Transactional
    public DinarWallet deposit(String walletId, int amount) {
        DinarWallet wallet = dinarWalletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (wallet.getStatus() != DinarWalletStatus.ACTIVE)
            throw new IllegalStateException("Wallet is not active");

        List<Node> nodes = nodeRepository.findAll();
        if (nodes.isEmpty()) throw new IllegalStateException("No nodes available");

        Random random = new Random();

        log.info("================================================================");
        log.info("💰 Depositing {} dinars into wallet {}", amount, walletId);
        log.info("📡 Distributing across {} nodes...", nodes.size());
        log.info("----------------------------------------------------------------");

        for (int i = 0; i < amount; i++) {
            Node storageNode = nodes.get(random.nextInt(nodes.size()));

            Dinar dinar = Dinar.builder()
                    .dinarId("TND-" + UUID.randomUUID())
                    .origin("DEPOSIT")
                    .createdAt(LocalDateTime.now())
                    .wallet(wallet)
                    .storageNode(storageNode)
                    .build();

            storageNode.getStoredDinars().add(dinar);
            nodeRepository.save(storageNode);

            log.info("💵 Dinar {} → Node {} | {} | {}",
                    dinar.getDinarId(),
                    storageNode.getNodeId(),
                    storageNode.getNodeType(),
                    storageNode.getLocation());
        }

        wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
        wallet.setUpdatedAt(LocalDateTime.now());
        DinarWallet saved = dinarWalletRepository.save(wallet);

        log.info("----------------------------------------------------------------");
        log.info("✅ Deposit complete | New balance: {} TND", saved.getBalance());
        log.info("================================================================");

        return saved;
    }

    @Transactional
    public DinarWallet withdraw(String walletId, int amount) throws BadRequestException {
        DinarWallet wallet = dinarWalletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (wallet.getStatus() != DinarWalletStatus.ACTIVE)
            throw new IllegalStateException("Wallet is not active");

        if (wallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0)
            throw new BadRequestException(String.format(
                    "Insufficient balance. Required: %d TND | Available: %.3f TND",
                    amount, wallet.getBalance()
            ));

        List<Dinar> walletDinars = dinarRepository.findAllByWallet_WalletId(walletId);

        if (walletDinars.size() < amount)
            throw new BadRequestException("Not enough dinar units to withdraw");

        log.info("================================================================");
        log.info("🏧 Withdrawing {} dinars from wallet {}", amount, walletId);
        log.info("----------------------------------------------------------------");

        for (int i = 0; i < amount; i++) {
            Dinar dinar = walletDinars.get(i);
            log.info("🗑️ Dinar {} leaving system | was on Node {} | {}",
                    dinar.getDinarId(),
                    dinar.getStorageNode().getNodeId(),
                    dinar.getStorageNode().getLocation());
            dinarRepository.delete(dinar); // ← dinar leaves the system entirely
        }

        wallet.setBalance(wallet.getBalance().subtract(BigDecimal.valueOf(amount)));
        wallet.setUpdatedAt(LocalDateTime.now());
        DinarWallet saved = dinarWalletRepository.save(wallet);

        log.info("----------------------------------------------------------------");
        log.info("✅ Withdrawal complete | New balance: {} TND", saved.getBalance());
        log.info("================================================================");

        return saved;
    }

    @Transactional(readOnly = true)
    public DinarWallet getWallet(String walletId) {
        return dinarWalletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    }
    @Transactional
    public DinarWallet getMyWallet(){
        return  dinarWalletRepository.findByUserId(authContextService.getLoggedInUserId().toString())
                .orElseThrow(() -> new IllegalArgumentException("You dont have wallet"));
    }

    @Transactional(readOnly = true)
    public List<Dinar> getWalletDinars(String walletId) {
        return dinarRepository.findAllByWallet_WalletId(walletId);
    }

    @Transactional(readOnly = true)
    public List<Dinar> getNodeDinars(Integer nodeId) {
        return dinarRepository.findAllByStorageNode_NodeId(nodeId);
    }
}