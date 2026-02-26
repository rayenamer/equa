package com.rayen.userManaement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * Pour les fournisseurs OpenID Connect (Google, etc.), Spring utilise OidcUserService et non OAuth2UserService.
 * Ce service charge l'utilisateur OIDC puis le synchronise en base (création ou mise à jour).
 */
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final OAuth2UserSyncService oauth2UserSyncService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        oauth2UserSyncService.findOrCreateFromOAuth2(registrationId, oidcUser);
        return oidcUser;
    }
}
