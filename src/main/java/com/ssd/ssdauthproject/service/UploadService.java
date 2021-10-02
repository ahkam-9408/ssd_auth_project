package com.ssd.ssdauthproject.service;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    String uploadToDrive(Credential cred, MultipartFile file) throws IOException;
}
