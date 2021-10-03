package com.ssd.ssdauthproject.controller;

import com.ssd.ssdauthproject.credentials.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public ModelAndView facebookLogin() throws Exception{
        return new ModelAndView("redirect:" + facebookLoginURL);
    }

    @GetMapping("/github")
    public String loadGithubPage(HttpServletRequest request) throws Exception{
        code = request.getParameter("code");
        return code.equals("") ? "redirect:/" : "redirect:/github.html" ;
    }

//    @RequestMapping(value = "/persistPerson", method = RequestMethod.POST)
//    public ResponseEntity< String > persistPerson(@RequestBody PersonDTO person) {
//        if (personService.isValid(person)) {
//            personRepository.persist(person);
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        }
//        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
//    }
}
