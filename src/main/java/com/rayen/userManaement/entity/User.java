package com.rayen.userManaement.entity;

import com.rayen.walletManagement.entity.Wallet;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING, length = 30)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Nombre de tentatives de connexion échouées. Réinitialisé à 0 après succès. Nullable pour migration (lignes existantes). */
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    /** Compte verrouillé jusqu'à cette date (après trop de tentatives). */
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    /** Compte vérifié par email. Nullable pour migration (lignes existantes). */
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expiry")
    private LocalDateTime emailVerificationTokenExpiry;

    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /** LOCAL, GOOGLE, FACEBOOK, MICROSOFT. Nullable pour migration (lignes existantes). */
    @Column(name = "auth_provider", length = 30)
    private String authProvider = "LOCAL";

    /** ID utilisateur fourni par le fournisseur OAuth (null si LOCAL). */
    @Column(name = "provider_id", length = 255)
    private String providerId;

    /** Compte actif (false = désactivé, connexion refusée). Nullable pour migration. */
    @Column(name = "enabled")
    private Boolean enabled = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToOne
    @JoinColumn(name = "wallet_id", nullable = true, unique = true)
    private Wallet wallet;
}
