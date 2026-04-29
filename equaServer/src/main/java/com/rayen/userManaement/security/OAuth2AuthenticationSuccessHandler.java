package com.rayen.userManaement.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.rayen.userManaement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Après une connexion OAuth2 réussie (Google, Microsoft), génère un JWT et redirige vers le frontend avec le token.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Value("${app.oauth2.redirect-uri:http://localhost:4200/user/login}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = null;
        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            email = getEmail(oauth2User);
            if (email == null || email.isBlank()) {
                redirect(request, response, "error=no_email");
                return;
            }
            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && Boolean.FALSE.equals(userOpt.get().getEnabled())) {
                SecurityContextHolder.clearContext();
                redirect(request, response, "error=account_disabled");
                return;
            }
            String token = jwtUtils.generateToken(email);
            redirect(request, response, "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("OAuth2 success handler error (email={})", email, e);
            redirect(request, response, "error=server_error");
        }
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String queryParam) throws IOException {
        String sep = redirectUri.contains("?") ? "&" : "?";
        String targetUrl = redirectUri + sep + queryParam;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String getEmail(OAuth2User oauth2User) {
        Map<String, Object> attrs = oauth2User.getAttributes();
        if (attrs.containsKey("email")) return (String) attrs.get("email");
        if (attrs.containsKey("mail")) return (String) attrs.get("mail");
        if (attrs.containsKey("preferred_username")) return (String) attrs.get("preferred_username");
        return null;
    }
}
