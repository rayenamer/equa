package com.rayen.userManaement.service;

/**
 * Vérification du token reCAPTCHA ("Je ne suis pas un robot") côté serveur.
 */
public interface RecaptchaService {

    /**
     * Vérifie le token reCAPTCHA auprès de Google.
     * @param token token reçu du frontend (grecaptcha.getResponse())
     * @return true si la vérification est désactivée (pas de clé secrète) ou si Google valide le token
     * @throws IllegalArgumentException si le token est invalide ou expiré
     */
    boolean verify(String token);
}
