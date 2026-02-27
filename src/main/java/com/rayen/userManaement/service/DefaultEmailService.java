package com.rayen.userManaement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implémentation par défaut : log le lien en console (dev). En prod, brancher un vrai envoi SMTP (JavaMailSender, etc.).
 */
@Slf4j
@Service
public class DefaultEmailService implements EmailService {

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        log.info("=== Email MDP oublié (dev - pas d'envoi réel) === To: {} | Lien: {}", to, resetLink);
        // TODO: en prod utiliser JavaMailSender pour envoyer un vrai email
    }
}
