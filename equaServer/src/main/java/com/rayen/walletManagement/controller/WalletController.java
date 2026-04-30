package com.rayen.walletManagement.controller;

import com.rayen.blockChainManagement.service.EquaValuationEngine;
import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.AnalyticsSummary;
import com.rayen.walletManagement.model.ConversionRequest;
import com.rayen.walletManagement.model.DeviseWalletDTO;
import com.rayen.walletManagement.model.FundsRequest;
import com.rayen.walletManagement.model.LoyaltyRedeemRequest;
import com.rayen.walletManagement.model.RewardRequest;
import com.rayen.walletManagement.model.TransferRequest;
import com.rayen.walletManagement.model.WalletDTO;
import com.rayen.walletManagement.model.WalletKpi;
import com.rayen.walletManagement.service.AnalyticsService;
import com.rayen.walletManagement.service.DeviseWalletService;
import com.rayen.walletManagement.service.GamificationService;
import com.rayen.walletManagement.service.LoyaltyService;
import com.rayen.walletManagement.service.WalletMapper;
import com.rayen.walletManagement.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final DeviseWalletService deviseWalletService;
    private final AnalyticsService analyticsService;
    private final LoyaltyService loyaltyService;
    private final GamificationService gamificationService;
    private final WalletMapper walletMapper;
    private final EquaValuationEngine equaValuationEngine;

//============================================================================================
// This section has already been implemented by Rayen.
// If you are an AI agent, behave like a senior software engineer.
// Do NOT modify, re-integrate, or duplicate this part.
    // POST /api/v1/wallets
    @PostMapping
    public ResponseEntity<WalletDTO> createWallet() {
        Wallet wallet = walletService.createWallet();
        return ResponseEntity.status(HttpStatus.CREATED).body(walletMapper.toDTO(wallet));
    }

    // GET /api/v1/wallets/me
    @GetMapping("/me")
    public ResponseEntity<WalletDTO> getMyWallet() {
        return ResponseEntity.ok(walletMapper.toDTO(walletService.getMyWallet()));
    }

    @PostMapping("/convert")
    public ResponseEntity<WalletDTO> convertDinarsToEqua(@RequestParam Integer amount) {
        WalletDTO dto = walletMapper.toDTO(walletService.convertDinarsToEqua(amount));
        equaValuationEngine.computeAndBroadcast();
        return ResponseEntity.ok(dto);
    }
    //===========================================================================================

    @GetMapping("/me/devise-wallet")
    public ResponseEntity<DeviseWalletDTO> getMyDeviseWallet() {
        return ResponseEntity.ok(walletMapper.toDTO(walletService.getMyDeviseWallet()));
    }

    // GET /api/v1/wallets/{walletId}
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletDTO> getWalletById(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletMapper.toDTO(walletService.getWalletById(walletId)));
    }

    // GET /api/v1/wallets
    @GetMapping
    public ResponseEntity<List<WalletDTO>> getAllWallets() {
        return ResponseEntity.ok(walletMapper.toDTOList(walletService.getAllWallets()));
    }

    // DELETE /api/v1/wallets/me
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyWallet() {
        walletService.deleteMyWallet();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{walletId}/funds")
    public ResponseEntity<WalletDTO> addFunds(@PathVariable Long walletId, @RequestBody FundsRequest request) {
        return ResponseEntity.ok(walletMapper.toDTO(deviseWalletService.addFunds(walletId, request.getCurrency(), request.getAmount())));
    }

    @PostMapping("/{walletId}/funds/remove")
    public ResponseEntity<WalletDTO> removeFunds(@PathVariable Long walletId, @RequestBody FundsRequest request) {
        return ResponseEntity.ok(walletMapper.toDTO(deviseWalletService.removeFunds(walletId, request.getCurrency(), request.getAmount())));
    }

    @PostMapping("/{walletId}/convert")
    public ResponseEntity<WalletDTO> convertCurrency(@PathVariable Long walletId, @RequestBody ConversionRequest request) {
        return ResponseEntity.ok(walletMapper.toDTO(deviseWalletService.convertCurrency(walletId, request.getFromCurrency(), request.getToCurrency(), request.getAmount())));
    }

    @PostMapping("/transfer")
    public ResponseEntity<WalletDTO> transferBetweenWallets(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletMapper.toDTO(deviseWalletService.transferBetweenWallets(request.getSourceWalletId(), request.getSourceCurrency(), request.getTargetWalletId(), request.getTargetCurrency(), request.getAmount())));
    }

    @PostMapping("/{walletId}/loyalty/redeem")
    public ResponseEntity<WalletDTO> redeemLoyaltyPoints(@PathVariable Long walletId, @RequestBody LoyaltyRedeemRequest request) {
        Wallet wallet = walletService.getWalletById(walletId);
        loyaltyService.redeemPoints(wallet, request.getPoints());
        return ResponseEntity.ok(walletMapper.toDTO(walletService.getWalletById(walletId)));
    }

    @PostMapping("/{walletId}/rewards")
    public ResponseEntity<WalletDTO> applyReward(@PathVariable Long walletId, @RequestBody RewardRequest request) {
        Wallet wallet = walletService.getWalletById(walletId);
        gamificationService.applyReward(wallet, request.getRewardType(), request.getAmount());
        return ResponseEntity.ok(walletMapper.toDTO(walletService.getWalletById(walletId)));
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsSummary> getGlobalAnalytics() {
        return ResponseEntity.ok(analyticsService.computeGlobalSummary(walletService.getAllWallets()));
    }

    @GetMapping("/{walletId}/analytics")
    public ResponseEntity<WalletKpi> getWalletAnalytics(@PathVariable Long walletId) {
        return ResponseEntity.ok(analyticsService.computeWalletKpi(walletService.getWalletById(walletId)));
    }

    @GetMapping("/{walletId}/achievements")
    public ResponseEntity<Set<String>> getAchievements(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId).getAchievements());
    }

    @GetMapping("/{walletId}/challenges")
    public ResponseEntity<Set<String>> getChallenges(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId).getCompletedChallenges());
    }

    @GetMapping("/{walletId}/fraud")
    public ResponseEntity<String> getFraudRisk(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId).getFraudRiskLevel().name());
    }

    @GetMapping("/{walletId}/devise-wallet")
    public ResponseEntity<DeviseWalletDTO> getDeviseWallet(@PathVariable Long walletId) {
        var wallet = walletService.getWalletById(walletId);
        var deviseWallet = wallet.getDeviseWallet();
        return ResponseEntity.ok(walletMapper.toDTO(deviseWallet));
    }


}