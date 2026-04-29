package com.rayen.userManaement.controller;

import com.rayen.userManaement.model.AuthResponseDTO;
import com.rayen.userManaement.model.ChangePasswordRequestDTO;
import com.rayen.userManaement.model.ForgotPasswordRequestDTO;
import com.rayen.userManaement.model.ResetPasswordRequestDTO;
import com.rayen.userManaement.model.SigninRequestDTO;
import com.rayen.userManaement.model.SignupRequestDTO;
import com.rayen.userManaement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Inscription et connexion")
public class AuthController {

    private final AuthService authService;

//doner
    @PostMapping("/signup")
    @Operation(summary = "Inscription", description = "Créer un compte (ASSISTANT ou OBSERVER). Si reCAPTCHA est activé (app.recaptcha.secret-key), envoyer recaptchaToken (token « Je ne suis pas un robot »).")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody SignupRequestDTO request) {
        AuthResponseDTO response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
//done
    @PostMapping("/signin")
    @Operation(summary = "Connexion", description = "Se connecter avec email et mot de passe. Retourne un token JWT. Si reCAPTCHA est activé, envoyer recaptchaToken.")
    public ResponseEntity<AuthResponseDTO> signin(@RequestBody SigninRequestDTO request) {
        AuthResponseDTO response = authService.signin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Mot de passe oublié",
            description = "Envoie un email avec un lien de réinitialisation si un compte existe pour cet email (compte LOCAL uniquement). Réponse identique dans tous les cas (sécurité).")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        authService.requestPasswordReset(request.getEmail());
        Map<String, String> body = new HashMap<>();
        body.put("message", "Si un compte existe pour cet email, vous recevrez un lien pour réinitialiser votre mot de passe.");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Réinitialiser le mot de passe",
            description = "Réinitialise le mot de passe avec le token reçu par email (dans le lien).")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        Map<String, String> body = new HashMap<>();
        body.put("message", "Mot de passe mis à jour. Vous pouvez vous connecter.");
        return ResponseEntity.ok(body);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Changer son mot de passe (utilisateur connecté)",
            description = "Cliquez sur Authorize (en haut à droite), collez le token reçu après signin. Envoie le mot de passe actuel et le nouveau. Comptes LOCAL uniquement.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequestDTO request, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Non authentifié."));
        }
        String email = (String) authentication.getPrincipal();
        authService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());
        Map<String, String> body = new HashMap<>();
        body.put("message", "Mot de passe modifié. Vous pouvez continuer à utiliser votre session.");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/disable-account")
    @Operation(summary = "Désactiver son compte",
            description = "Désactive le compte de l'utilisateur connecté (JWT). La connexion sera refusée ensuite. Pas de réactivation.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> disableAccount(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Non authentifié."));
        }
        String email = (String) authentication.getPrincipal();
        authService.disableMyAccount(email);
        Map<String, String> body = new HashMap<>();
        body.put("message", "Compte désactivé. Vous ne pourrez plus vous connecter avec ce compte.");
        return ResponseEntity.ok(body);
    }

    /** Clé de session pour le rôle choisi avant OAuth2 (OBSERVER ou ASSISTANT). */
    public static final String SESSION_OAUTH2_ROLE = "oauth2_signup_role";

    @GetMapping("/oauth2/google-url")
    @Operation(summary = "Lien connexion Google",
            description = "Retourne l'URL pour se connecter avec Google. Paramètre optionnel: role=OBSERVER ou role=ASSISTANT (défaut: OBSERVER). Après connexion, vous serez redirigé avec ?token=... dans l'URL.")
    public ResponseEntity<Map<String, String>> getGoogleLoginUrl(
            @RequestParam(required = false) String role,
            HttpSession session) {
        if (role != null && ("OBSERVER".equalsIgnoreCase(role) || "ASSISTANT".equalsIgnoreCase(role))) {
            session.setAttribute(SESSION_OAUTH2_ROLE, role.toUpperCase());
        }
        Map<String, String> body = new HashMap<>();
        body.put("url", "/oauth2/authorization/google");
        body.put("message", "Ouvrez dans le navigateur: http://localhost:8081/oauth2/authorization/google" + (role != null ? " (rôle: " + role + ")" : ""));
        return ResponseEntity.ok(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
