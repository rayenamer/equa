package com.rayen.userManaement.service;

import com.rayen.userManaement.entity.AssistantUser;
import com.rayen.userManaement.entity.ObserverUser;
import com.rayen.userManaement.entity.Permission;
import com.rayen.userManaement.entity.User;
import com.rayen.userManaement.model.AuthResponseDTO;
import com.rayen.userManaement.model.SigninRequestDTO;
import com.rayen.userManaement.model.SignupRequestDTO;
import com.rayen.userManaement.repository.ObserverUserRepository;
import com.rayen.userManaement.repository.UserRepository;
import com.rayen.userManaement.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private final UserRepository userRepository;
    private final ObserverUserRepository observerUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final RecaptchaService recaptchaService;
    private final LoginAttemptService loginAttemptService;

    @Value("${app.password-reset.base-url:http://localhost:8081/swagger-ui.html}")
    private String passwordResetBaseUrl;
    @Value("${app.password-reset.token-validity-minutes:60}")
    private int passwordResetTokenValidityMinutes;
    @Value("${app.auth.max-login-attempts:3}")
    private int maxLoginAttempts;
    @Value("${app.auth.lockout-minutes:5}")
    private int lockoutMinutes;

    @Transactional
    public AuthResponseDTO signup(SignupRequestDTO request) {
        recaptchaService.verify(request.getRecaptchaToken());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        if (request.getPassword() == null || request.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères.");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user;
        if ("OBSERVER".equalsIgnoreCase(request.getUserType())) {
            ObserverUser observer = new ObserverUser();
            observer.setUsername(request.getUsername());
            observer.setEmail(request.getEmail());
            observer.setPassword(hashedPassword);
            observer.setAuthProvider("LOCAL");
            observer.setEmailVerified(false);
            if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
                observer.setPermissions(convertToPermissionEnums(request.getPermissions()));
            }
            user = observerUserRepository.save(observer);
        } else {
            AssistantUser assistant = new AssistantUser();
            assistant.setUsername(request.getUsername());
            assistant.setEmail(request.getEmail());
            assistant.setPassword(hashedPassword);
            assistant.setAuthProvider("LOCAL");
            assistant.setEmailVerified(false);
            user = userRepository.save(assistant);
        }

        String token = jwtUtils.generateToken(user.getEmail());
        String userType = user instanceof ObserverUser ? "OBSERVER" : "ASSISTANT";
        return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getEmail(), userType);
    }

    @Transactional
    public AuthResponseDTO signin(SigninRequestDTO request) {
        recaptchaService.verify(request.getRecaptchaToken());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new IllegalArgumentException("Compte désactivé. Contactez l'administrateur.");
        }

        String provider = user.getAuthProvider() != null ? user.getAuthProvider() : "LOCAL";
        if (!"LOCAL".equals(provider)) {
            throw new IllegalArgumentException("Connectez-vous avec " + provider);
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil())) {
            throw new IllegalArgumentException("Compte verrouillé après " + maxLoginAttempts + " tentatives incorrectes. Réessayez après " + user.getLockedUntil() + ".");
        }
        if (user.getLockedUntil() != null && !LocalDateTime.now().isBefore(user.getLockedUntil())) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1;
            LocalDateTime lockedUntil = attempts >= maxLoginAttempts ? LocalDateTime.now().plusMinutes(lockoutMinutes) : null;
            loginAttemptService.recordFailedLogin(user.getId(), attempts, lockedUntil);
            if (attempts >= maxLoginAttempts) {
                throw new IllegalArgumentException("Mot de passe incorrect. Compte verrouillé pendant " + lockoutMinutes + " minute(s). Réessayez après " + lockedUntil + ".");
            }
            int restantes = maxLoginAttempts - attempts;
            throw new IllegalArgumentException("Mot de passe incorrect. " + restantes + " tentative(s) restante(s) avant verrouillage.");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getEmail());
        String userType = user instanceof ObserverUser ? "OBSERVER" : "ASSISTANT";
        return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getEmail(), userType);
    }

    /**
     * Demande de réinitialisation du mot de passe. Envoie un email avec un lien si un compte LOCAL existe.
     * On ne révèle pas si l'email existe ou non (sécurité).
     */
    @Transactional
    public void requestPasswordReset(String email) {
        if (email == null || email.isBlank()) return;
        Optional<User> opt = userRepository.findByEmail(email.trim());
        if (opt.isEmpty()) return;
        User user = opt.get();
        String provider = user.getAuthProvider() != null ? user.getAuthProvider() : "LOCAL";
        if (!"LOCAL".equals(provider) || user.getEmail() == null || user.getEmail().isBlank()) return;
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(passwordResetTokenValidityMinutes));
        userRepository.save(user);
        String resetLink = passwordResetBaseUrl + (passwordResetBaseUrl.contains("?") ? "&" : "?") + "token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    /**
     * Réinitialise le mot de passe avec le token reçu par email.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token invalide ou expiré.");
        }
        User user = userRepository.findByPasswordResetToken(token.trim())
                .orElseThrow(() -> new IllegalArgumentException("Token invalide ou expiré."));
        if (user.getPasswordResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getPasswordResetTokenExpiry())) {
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
            userRepository.save(user);
            throw new IllegalArgumentException("Token invalide ou expiré.");
        }
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    /**
     * Changement de mot de passe par un utilisateur déjà connecté (JWT).
     * Vérifie le mot de passe actuel puis met à jour. Réinitialise verrouillage et tentatives.
     */
    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Utilisateur non identifié.");
        }
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        String provider = user.getAuthProvider() != null ? user.getAuthProvider() : "LOCAL";
        if (!"LOCAL".equals(provider)) {
            throw new IllegalArgumentException("Le changement de mot de passe n'est pas disponible pour les comptes " + provider + ".");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Ce compte n'a pas de mot de passe à modifier.");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect.");
        }
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    /**
     * Désactive le compte de l'utilisateur connecté (enabled = false).
     * La connexion sera refusée ensuite. Pas de réactivation dans ce métier.
     */
    @Transactional
    public void disableMyAccount(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Utilisateur non identifié.");
        }
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        user.setEnabled(false);
        userRepository.save(user);
    }

    private List<Permission> convertToPermissionEnums(List<String> permissionStrings) {
        return permissionStrings.stream()
                .map(str -> {
                    try {
                        return Permission.valueOf(str.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid permission: " + str);
                    }
                })
                .collect(Collectors.toList());
    }
}
