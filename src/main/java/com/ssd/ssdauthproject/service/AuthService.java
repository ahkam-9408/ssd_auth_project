package com.ssd.ssdauthproject.service;

import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;

public interface AuthService {
    boolean authenticateCredentials(Credential credential) throws IOException;
}
