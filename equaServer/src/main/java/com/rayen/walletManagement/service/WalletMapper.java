package com.rayen.walletManagement.service;

import com.rayen.userManaement.entity.User;
import com.rayen.walletManagement.entity.DeviseWallet;
import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.CurrencyCode;
import com.rayen.walletManagement.model.DeviseWalletDTO;
import com.rayen.walletManagement.model.WalletDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WalletMapper {

    public WalletDTO toDTO(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        User user = wallet.getUser();
        Map<CurrencyCode, BigDecimal> balances = balancesOf(wallet.getDeviseWallet());

        return WalletDTO.builder()
                .id(wallet.getWalletId())
                .publicKey(wallet.getPublicKey())
                .status(wallet.getStatus())
                .balance(BigDecimal.valueOf(wallet.getBalance()))
                .equaAmount(BigDecimal.valueOf(wallet.getEquaAmount()))
                .ownerEmail(user == null ? null : user.getEmail())
                .loyaltyTier(wallet.getLoyaltyTier())
                .loyaltyPoints(wallet.getLoyaltyPoints())
                .fraudRiskLevel(wallet.getFraudRiskLevel() == null ? null : wallet.getFraudRiskLevel().name())
                .achievements(wallet.getAchievements())
                .completedChallenges(wallet.getCompletedChallenges())
                .currencyBalances(balances)
                .build();
    }

    public List<WalletDTO> toDTOList(List<Wallet> wallets) {
        if (wallets == null) {
            return Collections.emptyList();
        }
        return wallets.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DeviseWalletDTO toDTO(DeviseWallet deviseWallet) {
        if (deviseWallet == null) {
            return null;
        }
        return DeviseWalletDTO.builder()
                .id(deviseWallet.getId())
                .balances(balancesOf(deviseWallet))
                .build();
    }

    private Map<CurrencyCode, BigDecimal> balancesOf(DeviseWallet deviseWallet) {
        if (deviseWallet == null || deviseWallet.getBalances() == null) {
            return Collections.emptyMap();
        }
        Map<CurrencyCode, BigDecimal> snapshot = new EnumMap<>(CurrencyCode.class);
        for (CurrencyCode currency : CurrencyCode.values()) {
            snapshot.put(currency, deviseWallet.getBalance(currency));
        }
        return snapshot;
    }
}
