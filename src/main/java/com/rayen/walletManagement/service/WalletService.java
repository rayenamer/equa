package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.TransferRequest;
import com.rayen.walletManagement.model.WalletDTO;
import com.rayen.walletManagement.model.TokenDTO;
import com.rayen.walletManagement.model.AssetDTO;
import com.rayen.walletManagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    private static final Double MAX_SINGLE_TRANSACTION = 50000.0;
    private static final Double DAILY_TRANSACTION_LIMIT = 100000.0;
    private static final Double MIN_BALANCE_THRESHOLD = 10.0;

    // ==================== CRUD ====================

    public List<WalletDTO> getAllWallets() {
        return walletRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public WalletDTO getWalletById(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));
        return convertToDTO(wallet);
    }

    public WalletDTO getWalletByCustomerId(Long customerId) {
        Wallet wallet = walletRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for customer: " + customerId));
        return convertToDTO(wallet);
    }

    public WalletDTO createWallet(WalletDTO walletDTO) {
        if (walletRepository.existsByCustomerId(walletDTO.getCustomerId())) {
            throw new RuntimeException("Wallet already exists for customer: " + walletDTO.getCustomerId());
        }
        Wallet wallet = convertToEntity(walletDTO);
        wallet.setPublicKey(generatePublicKey());
        wallet.setStatus("ACTIVE");
        wallet.setBalance(walletDTO.getBalance() != null ? walletDTO.getBalance() : 0.0);
        wallet.setCurrency(walletDTO.getCurrency() != null ? walletDTO.getCurrency() : "EUR");
        wallet.setLoyaltyPoints(walletDTO.getLoyaltyPoints() != null ? walletDTO.getLoyaltyPoints() : 0);
        wallet.setLoyaltyTier(walletDTO.getLoyaltyTier() != null ? walletDTO.getLoyaltyTier() : "BRONZE");
        Wallet saved = walletRepository.save(wallet);
        return convertToDTO(saved);
    }

    public WalletDTO updateWallet(Long id, WalletDTO walletDTO) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));
        if (walletDTO.getStatus() != null) {
            wallet.setStatus(walletDTO.getStatus());
        }
        Wallet saved = walletRepository.save(wallet);
        return convertToDTO(saved);
    }

    public void deleteWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));
        if (wallet.getBalance() > 0) {
            throw new RuntimeException("Cannot delete wallet with positive balance. Withdraw funds first.");
        }
        walletRepository.delete(wallet);
    }

    // ==================== BUSINESS RULES ====================

    @Transactional
    public WalletDTO deposit(Long walletId, Double amount) {
        validateAmount(amount);
        Wallet wallet = getWalletEntity(walletId);
        validateWalletActive(wallet);

        if (amount > MAX_SINGLE_TRANSACTION) {
            throw new RuntimeException("Deposit exceeds maximum single transaction limit of " + MAX_SINGLE_TRANSACTION);
        }

        wallet.deposit(amount);
        Wallet saved = walletRepository.save(wallet);
        return convertToDTO(saved);
    }

    @Transactional
    public WalletDTO withdraw(Long walletId, Double amount) {
        validateAmount(amount);
        Wallet wallet = getWalletEntity(walletId);
        validateWalletActive(wallet);

        if (amount > MAX_SINGLE_TRANSACTION) {
            throw new RuntimeException("Withdrawal exceeds maximum single transaction limit of " + MAX_SINGLE_TRANSACTION);
        }
        if (wallet.getBalance() - amount < MIN_BALANCE_THRESHOLD) {
            throw new RuntimeException("Withdrawal would leave balance below minimum threshold of " + MIN_BALANCE_THRESHOLD);
        }

        boolean success = wallet.withdraw(amount);
        if (!success) {
            throw new RuntimeException("Insufficient funds for withdrawal");
        }
        Wallet saved = walletRepository.save(wallet);
        return convertToDTO(saved);
    }

    @Transactional
    public WalletDTO transferTokens(Long senderWalletId, TransferRequest request) {
        validateAmount(request.getAmount());
        Wallet sender = getWalletEntity(senderWalletId);
        validateWalletActive(sender);

        Wallet recipient;
        if (request.getRecipientWalletId() != null) {
            recipient = getWalletEntity(request.getRecipientWalletId());
        } else if (request.getRecipientPublicKey() != null) {
            recipient = walletRepository.findByPublicKey(request.getRecipientPublicKey())
                    .orElseThrow(() -> new RuntimeException("Recipient wallet not found"));
        } else {
            throw new RuntimeException("Recipient wallet ID or public key is required");
        }

        validateWalletActive(recipient);

        if (sender.getWalletId().equals(recipient.getWalletId())) {
            throw new RuntimeException("Cannot transfer to the same wallet");
        }

        if (request.getAmount() > MAX_SINGLE_TRANSACTION) {
            throw new RuntimeException("Transfer exceeds maximum single transaction limit");
        }

        boolean sent = sender.sendTokens(request.getAmount(), recipient.getPublicKey());
        if (!sent) {
            throw new RuntimeException("Transfer failed: insufficient funds or wallet inactive");
        }
        recipient.receiveTokens(request.getAmount());

        walletRepository.save(sender);
        walletRepository.save(recipient);

        return convertToDTO(sender);
    }

    @Transactional
    public WalletDTO suspendWallet(Long walletId) {
        Wallet wallet = getWalletEntity(walletId);
        wallet.setStatus("SUSPENDED");
        return convertToDTO(walletRepository.save(wallet));
    }

    @Transactional
    public WalletDTO activateWallet(Long walletId) {
        Wallet wallet = getWalletEntity(walletId);
        if ("CLOSED".equals(wallet.getStatus())) {
            throw new RuntimeException("Cannot activate a closed wallet");
        }
        wallet.setStatus("ACTIVE");
        return convertToDTO(walletRepository.save(wallet));
    }

    public List<WalletDTO> getLowBalanceWallets(Double threshold) {
        return walletRepository.findLowBalanceWallets(threshold).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==================== HELPERS ====================

    private Wallet getWalletEntity(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));
    }

    private void validateWalletActive(Wallet wallet) {
        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new RuntimeException("Wallet is not active. Current status: " + wallet.getStatus());
        }
    }

    private void validateAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Amount must be positive");
        }
    }

    private String generatePublicKey() {
        return "PK-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
    }

    // ==================== CONVERTERS ====================

    private WalletDTO convertToDTO(Wallet wallet) {
        List<TokenDTO> tokenDTOs = wallet.getTokens() != null
                ? wallet.getTokens().stream().map(t -> TokenDTO.builder()
                    .tokenId(t.getTokenId())
                    .value(t.getValue())
                    .customerId(t.getCustomerId())
                    .conversionRate(t.getConversionRate())
                    .totalSupply(t.getTotalSupply())
                    .walletId(wallet.getWalletId())
                    .createdAt(t.getCreatedAt())
                    .updatedAt(t.getUpdatedAt())
                    .build()).collect(Collectors.toList())
                : List.of();

        List<AssetDTO> assetDTOs = wallet.getAssets() != null
                ? wallet.getAssets().stream().map(a -> AssetDTO.builder()
                    .assetId(a.getAssetId())
                    .ownerId(a.getOwnerId())
                    .assetType(a.getAssetType())
                    .value(a.getValue())
                    .status(a.getStatus())
                    .walletId(wallet.getWalletId())
                    .createdAt(a.getCreatedAt())
                    .updatedAt(a.getUpdatedAt())
                    .build()).collect(Collectors.toList())
                : List.of();

        return WalletDTO.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalance())
                .customerId(wallet.getCustomerId())
                .publicKey(wallet.getPublicKey())
                .status(wallet.getStatus())
                .currency(wallet.getCurrency())
                .loyaltyPoints(wallet.getLoyaltyPoints())
                .loyaltyTier(wallet.getLoyaltyTier())
                .transactionHistory(wallet.getTransactionHistory())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .tokens(tokenDTOs)
                .assets(assetDTOs)
                .build();
    }

    private Wallet convertToEntity(WalletDTO dto) {
        Wallet wallet = new Wallet();
        wallet.setCustomerId(dto.getCustomerId());
        wallet.setBalance(dto.getBalance() != null ? dto.getBalance() : 0.0);
        return wallet;
    }
}
