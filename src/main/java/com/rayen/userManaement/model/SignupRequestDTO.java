package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {
    private String username;
    private String email;
    private String password;
    /** ASSISTANT ou OBSERVER */
    private String userType;
    /** Permissions si userType = OBSERVER (ex. VIEW_LOGS, AUDIT) */
    private List<String> permissions;
    /** Token reCAPTCHA ("Je ne suis pas un robot") - requis si app.recaptcha.secret-key est configuré */
    private String recaptchaToken;
}
