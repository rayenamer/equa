package com.rayen.walletManagement.controller;

import com.rayen.walletManagement.service.CashbackGamificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cashback")
@Tag(name = "Cashback & Gamification", description = "Systeme de cashback en tokens, recompenses pour usage frequent, gamification avec badges et challenges")
public class CashbackGamificationController {

    @Autowired
    private CashbackGamificationService cashbackGamificationService;

    // ==================== CASHBACK ====================

    @PostMapping("/{walletId}/process")
    @Operation(summary = "Traiter un cashback sur une transaction",
            description = "Calcule et credite le cashback selon le tier du wallet et le streak. Le cashback peut etre credite en balance ou en points de fidelite (tokens).")
    public ResponseEntity<Map<String, Object>> processCashback(
            @PathVariable Long walletId,
            @RequestParam Double transactionAmount,
            @RequestParam(defaultValue = "false") Boolean asTokens) {
        return ResponseEntity.ok(cashbackGamificationService.processCashback(walletId, transactionAmount, asTokens));
    }

    @GetMapping("/{walletId}/dashboard")
    @Operation(summary = "Dashboard cashback et gamification",
            description = "Vue complete : taux de cashback actuel, streak, badges, activite mensuelle, historique des recompenses")
    public ResponseEntity<Map<String, Object>> getCashbackDashboard(@PathVariable Long walletId) {
        return ResponseEntity.ok(cashbackGamificationService.getCashbackDashboard(walletId));
    }

    // ==================== CHALLENGES ====================

    @PostMapping("/challenges/init")
    @Operation(summary = "Initialiser les challenges par defaut",
            description = "Cree 6 challenges par defaut : First Transaction, Active Trader, Big Spender, Whale, Week Streak, Month Streak")
    public ResponseEntity<Map<String, Object>> initDefaultChallenges() {
        return ResponseEntity.ok(cashbackGamificationService.initDefaultChallenges());
    }

    @PostMapping("/challenges/create")
    @Operation(summary = "Creer un nouveau challenge personnalise",
            description = "Types: TRANSACTION_COUNT, TRANSACTION_VOLUME, STREAK, DEPOSIT_COUNT")
    public ResponseEntity<Map<String, Object>> createChallenge(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String challengeType,
            @RequestParam Double targetValue,
            @RequestParam Integer rewardPoints,
            @RequestParam Double cashbackBonus,
            @RequestParam String badgeName) {
        return ResponseEntity.ok(cashbackGamificationService.createChallenge(
                name, description, challengeType, targetValue, rewardPoints, cashbackBonus, badgeName));
    }

    @GetMapping("/challenges")
    @Operation(summary = "Lister les challenges actifs",
            description = "Retourne tous les challenges disponibles avec leurs objectifs et recompenses")
    public ResponseEntity<List<Map<String, Object>>> getActiveChallenges() {
        return ResponseEntity.ok(cashbackGamificationService.getActiveChallenges());
    }

    @GetMapping("/challenges/{walletId}/progress")
    @Operation(summary = "Progression des challenges pour un wallet",
            description = "Retourne la progression de chaque challenge actif avec le pourcentage d'avancement")
    public ResponseEntity<Map<String, Object>> getChallengeProgress(@PathVariable Long walletId) {
        return ResponseEntity.ok(cashbackGamificationService.getChallengeProgress(walletId));
    }

    // ==================== BADGES / GAMIFICATION ====================

    @GetMapping("/{walletId}/badges")
    @Operation(summary = "Badges gagnes par le wallet",
            description = "Liste tous les badges obtenus, groupes par categorie (STREAK, VOLUME, FREQUENCY, etc.)")
    public ResponseEntity<Map<String, Object>> getWalletBadges(@PathVariable Long walletId) {
        return ResponseEntity.ok(cashbackGamificationService.getWalletBadges(walletId));
    }

    // ==================== LEADERBOARD ====================

    @GetMapping("/leaderboard")
    @Operation(summary = "Classement des meilleurs wallets",
            description = "Top 10 wallets par cashback total gagne, avec badges et tier")
    public ResponseEntity<Map<String, Object>> getLeaderboard() {
        return ResponseEntity.ok(cashbackGamificationService.getLeaderboard());
    }
}
