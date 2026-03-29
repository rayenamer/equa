package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Token;
import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.TokenDTO;
import com.rayen.walletManagement.repository.TokenRepository;
import com.rayen.walletManagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private WalletRepository walletRepository;

    private static final Double FIXED_CONVERSION_RATE = 1.0;
    private static final Double MAX_CONVERSION_RATE = 2.0;
    private static final Double MIN_CONVERSION_RATE = 0.5;

    // ==================== CRUD ====================

    public List<TokenDTO> getAllTokens() {
        return tokenRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TokenDTO getTokenById(Long id) {
        Token token = tokenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found with id: " + id));
        return convertToDTO(token);
    }

    public List<TokenDTO> getTokensByWalletId(Long walletId) {
        return tokenRepository.findByWalletWalletId(walletId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TokenDTO> getTokensByCustomerId(Long customerId) {
        return tokenRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TokenDTO createToken(TokenDTO tokenDTO) {
        Wallet wallet = walletRepository.findById(tokenDTO.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + tokenDTO.getWalletId()));

        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new RuntimeException("Cannot create token for inactive wallet");
        }

        Token token = convertToEntity(tokenDTO);
        token.setWallet(wallet);

        // Enforce stable conversion rate (anti-volatility)
        Double rate = tokenDTO.getConversionRate() != null ? tokenDTO.getConversionRate() : FIXED_CONVERSION_RATE;
        if (rate < MIN_CONVERSION_RATE || rate > MAX_CONVERSION_RATE) {
            throw new RuntimeException("Conversion rate must be between " + MIN_CONVERSION_RATE + " and " + MAX_CONVERSION_RATE);
        }
        token.setConversionRate(rate);

        Token saved = tokenRepository.save(token);
        return convertToDTO(saved);
    }

    public TokenDTO updateToken(Long id, TokenDTO tokenDTO) {
        Token token = tokenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found with id: " + id));

        if (tokenDTO.getValue() != null) {
            token.setValue(tokenDTO.getValue());
        }
        if (tokenDTO.getConversionRate() != null) {
            if (tokenDTO.getConversionRate() < MIN_CONVERSION_RATE || tokenDTO.getConversionRate() > MAX_CONVERSION_RATE) {
                throw new RuntimeException("Conversion rate must be between " + MIN_CONVERSION_RATE + " and " + MAX_CONVERSION_RATE);
            }
            token.setConversionRate(tokenDTO.getConversionRate());
        }
        if (tokenDTO.getTotalSupply() != null) {
            token.setTotalSupply(tokenDTO.getTotalSupply());
        }

        Token saved = tokenRepository.save(token);
        return convertToDTO(saved);
    }

    public void deleteToken(Long id) {
        Token token = tokenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found with id: " + id));
        tokenRepository.delete(token);
    }

    // ==================== BUSINESS RULES ====================

    @Transactional
    public TokenDTO transferToken(Long tokenId, Long recipientCustomerId, Double amount) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found with id: " + tokenId));

        if (amount <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }

        boolean success = token.transfer(recipientCustomerId, amount);
        if (!success) {
            throw new RuntimeException("Transfer failed: insufficient token value");
        }

        // Credit recipient wallet
        Wallet recipientWallet = walletRepository.findByCustomerId(recipientCustomerId)
                .orElseThrow(() -> new RuntimeException("Recipient wallet not found"));

        Double convertedAmount = amount * token.getConversionRate();
        recipientWallet.receiveTokens(convertedAmount);

        // Update sender wallet transaction history
        Wallet senderWallet = token.getWallet();
        senderWallet.getTransactionHistory().add(
                java.time.LocalDateTime.now() + " | TOKEN_TRANSFER | -" + amount + " tokens | to customer: " + recipientCustomerId
        );

        walletRepository.save(recipientWallet);
        walletRepository.save(senderWallet);
        Token saved = tokenRepository.save(token);

        return convertToDTO(saved);
    }

    public Double getTotalTokenValue(Long walletId) {
        Double total = tokenRepository.getTotalConvertedValueByWalletId(walletId);
        return total != null ? total : 0.0;
    }

    // ==================== CONVERTERS ====================

    private TokenDTO convertToDTO(Token token) {
        return TokenDTO.builder()
                .tokenId(token.getTokenId())
                .value(token.getValue())
                .customerId(token.getCustomerId())
                .conversionRate(token.getConversionRate())
                .totalSupply(token.getTotalSupply())
                .walletId(token.getWallet() != null ? token.getWallet().getWalletId() : null)
                .createdAt(token.getCreatedAt())
                .updatedAt(token.getUpdatedAt())
                .build();
    }

    private Token convertToEntity(TokenDTO dto) {
        Token token = new Token();
        token.setValue(dto.getValue() != null ? dto.getValue() : 0.0);
        token.setCustomerId(dto.getCustomerId());
        token.setConversionRate(dto.getConversionRate() != null ? dto.getConversionRate() : FIXED_CONVERSION_RATE);
        token.setTotalSupply(dto.getTotalSupply() != null ? dto.getTotalSupply() : 0);
        return token;
    }
}
