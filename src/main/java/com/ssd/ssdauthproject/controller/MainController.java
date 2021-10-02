package com.ssd.ssdauthproject.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.ssd.ssdauthproject.service.AuthService;
import com.ssd.ssdauthproject.service.UploadService;

import com.ssd.ssdauthproject.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;


@Controller
public class MainController {

    //Creating a logger object to perform logging operations
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    UploadService uploadService;

    @Autowired
    AuthService authService;

    @Autowired
    ResourceLoader resourceLoader;

    //Path to credentials.json file as defined in yml
    @Value("${google.secret.key.path}")
    private String pathDriveSecretKey;

    //Path to where temporary credentials will be stored once authentication is successful, as defined in yml
    @Value("${google.credentials.folder.path}")
    private String pathCredentialFolder;

    //Callback url defined in yml
    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    //GoogleAuthorizationCodeFlow object
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    //initializing Google Authentication process
    @PostConstruct
    public void init() throws Exception {

        GoogleClientSecrets secrets = GoogleClientSecrets.load(Constants.JSON_FACTORY,
                new InputStreamReader(resourceLoader.getResource(pathDriveSecretKey).getInputStream()));
        googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(Constants.HTTP_TRANSPORT, Constants.JSON_FACTORY, secrets, Constants.SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(resourceLoader.getResource(pathCredentialFolder).getFile())).build();
    }

//  ========================================================================

    @GetMapping("/uploads")
    public String loadIndexPage() throws Exception {
        return "redirect:/uploads.html";
    }

    @GetMapping("/signingoogle")
    public void doGoogleSignIn(HttpServletResponse response) throws Exception {
        GoogleAuthorizationCodeRequestUrl url = googleAuthorizationCodeFlow.newAuthorizationUrl();
        String redirectURL = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
        logger.info("Redirect URL {} ", redirectURL);
        response.sendRedirect(redirectURL);
    }

    @GetMapping("/upload")
    public String uploadFileToDrive(@RequestParam("file") MultipartFile multipartFile) throws Exception {

        Credential cred = googleAuthorizationCodeFlow.loadCredential(Constants.USER_IDENTIFIER_KEY);
        String id = uploadService.uploadToDrive(cred, multipartFile);
        logger.info("Uploaded file id - {} ", id);
        return "redirect:/uploader";

    }
}
