package com.ssd.ssdauthproject.controller;

import com.ssd.ssdauthproject.credentials.Credential;
import com.ssd.ssdauthproject.model.CreateRepo;
import com.ssd.ssdauthproject.utils.Constants;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

    private String getTokenURL(String code){
        return "https://github.com/login/oauth/access_token?" +
                "client_id="+ Credential.clientId +
                "&client_secret=" + Credential.client_secret +
                "&code="+code +
                "&redirect_uri=" + Constants.REDIRECT_URL;
    }

    @GetMapping("/")
    public String loadIndexPage() throws Exception{
        return token.equals("") ? "redirect:/index.html" : "redirect:/github.html";
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
        map.put("private",createRepo.getStatus() == "private" ? true : false);
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
