package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {
    /** Token reçu par email (lien de réinitialisation). */
    private String token;
    /** Nouveau mot de passe. */
    private String newPassword;
}
