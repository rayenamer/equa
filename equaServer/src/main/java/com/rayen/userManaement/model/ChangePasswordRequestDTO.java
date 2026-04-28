package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {
    /** Mot de passe actuel (vérification). */
    private String currentPassword;
    /** Nouveau mot de passe. */
    private String newPassword;
}
