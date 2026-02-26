package com.rayen.userManaement.service;

import com.rayen.userManaement.entity.User;
import com.rayen.userManaement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Enregistre les échecs de connexion dans une transaction séparée pour que le compteur
 * soit bien persisté même quand signin lance une exception (rollback de la transaction principale).
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedLogin(Long userId, int attempts, LocalDateTime lockedUntil) {
        User u = userRepository.findById(userId).orElse(null);
        if (u != null) {
            u.setFailedLoginAttempts(attempts);
            u.setLockedUntil(lockedUntil);
            userRepository.save(u);
        }
    }
}
