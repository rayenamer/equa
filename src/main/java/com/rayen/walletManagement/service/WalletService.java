package com.rayen.walletManagement.service;

import com.rayen.AuthContextService;
import com.rayen.blockChainManagement.entity.DinarWallet;
import com.rayen.blockChainManagement.repository.DinarWalletRepository;
import com.rayen.blockChainManagement.service.EquaValuationEngine;
import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.repository.WalletRepository;
import com.rayen.userManaement.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final DinarWalletRepository dinarWalletRepository;
    private final EquaValuationEngine equaValuationEngine;
    private final AuthContextService authContextService;

    // ─── CRUD ────────────────────────────────────────────────

    @Transactional
    public Wallet createWallet() {
        User user = authContextService.getLoggedInUser();

        if (walletRepository.existsByUser_Id(user.getId()))
            throw new RuntimeException("Wallet already exists for userId: " + user.getId());

        Wallet wallet = Wallet.builder()
                .balance(0f)
                .equaAmount(0f)
                .status("ACTIVE")
                .publicKey("pk-" + user.getId() + "-" + System.currentTimeMillis())
                .user(user)
                .build();

        log.info("[WalletService] Wallet created for userId: {}", user.getId());
        return walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public Wallet getMyWallet() {
        Long userId = authContextService.getLoggedInUserId();
        return walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));
    }

    @Transactional(readOnly = true)
    public Wallet getWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));
    }

    @Transactional(readOnly = true)
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @Transactional
    public void deleteMyWallet() {
        Long userId = authContextService.getLoggedInUserId();
        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));
        walletRepository.delete(wallet);
        log.info("[WalletService] Wallet deleted for userId: {}", userId);
    }

    // ─── CONVERT DINARS → EQUA ───────────────────────────────

    @Transactional
    public Wallet convertDinarsToEqua(BigDecimal amountInDinars) {
        Long userId = authContextService.getLoggedInUserId();

        DinarWallet dinarWallet = dinarWalletRepository.findByUserId(userId.toString())
                .orElseThrow(() -> new RuntimeException("DinarWallet not found for userId: " + userId));

        if (dinarWallet.getBalance().compareTo(amountInDinars) < 0)
            throw new RuntimeException("Insufficient Dinar balance");

        BigDecimal rate = equaValuationEngine.getCurrentRate();
        BigDecimal equaReceived = amountInDinars.divide(rate, 8, RoundingMode.HALF_UP);

        dinarWallet.setBalance(dinarWallet.getBalance().subtract(amountInDinars));
        dinarWalletRepository.save(dinarWallet);

        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));

        wallet.setEquaAmount(wallet.getEquaAmount() + equaReceived.floatValue());

        log.info("[WalletService] userId:{} converted {} DT → {} EQUA @ rate={}", userId, amountInDinars, equaReceived, rate);
        return walletRepository.save(wallet);
    }
}