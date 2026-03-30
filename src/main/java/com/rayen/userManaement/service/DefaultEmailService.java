package com.rayen.userManaement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implémentation SMTP via JavaMailSender.
 */
@Slf4j
@Service
public class DefaultEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public DefaultEmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from:${spring.mail.username:no-reply@equa.local}}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("EQUA - Reinitialisation du mot de passe");
        message.setText(
                "Suite à votre demande, Equa vous contacte afin de procéder à la réinitialisation de votre mot de passe.  \n\n" +
                "Pour définir un nouveau mot de passe, nous vous invitons à cliquer sur le lien sécurisé ci-dessous :\n" +
                resetLink + "\n\n" +
                "Ce lien est temporaire et expirera automatiquement conformément à notre politique de sécurité de la plateforme.\n\n" +
                "Si vous n’êtes pas à l’origine de cette demande, vous pouvez ignorer ce message en toute sécurité,\n" +
                "Cordialement,\n" +
                        "Léquipe EQUA\n"
        );

        try {
            mailSender.send(message);
            log.info("Email de reinitialisation envoye a {}", to);
        } catch (MailException ex) {
            log.error("Echec d'envoi email de reinitialisation vers {}", to, ex);
            throw new IllegalStateException("Impossible d'envoyer l'email de reinitialisation.");
        }
    }
}
