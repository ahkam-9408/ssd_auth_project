package com.ssd.ssdauthproject.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.ssd.ssdauthproject.service.AuthService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public boolean authenticateCredentials(Credential credential) throws IOException {

        boolean isAuthenticated = false;

        if (credential != null) {
            boolean tokenValid = credential.refreshToken();
            if (tokenValid) {
                isAuthenticated = true;
            }
        }

        return isAuthenticated;
    }
}
