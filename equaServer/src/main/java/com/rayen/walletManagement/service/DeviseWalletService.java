package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.DeviseWallet;
import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.CurrencyCode;
import com.rayen.walletManagement.model.FraudAssessment;
import com.rayen.walletManagement.model.WalletOperationType;
import com.rayen.walletManagement.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviseWalletService {

    private final WalletRepository walletRepository;
    private final WalletBalanceValidator validator;
    private final FraudDetectionService fraudDetectionService;
    private final LoyaltyService loyaltyService;
    private final GamificationService gamificationService;

    @Transactional
    public Wallet createWalletWithDevise(Wallet wallet) {
        if (wallet.getDeviseWallet() == null) {
            DeviseWallet deviseWallet = DeviseWallet.builder().build();
            deviseWallet.initializeBalances();
            deviseWallet.setWallet(wallet);
            wallet.setDeviseWallet(deviseWallet);
        }
        wallet.setStatus("ACTIVE");
        wallet.setEquaAmount(wallet.getEquaAmount() == null ? 0f : wallet.getEquaAmount());
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet addFunds(Long walletId, CurrencyCode currency, BigDecimal amount) {
        Wallet wallet = findActiveWallet(walletId);
        validator.validateAmount(amount);
        ensureDeviseWallet(wallet);

        wallet.getDeviseWallet().deposit(currency, amount);

        recordOperation(wallet, WalletOperationType.FUNDING, currency, amount);
        log.info("Added {} {} to wallet {}", amount, currency, walletId);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet removeFunds(Long walletId, CurrencyCode currency, BigDecimal amount) {
        Wallet wallet = findActiveWallet(walletId);
        validator.validateAmount(amount);
        ensureDeviseWallet(wallet);

        wallet.getDeviseWallet().withdraw(currency, amount);

        recordOperation(wallet, WalletOperationType.WITHDRAWAL, currency, amount);
        log.info("Removed {} {} from wallet {}", amount, currency, walletId);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet convertCurrency(Long walletId, CurrencyCode from, CurrencyCode to, BigDecimal amount) {
        Wallet wallet = findActiveWallet(walletId);
        validator.validateAmount(amount);
        ensureDeviseWallet(wallet);

        wallet.getDeviseWallet().withdraw(from, amount);
        ConversionResult result = calculateConversionWithFee(from, to, amount);
        wallet.getDeviseWallet().deposit(to, result.netAmount());

        recordOperation(wallet, WalletOperationType.CONVERSION, to, result.netAmount());
        log.info("Converted {} {} to {} {} in wallet {} (fee {})", amount, from, result.netAmount(), to, walletId, result.feeAmount());
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet transferBetweenWallets(Long sourceWalletId, CurrencyCode sourceCurrency, Long targetWalletId, CurrencyCode targetCurrency, BigDecimal amount) {
        if (sourceWalletId.equals(targetWalletId)) {
            throw new IllegalArgumentException("Cannot transfer to the same wallet");
        }

        Wallet source = findActiveWallet(sourceWalletId);
        Wallet target = findActiveWallet(targetWalletId);
        validator.validateAmount(amount);
        ensureDeviseWallet(source);
        ensureDeviseWallet(target);

        source.getDeviseWallet().withdraw(sourceCurrency, amount);
        ConversionResult result = calculateConversionWithFee(sourceCurrency, targetCurrency, amount);
        target.getDeviseWallet().deposit(targetCurrency, result.netAmount());

        recordOperation(source, WalletOperationType.TRANSFER, sourceCurrency, amount);
        recordOperation(target, WalletOperationType.TRANSFER, targetCurrency, result.netAmount());

        walletRepository.save(source);
        log.info("Transferred {} {} from wallet {} to {} {} in wallet {}", amount, sourceCurrency, sourceWalletId, result.netAmount(), targetCurrency, targetWalletId);
        return walletRepository.save(target);
    }

    private Wallet findActiveWallet(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));
    }

    private void ensureDeviseWallet(Wallet wallet) {
        if (wallet.getDeviseWallet() == null) {
            createWalletWithDevise(wallet);
        }
    }

    private void recordOperation(Wallet wallet, WalletOperationType operationType, CurrencyCode currency, BigDecimal amount) {
        fraudDetectionService.assess(wallet, operationType, currency, amount);
        loyaltyService.recordActivity(wallet, amount, operationType);
        gamificationService.evaluateAchievements(wallet, operationType);
        gamificationService.evaluateChallenges(wallet);
    }

    private ConversionResult calculateConversionWithFee(CurrencyCode from, CurrencyCode to, BigDecimal amount) {
        BigDecimal targetRaw = from.convertTo(to, amount);
        BigDecimal fee = targetRaw.multiply(BigDecimal.valueOf(0.005));
        BigDecimal net = targetRaw.subtract(fee);
        return new ConversionResult(net, fee);
    }

    private record ConversionResult(BigDecimal netAmount, BigDecimal feeAmount) {
    }
}
