package com.rayen.userManaement.service;

import com.rayen.userManaement.controller.AuthController;
import com.rayen.userManaement.entity.AssistantUser;
import com.rayen.userManaement.entity.ObserverUser;
import com.rayen.userManaement.entity.User;
import com.rayen.userManaement.repository.ObserverUserRepository;
import com.rayen.userManaement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Trouve ou crée un utilisateur à partir des infos OAuth2 (Google, Microsoft/Outlook).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserSyncService {

    private final UserRepository userRepository;
    private final ObserverUserRepository observerUserRepository;

    @Transactional
    public User findOrCreateFromOAuth2(String registrationId, OAuth2User oauth2User) {
        String providerId = getProviderId(registrationId, oauth2User);
        String email = getEmail(registrationId, oauth2User);
        String name = getName(registrationId, oauth2User);
        String provider = registrationIdToProvider(registrationId);
        log.info("OAuth2 sync: registrationId={}, providerId={}, email={}, name={}", registrationId, providerId, email, name);

        var byProvider = userRepository.findByProviderIdAndAuthProvider(providerId, provider);
        if (byProvider.isPresent()) {
            log.info("OAuth2: utilisateur existant trouvé par providerId, id={}", byProvider.get().getId());
            return byProvider.get();
        }
        if (email != null && !email.isBlank()) {
            var byEmail = userRepository.findByEmailAndAuthProvider(email, provider);
            if (byEmail.isPresent()) {
                User u = byEmail.get();
                u.setProviderId(providerId);
                log.info("OAuth2: utilisateur existant mis à jour (email+provider), id={}", u.getId());
                return userRepository.save(u);
            }
            var existingEmail = userRepository.findByEmail(email);
            if (existingEmail.isPresent()) {
                User existing = existingEmail.get();
                existing.setAuthProvider(provider);
                existing.setProviderId(providerId);
                existing.setEmailVerified(true);
                log.info("OAuth2: compte local converti en OAuth, id={}", existing.getId());
                return userRepository.save(existing);
            }
        }

        String role = getOAuth2RoleFromSession();
        boolean asObserver = "OBSERVER".equalsIgnoreCase(role);

        try {
            if (asObserver) {
                ObserverUser newUser = new ObserverUser();
                newUser.setUsername(name != null && !name.isBlank() ? name : (email != null ? email : "User"));
                newUser.setEmail(email);
                newUser.setAuthProvider(provider);
                newUser.setProviderId(providerId);
                newUser.setPassword(null);
                newUser.setEmailVerified(true);
                User saved = observerUserRepository.save(newUser);
                log.info("OAuth2: nouvel utilisateur OBSERVER créé, id={}, email={}, username={}", saved.getId(), saved.getEmail(), saved.getUsername());
                return saved;
            } else {
                AssistantUser newUser = new AssistantUser();
                newUser.setUsername(name != null && !name.isBlank() ? name : (email != null ? email : "User"));
                newUser.setEmail(email);
                newUser.setAuthProvider(provider);
                newUser.setProviderId(providerId);
                newUser.setPassword(null);
                newUser.setEmailVerified(true);
                User saved = userRepository.save(newUser);
                log.info("OAuth2: nouvel utilisateur ASSISTANT créé, id={}, email={}, username={}", saved.getId(), saved.getEmail(), saved.getUsername());
                return saved;
            }
        } catch (DataIntegrityViolationException e) {
            // Connexion répétée ou doublon (ex. même lien utilisé deux fois) : rattacher au compte existant
            if (email != null && !email.isBlank()) {
                var existing = userRepository.findByEmail(email);
                if (existing.isPresent()) {
                    User u = existing.get();
                    u.setAuthProvider(provider);
                    u.setProviderId(providerId);
                    u.setEmailVerified(true);
                    log.info("OAuth2: utilisateur existant rattaché après conflit (email), id={}", u.getId());
                    return userRepository.save(u);
                }
            }
            throw e;
        }
    }

    /** Rôle en session (OBSERVER ou ASSISTANT). Par défaut OBSERVER si absent. */
    private String getOAuth2RoleFromSession() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                if (request != null && request.getSession(false) != null) {
                    Object role = request.getSession().getAttribute(AuthController.SESSION_OAUTH2_ROLE);
                    if (role instanceof String s && ("OBSERVER".equals(s) || "ASSISTANT".equals(s))) {
                        return s;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Session non disponible pour oauth2_signup_role: {}", e.getMessage());
        }
        return "OBSERVER";
    }

    private String getProviderId(String registrationId, OAuth2User oauth2User) {
        Map<String, Object> attrs = oauth2User.getAttributes();
        Object sub = attrs.get("sub");
        return sub != null ? sub.toString() : null;
    }

    private String getEmail(String registrationId, OAuth2User oauth2User) {
        Map<String, Object> attrs = oauth2User.getAttributes();
        Object email = attrs.get("email");
        if (email != null) return email.toString();
        if ("microsoft".equalsIgnoreCase(registrationId)) {
            Object mail = attrs.get("mail");
            if (mail != null) return mail.toString();
            Object upn = attrs.get("preferred_username");
            return upn != null ? upn.toString() : null;
        }
        return null;
    }

    private String getName(String registrationId, OAuth2User oauth2User) {
        Map<String, Object> attrs = oauth2User.getAttributes();
        return switch (registrationId.toLowerCase()) {
            case "google" -> (String) attrs.get("name");
            case "microsoft" -> (String) attrs.getOrDefault("displayName", attrs.get("name"));
            default -> (String) attrs.get("name");
        };
    }

    private String registrationIdToProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> "GOOGLE";
            case "microsoft" -> "MICROSOFT";
            default -> registrationId.toUpperCase();
        };
    }
}
