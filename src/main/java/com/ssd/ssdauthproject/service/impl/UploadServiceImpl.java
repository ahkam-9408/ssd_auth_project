package com.ssd.ssdauthproject.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.ssd.ssdauthproject.service.UploadService;
import com.ssd.ssdauthproject.util.Constants;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class UploadServiceImpl implements UploadService {
    @Override
    public String uploadToDrive(Credential cred, MultipartFile multipartFile) throws IOException {

        Drive drive = new Drive.Builder(Constants.HTTP_TRANSPORT, Constants.JSON_FACTORY, cred)
                .setApplicationName(Constants.APPLICATION_NAME).build();

        File file = new File();
        file.setName(multipartFile.getOriginalFilename());
        file.setMimeType(multipartFile.getContentType());
        java.io.File convertedFile = convertMultiPartToFile(multipartFile);
        FileContent content = new FileContent(file.getMimeType(), convertedFile);
        File uploadedFile = drive.files().create(file, content).setFields("id").execute();
        convertedFile.delete();

        return uploadedFile.getId();
    }

    /**
     * Converts multipart file to File object
     *
     * @param file multipart file submission
     * @return converted java.io.File object
     * @throws IOException
     */
    private java.io.File convertMultiPartToFile(MultipartFile file) throws IOException {

        java.io.File convertedFile = new java.io.File(Objects.requireNonNull(file.getOriginalFilename()));
        convertedFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }
}
