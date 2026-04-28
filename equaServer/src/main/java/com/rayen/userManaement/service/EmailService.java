package com.rayen.userManaement.service;

/**
 * Envoi d'emails (réinitialisation MDP, vérification email, etc.).
 */
public interface EmailService {

    /**
     * Envoie un email avec le lien de réinitialisation du mot de passe.
     * @param to      adresse email du destinataire
     * @param resetLink URL complète du lien (ex. https://monapp.com/reset-password?token=xxx)
     */
    void sendPasswordResetEmail(String to, String resetLink);
}
