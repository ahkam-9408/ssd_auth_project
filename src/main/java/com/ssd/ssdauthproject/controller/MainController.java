package com.ssd.ssdauthproject.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.ssd.ssdauthproject.credentials.Credential;
import com.ssd.ssdauthproject.model.CreateRepo;
import com.ssd.ssdauthproject.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;

@Controller
public class MainController {
    final String githubLoginURL = "https://github.com/login/oauth/authorize?" +
            "&client_id=" + Credential.clientId +
            "&redirect_uri=" + Constants.REDIRECT_URL +
            "&scope="+ Constants.SCOPE +
            "&state=" + Constants.STATE;

    final String createRepoURL = "https://api.github.com/user/repos";

    static String token = "";

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    com.ssd.ssdauthproject.service.UploadService uploadService;

    @Autowired
    com.ssd.ssdauthproject.service.AuthService authService;

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${google.secret.key.path}")
    private String pathDriveSecretKey;

    @Value("${google.credentials.folder.path}")
    private String pathCredentialFolder;

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    @PostConstruct
    public void init() throws Exception {

        GoogleClientSecrets secrets = GoogleClientSecrets.load(com.ssd.assignment.oauth.demo.util.Constants.JSON_FACTORY,
                new InputStreamReader(resourceLoader.getResource(pathDriveSecretKey).getInputStream()));
        googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(com.ssd.assignment.oauth.demo.util.Constants.HTTP_TRANSPORT, com.ssd.assignment.oauth.demo.util.Constants.JSON_FACTORY, secrets, com.ssd.assignment.oauth.demo.util.Constants.SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(resourceLoader.getResource(pathCredentialFolder).getFile())).build();
    }

    @GetMapping("/uploader")
    public String getHomePage() throws IOException {

        return isAuthenticated() ? "redirect:/uploader.html" : "redirect:/";
    }

    @GetMapping("/")
    public String showHomePage() throws Exception {
        return "redirect:/index.html";
    }

    @GetMapping("/oauth/callback")
    public String saveAuthorizationCode(HttpServletRequest request) throws Exception {

        String code = request.getParameter("code");
        logger.info("Code - {} ", code);
        if (code != null) {
            GoogleTokenResponse response = googleAuthorizationCodeFlow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
            googleAuthorizationCodeFlow.createAndStoreCredential(response, com.ssd.assignment.oauth.demo.util.Constants.USER_IDENTIFIER_KEY);
            return "redirect:/uploader";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout() throws Exception {
        Resource credentialsFolder = resourceLoader.getResource(pathCredentialFolder);
        FileUtils.cleanDirectory(credentialsFolder.getFile());
        logger.info("Logged Out");
        return "redirect:/";
    }

    @GetMapping("/signingoogle")
    public void doGoogleSignIn(HttpServletResponse response) throws Exception {
        GoogleAuthorizationCodeRequestUrl url = googleAuthorizationCodeFlow.newAuthorizationUrl();
        String redirectURL = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
        logger.info("Redirect URL {} ", redirectURL);
        response.sendRedirect(redirectURL);
    }

    @PostMapping("/upload")
    public String uploadFileToDrive(@RequestParam("file") MultipartFile multipartFile) throws Exception {

        com.google.api.client.auth.oauth2.Credential cred = googleAuthorizationCodeFlow.loadCredential(com.ssd.assignment.oauth.demo.util.Constants.USER_IDENTIFIER_KEY);
        String id = uploadService.uploadToDrive(cred, multipartFile);
        logger.info("Uploaded file id - {} ", id);
        return "redirect:/uploader";

    }

    private boolean isAuthenticated() throws IOException {

        com.google.api.client.auth.oauth2.Credential credential = googleAuthorizationCodeFlow.loadCredential(com.ssd.assignment.oauth.demo.util.Constants.USER_IDENTIFIER_KEY);
        return authService.authenticateCredentials(credential);

    }

    private String getTokenURL(String code){
        return "https://github.com/login/oauth/access_token?" +
                "client_id="+ Credential.clientId +
                "&client_secret=" + Credential.client_secret +
                "&code="+code +
                "&redirect_uri=" + Constants.REDIRECT_URL;
    }

    @RequestMapping("/github-login")
    public ModelAndView githubLogin() throws Exception {
        return new ModelAndView("redirect:" + githubLoginURL);
    }

    @RequestMapping(value = "/create-repo",method = RequestMethod.POST)
    public String createRepo(CreateRepo createRepo) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = new URI(createRepoURL);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","token "+token);

        HashMap map = new HashMap<>();
        map.put("name",createRepo.getReponame());
        map.put("private",createRepo.getStatus().equals("private"));
        map.put("description",createRepo.getDescription());

        HttpEntity httpHeader = new HttpEntity<>(map,headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, httpHeader, String.class);

        String response = result.toString().split(",")[0];
        String successStatus = response.split("<")[1];

        return successStatus.equals("201") ? "redirect:/success.html" : "redirect:/github.html";
    }

    @GetMapping("/github")
    public String loadGithubPage(HttpServletRequest request) throws Exception{
        RestTemplate restTemplate = new RestTemplate();

        final String baseUrl = getTokenURL(request.getParameter("code"));
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpHeader = new HttpEntity<>(headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, httpHeader, String.class);

        String response = result.toString().split(",")[1];
        String tokenString = response.split("=")[1].split("&")[0];

        token = tokenString;

        return token.equals("") ? "redirect:/" : "redirect:/github.html";
    }

    @GetMapping("/go-back")
    public String goBack() throws Exception{
        return "redirect:/github.html";
    }
}
