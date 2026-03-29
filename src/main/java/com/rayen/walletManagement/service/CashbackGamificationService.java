package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.*;
import com.rayen.walletManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CashbackGamificationService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CashbackRewardRepository cashbackRewardRepository;

    @Autowired
    private LoyaltyChallengeRepository loyaltyChallengeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    // ==================== CASHBACK RATES BY TIER ====================

    private static final Map<String, Double> CASHBACK_RATES = new LinkedHashMap<>();
    static {
        CASHBACK_RATES.put("BRONZE", 1.0);    // 1% cashback
        CASHBACK_RATES.put("SILVER", 2.0);    // 2% cashback
        CASHBACK_RATES.put("GOLD", 3.5);      // 3.5% cashback
        CASHBACK_RATES.put("PLATINUM", 5.0);  // 5% cashback
    }

    // Streak bonus: extra cashback % per consecutive day
    private static final Double STREAK_BONUS_PER_DAY = 0.1; // +0.1% per streak day
    private static final Integer MAX_STREAK_BONUS_DAYS = 30; // max 30 days = +3%

    // ==================== 1. PROCESS CASHBACK ON TRANSACTION ====================

    @Transactional
    public Map<String, Object> processCashback(Long walletId, Double transactionAmount, Boolean asTokens) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new RuntimeException("Wallet is not active");
        }
        if (transactionAmount <= 0) {
            throw new RuntimeException("Transaction amount must be positive");
        }

        // Calculate base cashback rate from tier
        Double baseCashbackRate = CASHBACK_RATES.getOrDefault(wallet.getLoyaltyTier(), 1.0);

        // Calculate streak bonus
        int streakDays = calculateStreak(walletId);
        Double streakBonus = Math.min(streakDays * STREAK_BONUS_PER_DAY, MAX_STREAK_BONUS_DAYS * STREAK_BONUS_PER_DAY);

        Double totalRate = baseCashbackRate + streakBonus;
        Double cashbackAmount = transactionAmount * (totalRate / 100.0);

        // Credit cashback
        if (Boolean.TRUE.equals(asTokens)) {
            // Credit as loyalty points (tokens)
            int pointsEarned = (int) Math.ceil(cashbackAmount * 10); // 1 EUR = 10 points
            wallet.setLoyaltyPoints(wallet.getLoyaltyPoints() + pointsEarned);
        } else {
            // Credit as balance
            wallet.setBalance(wallet.getBalance() + cashbackAmount);
        }

        wallet.getTransactionHistory().add(
                LocalDateTime.now() + " | CASHBACK | +" + String.format("%.2f", cashbackAmount)
                        + (Boolean.TRUE.equals(asTokens) ? " (as tokens)" : " " + wallet.getCurrency())
                        + " | rate: " + String.format("%.1f", totalRate) + "%"
                        + (streakDays > 0 ? " | streak: " + streakDays + "d" : "")
        );

        walletRepository.save(wallet);

        // Save cashback record
        CashbackReward reward = new CashbackReward();
        reward.setWalletId(walletId);
        reward.setTransactionAmount(transactionAmount);
        reward.setCashbackPercentage(totalRate);
        reward.setCashbackAmount(cashbackAmount);
        reward.setRewardType("TRANSACTION_CASHBACK");
        reward.setCreditedAsTokens(asTokens);
        reward.setDescription("Cashback " + String.format("%.1f", totalRate) + "% on " + String.format("%.2f", transactionAmount) + " " + wallet.getCurrency());
        cashbackRewardRepository.save(reward);

        // Check challenges after transaction
        List<Map<String, Object>> completedChallenges = checkAndCompleteChallenges(walletId, wallet);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("walletId", walletId);
        result.put("transactionAmount", transactionAmount);
        result.put("tier", wallet.getLoyaltyTier());
        result.put("baseCashbackRate", baseCashbackRate + "%");
        result.put("streakDays", streakDays);
        result.put("streakBonus", "+" + String.format("%.1f", streakBonus) + "%");
        result.put("totalCashbackRate", String.format("%.1f", totalRate) + "%");
        result.put("cashbackAmount", cashbackAmount);
        result.put("creditedAs", Boolean.TRUE.equals(asTokens) ? "LOYALTY_POINTS" : "BALANCE");
        result.put("newBalance", wallet.getBalance());
        result.put("loyaltyPoints", wallet.getLoyaltyPoints());
        if (!completedChallenges.isEmpty()) {
            result.put("challengesCompleted", completedChallenges);
        }
        return result;
    }

    // ==================== 2. GET CASHBACK DASHBOARD ====================

    public Map<String, Object> getCashbackDashboard(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        Double totalCashback = cashbackRewardRepository.getTotalCashbackByWalletId(walletId);
        List<CashbackReward> recentRewards = cashbackRewardRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
        int streakDays = calculateStreak(walletId);
        List<UserBadge> badges = userBadgeRepository.findByWalletIdOrderByEarnedAtDesc(walletId);
        Long badgeCount = userBadgeRepository.countByWalletId(walletId);

        // Monthly stats
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Long monthlyTransactions = cashbackRewardRepository.countTransactionsSince(walletId, monthStart);
        Double monthlyVolume = cashbackRewardRepository.getTotalVolumeSince(walletId, monthStart);

        Double currentRate = CASHBACK_RATES.getOrDefault(wallet.getLoyaltyTier(), 1.0);
        Double streakBonus = Math.min(streakDays * STREAK_BONUS_PER_DAY, MAX_STREAK_BONUS_DAYS * STREAK_BONUS_PER_DAY);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("walletId", walletId);
        result.put("tier", wallet.getLoyaltyTier());
        result.put("loyaltyPoints", wallet.getLoyaltyPoints());

        // Cashback info
        Map<String, Object> cashbackInfo = new LinkedHashMap<>();
        cashbackInfo.put("currentBaseRate", currentRate + "%");
        cashbackInfo.put("streakDays", streakDays);
        cashbackInfo.put("streakBonus", "+" + String.format("%.1f", streakBonus) + "%");
        cashbackInfo.put("effectiveRate", String.format("%.1f", currentRate + streakBonus) + "%");
        cashbackInfo.put("totalCashbackEarned", totalCashback);
        result.put("cashback", cashbackInfo);

        // Monthly activity
        Map<String, Object> monthly = new LinkedHashMap<>();
        monthly.put("transactions", monthlyTransactions);
        monthly.put("volume", monthlyVolume);
        result.put("thisMonth", monthly);

        // Gamification
        Map<String, Object> gamification = new LinkedHashMap<>();
        gamification.put("totalBadges", badgeCount);
        gamification.put("badges", badges.stream().map(b -> {
            Map<String, Object> bm = new LinkedHashMap<>();
            bm.put("name", b.getBadgeName());
            bm.put("icon", b.getBadgeIcon());
            bm.put("category", b.getCategory());
            bm.put("earnedAt", b.getEarnedAt());
            return bm;
        }).collect(Collectors.toList()));
        result.put("gamification", gamification);

        // Cashback rates by tier
        result.put("allTierRates", CASHBACK_RATES.entrySet().stream().map(e -> {
            Map<String, Object> tm = new LinkedHashMap<>();
            tm.put("tier", e.getKey());
            tm.put("cashbackRate", e.getValue() + "%");
            return tm;
        }).collect(Collectors.toList()));

        // Recent rewards (last 10)
        result.put("recentRewards", recentRewards.stream().limit(10).map(r -> {
            Map<String, Object> rm = new LinkedHashMap<>();
            rm.put("type", r.getRewardType());
            rm.put("amount", r.getCashbackAmount());
            rm.put("rate", r.getCashbackPercentage() + "%");
            rm.put("asTokens", r.getCreditedAsTokens());
            rm.put("date", r.getCreatedAt());
            return rm;
        }).collect(Collectors.toList()));

        return result;
    }

    // ==================== 3. CHALLENGES MANAGEMENT ====================

    @Transactional
    public Map<String, Object> createChallenge(String name, String description, String challengeType,
                                                Double targetValue, Integer rewardPoints,
                                                Double cashbackBonus, String badgeName) {
        LoyaltyChallenge challenge = new LoyaltyChallenge();
        challenge.setName(name);
        challenge.setDescription(description);
        challenge.setChallengeType(challengeType);
        challenge.setTargetValue(targetValue);
        challenge.setRewardPoints(rewardPoints);
        challenge.setCashbackBonus(cashbackBonus);
        challenge.setBadgeName(badgeName);
        challenge.setActive(true);
        loyaltyChallengeRepository.save(challenge);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("challengeId", challenge.getId());
        result.put("name", name);
        result.put("type", challengeType);
        result.put("target", targetValue);
        result.put("rewardPoints", rewardPoints);
        result.put("cashbackBonus", cashbackBonus + "%");
        result.put("badge", badgeName);
        result.put("status", "ACTIVE");
        return result;
    }

    public List<Map<String, Object>> getActiveChallenges() {
        return loyaltyChallengeRepository.findByActiveTrue().stream().map(c -> {
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("challengeId", c.getId());
            cm.put("name", c.getName());
            cm.put("description", c.getDescription());
            cm.put("type", c.getChallengeType());
            cm.put("target", c.getTargetValue());
            cm.put("rewardPoints", c.getRewardPoints());
            cm.put("cashbackBonus", c.getCashbackBonus() + "%");
            cm.put("badge", c.getBadgeName());
            return cm;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getChallengeProgress(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        List<LoyaltyChallenge> activeChallenges = loyaltyChallengeRepository.findByActiveTrue();
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        Long txCount = cashbackRewardRepository.countTransactionsSince(walletId, monthStart);
        Double txVolume = cashbackRewardRepository.getTotalVolumeSince(walletId, monthStart);
        int streakDays = calculateStreak(walletId);

        List<Map<String, Object>> progress = activeChallenges.stream().map(c -> {
            Map<String, Object> pm = new LinkedHashMap<>();
            pm.put("challengeId", c.getId());
            pm.put("name", c.getName());
            pm.put("type", c.getChallengeType());
            pm.put("target", c.getTargetValue());

            Double currentValue = switch (c.getChallengeType()) {
                case "TRANSACTION_COUNT" -> txCount.doubleValue();
                case "TRANSACTION_VOLUME" -> txVolume;
                case "STREAK" -> (double) streakDays;
                case "DEPOSIT_COUNT" -> txCount.doubleValue();
                default -> 0.0;
            };

            pm.put("currentValue", currentValue);
            pm.put("progressPercent", Math.min(100.0, (currentValue / c.getTargetValue()) * 100.0));

            boolean alreadyCompleted = userBadgeRepository.existsByWalletIdAndChallengeId(walletId, c.getId());
            pm.put("completed", alreadyCompleted);
            pm.put("badge", c.getBadgeName());

            return pm;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("walletId", walletId);
        result.put("challenges", progress);
        return result;
    }

    // ==================== 4. BADGES / GAMIFICATION ====================

    public Map<String, Object> getWalletBadges(Long walletId) {
        walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        List<UserBadge> badges = userBadgeRepository.findByWalletIdOrderByEarnedAtDesc(walletId);
        Long totalBadges = userBadgeRepository.countByWalletId(walletId);

        // Group by category
        Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        for (UserBadge b : badges) {
            grouped.computeIfAbsent(b.getCategory(), k -> new ArrayList<>()).add(Map.of(
                    "name", b.getBadgeName(),
                    "icon", b.getBadgeIcon(),
                    "description", b.getDescription() != null ? b.getDescription() : "",
                    "earnedAt", b.getEarnedAt().toString()
            ));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("walletId", walletId);
        result.put("totalBadges", totalBadges);
        result.put("badgesByCategory", grouped);
        return result;
    }

    // ==================== 5. LEADERBOARD ====================

    public Map<String, Object> getLeaderboard() {
        List<Wallet> allWallets = walletRepository.findByStatus("ACTIVE");

        List<Map<String, Object>> leaderboard = allWallets.stream()
                .sorted((a, b) -> {
                    Double cashbackA = cashbackRewardRepository.getTotalCashbackByWalletId(a.getWalletId());
                    Double cashbackB = cashbackRewardRepository.getTotalCashbackByWalletId(b.getWalletId());
                    return cashbackB.compareTo(cashbackA);
                })
                .limit(10)
                .map(w -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("walletId", w.getWalletId());
                    entry.put("tier", w.getLoyaltyTier());
                    entry.put("loyaltyPoints", w.getLoyaltyPoints());
                    entry.put("totalCashback", cashbackRewardRepository.getTotalCashbackByWalletId(w.getWalletId()));
                    entry.put("badges", userBadgeRepository.countByWalletId(w.getWalletId()));
                    return entry;
                })
                .collect(Collectors.toList());

        // Add rank
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).put("rank", i + 1);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("leaderboard", leaderboard);
        result.put("totalParticipants", allWallets.size());
        return result;
    }

    // ==================== 6. INIT DEFAULT CHALLENGES ====================

    @Transactional
    public Map<String, Object> initDefaultChallenges() {
        List<LoyaltyChallenge> existing = loyaltyChallengeRepository.findByActiveTrue();
        if (!existing.isEmpty()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "Challenges already exist");
            result.put("count", existing.size());
            return result;
        }

        List<Object[]> defaults = List.of(
                new Object[]{"First Transaction", "Complete your first cashback transaction", "TRANSACTION_COUNT", 1.0, 50, 0.5, "Starter"},
                new Object[]{"Active Trader", "Complete 10 transactions this month", "TRANSACTION_COUNT", 10.0, 200, 1.0, "Active Trader"},
                new Object[]{"Big Spender", "Reach 5000 EUR in transaction volume", "TRANSACTION_VOLUME", 5000.0, 500, 1.5, "Big Spender"},
                new Object[]{"Whale", "Reach 50000 EUR in transaction volume", "TRANSACTION_VOLUME", 50000.0, 2000, 2.5, "Whale"},
                new Object[]{"Week Streak", "Maintain a 7-day transaction streak", "STREAK", 7.0, 150, 0.5, "Streak Master"},
                new Object[]{"Month Streak", "Maintain a 30-day transaction streak", "STREAK", 30.0, 1000, 2.0, "Unstoppable"}
        );

        List<Map<String, Object>> created = new ArrayList<>();
        for (Object[] d : defaults) {
            created.add(createChallenge(
                    (String) d[0], (String) d[1], (String) d[2],
                    (Double) d[3], (Integer) d[4], (Double) d[5], (String) d[6]
            ));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Default challenges created");
        result.put("challenges", created);
        return result;
    }

    // ==================== HELPERS ====================

    private int calculateStreak(Long walletId) {
        List<CashbackReward> rewards = cashbackRewardRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
        if (rewards.isEmpty()) return 0;

        int streak = 0;
        LocalDateTime checkDate = LocalDateTime.now().toLocalDate().atStartOfDay();

        Set<String> datesWithTransactions = rewards.stream()
                .map(r -> r.getCreatedAt().toLocalDate().toString())
                .collect(Collectors.toSet());

        for (int i = 0; i < MAX_STREAK_BONUS_DAYS + 1; i++) {
            String dateStr = checkDate.minusDays(i).toLocalDate().toString();
            if (datesWithTransactions.contains(dateStr)) {
                streak++;
            } else if (i > 0) {
                break; // streak broken
            }
        }

        return streak;
    }

    private List<Map<String, Object>> checkAndCompleteChallenges(Long walletId, Wallet wallet) {
        List<Map<String, Object>> completed = new ArrayList<>();
        List<LoyaltyChallenge> activeChallenges = loyaltyChallengeRepository.findByActiveTrue();

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Long txCount = cashbackRewardRepository.countTransactionsSince(walletId, monthStart);
        Double txVolume = cashbackRewardRepository.getTotalVolumeSince(walletId, monthStart);
        int streakDays = calculateStreak(walletId);

        for (LoyaltyChallenge challenge : activeChallenges) {
            // Skip if already completed
            if (userBadgeRepository.existsByWalletIdAndChallengeId(walletId, challenge.getId())) {
                continue;
            }

            Double currentValue = switch (challenge.getChallengeType()) {
                case "TRANSACTION_COUNT" -> txCount.doubleValue();
                case "TRANSACTION_VOLUME" -> txVolume;
                case "STREAK" -> (double) streakDays;
                case "DEPOSIT_COUNT" -> txCount.doubleValue();
                default -> 0.0;
            };

            if (currentValue >= challenge.getTargetValue()) {
                // Award badge
                UserBadge badge = new UserBadge();
                badge.setWalletId(walletId);
                badge.setBadgeName(challenge.getBadgeName());
                badge.setBadgeIcon(getBadgeIcon(challenge.getChallengeType()));
                badge.setDescription("Completed: " + challenge.getName());
                badge.setCategory(challenge.getChallengeType());
                badge.setChallengeId(challenge.getId());
                userBadgeRepository.save(badge);

                // Award points
                wallet.setLoyaltyPoints(wallet.getLoyaltyPoints() + challenge.getRewardPoints());
                walletRepository.save(wallet);

                // Log reward
                CashbackReward bonusReward = new CashbackReward();
                bonusReward.setWalletId(walletId);
                bonusReward.setTransactionAmount(0.0);
                bonusReward.setCashbackPercentage(challenge.getCashbackBonus());
                bonusReward.setCashbackAmount(0.0);
                bonusReward.setRewardType("CHALLENGE_REWARD");
                bonusReward.setCreditedAsTokens(true);
                bonusReward.setDescription("Challenge completed: " + challenge.getName() + " | +" + challenge.getRewardPoints() + " points");
                cashbackRewardRepository.save(bonusReward);

                Map<String, Object> cm = new LinkedHashMap<>();
                cm.put("challenge", challenge.getName());
                cm.put("badge", challenge.getBadgeName());
                cm.put("pointsAwarded", challenge.getRewardPoints());
                completed.add(cm);
            }
        }

        return completed;
    }

    private String getBadgeIcon(String challengeType) {
        return switch (challengeType) {
            case "TRANSACTION_COUNT" -> "STAR";
            case "TRANSACTION_VOLUME" -> "DIAMOND";
            case "STREAK" -> "FIRE";
            case "DEPOSIT_COUNT" -> "COIN";
            default -> "TROPHY";
        };
    }
}
