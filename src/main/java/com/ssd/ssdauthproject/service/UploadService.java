package com.ssd.ssdauthproject.service;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * All functions of the upload service
 */
public interface UploadService {

     /**
      * Uploads file to Google Drive
      * @param cred Credential object
      * @param file Multipart file uploaded
      * @return uploaded file id
      * @throws IOException
      */
     String uploadToDrive(Credential cred, MultipartFile file) throws IOException;

}
