package com.ssd.ssdauthproject.service;

import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;

/**
 * All functions of the Authentication service
 */
public interface AuthService {

    /**
     * Function which validate authentication status of user
     * @param credential Credential object
     * @return authentication status
     * @throws IOException
     */
    boolean authenticateCredentials(Credential credential) throws IOException;
}
