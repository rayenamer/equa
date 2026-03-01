package com.rayen.userManaement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigninRequestDTO {
    private String email;
    private String password;
    /** Token reCAPTCHA ("Je ne suis pas un robot") - requis si app.recaptcha.secret-key est configuré */
    private String recaptchaToken;
}
