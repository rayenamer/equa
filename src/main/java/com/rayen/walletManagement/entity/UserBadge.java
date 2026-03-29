package com.rayen.walletManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_badges")
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long walletId;

    @Column(nullable = false)
    private String badgeName;

    @Column(nullable = false)
    private String badgeIcon; // emoji or icon identifier

    private String description;

    @Column(nullable = false)
    private String category; // STREAK, VOLUME, FREQUENCY, TIER, SPECIAL

    private Long challengeId; // which challenge unlocked this badge (nullable for special badges)

    private LocalDateTime earnedAt;

    @PrePersist
    protected void onCreate() {
        earnedAt = LocalDateTime.now();
    }
}
