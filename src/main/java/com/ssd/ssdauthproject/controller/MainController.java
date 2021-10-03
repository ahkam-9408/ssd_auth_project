package com.ssd.ssdauthproject.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.ssd.ssdauthproject.credentials.Credential;
import com.ssd.ssdauthproject.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.ArrayList;

@Controller
public class MainController {
    final String facebookLoginURL = "https://github.com/login/oauth/authorize?" +
//            "response_type=code" +
            "&client_id=" + Credential.clientId +
            "&redirect_uri=" + Credential.redirectURL +
            "&scope="+ Credential.scope +
            "&state=" + Credential.state;

    static String getToken = "";

    static String code = "";

    private RestTemplate restTemplate;
    private JSONPObject jsonpObject;
    private Object ArrayList;

//    @Autowired
//    private PersonService personService;
//
//    @Autowired
//    private PersonRepository personRepository;

    @GetMapping("/")
    public String loadIndexPage() throws Exception{
        return code.equals("") ? "redirect:/index.html" : "redirect:/github.html";
    }

    @RequestMapping("/facebook-login")
    public ModelAndView facebookLogin() throws Exception {
        return new ModelAndView("redirect:" + facebookLoginURL);
    }

    @GetMapping("/github")
    public void loadGithubPage(HttpServletRequest request) throws Exception{
        code = request.getParameter("code");
//        return new ModelAndView("redirect:" + "https%3A%2F%2Fgithub.com%2Flogin%2Foauth%2Faccess_token%3F" +
//                "client_id=5b8a23cfc2df318b5cd1&" +
//                "client_secret=bee326cdf84f8ee6a566f2f7339768a2b90990a0" +
//                "&code="+code +
//                "&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2Fgithub%2F");
//        getTokennn();
//        return code.equals("") ? "redirect:/" : "redirect:/github.html";

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "https://github.com/login/oauth/access_token?" +
                "client_id=5b8a23cfc2df318b5cd1&" +
                "client_secret=bee326cdf84f8ee6a566f2f7339768a2b90990a0" +
                "&code="+code +
                "&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2Fgithub%2F";;
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> valll = new HttpEntity<>(headers);

        ResponseEntity<String> result = restTemplate.postForEntity(uri, valll, String.class);

        System.out.println(result);
    }

//    @RequestMapping(value = "/persistPerson", method = RequestMethod.POST)
//    public ResponseEntity< String > persistPerson(@RequestBody PersonDTO person) {
//        if (personService.isValid(person)) {
//            personRepository.persist(person);
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        }
//        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
//    }

    @PostMapping("/github/get-token")
    public void getToken() throws Exception{
        String url = "https://github.com/login/oauth/access_token?" +
                "client_id=5b8a23cfc2df318b5cd1&" +
                "client_secret=bee326cdf84f8ee6a566f2f7339768a2b90990a0" +
                "&code="+code +
                "&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2Fgithub%2F";

        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

//        CurrentAccount account = client.postForObject(
//                "https://api.dropboxapi.com/2/users/get_current_account", entity, CurrentAccount.class);

        Token result = restTemplate.postForObject(url,entity, Token.class);
        System.out.println(result);
    }

    private static void getTokens()
    {
        final String uri = "https%3A%2F%2Fgithub.com%2Flogin%2Foauth%2Faccess_token%3F" +
                "client_id=5b8a23cfc2df318b5cd1&" +
                "client_secret=bee326cdf84f8ee6a566f2f7339768a2b90990a0" +
                "&code="+code +
                "&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2Fgithub%2F";

        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject(uri,entity, String.class);

        System.out.println(result);
    }

//    @RequestMapping("/get-token")
    public void getTokennn() throws Exception {

    }
}
