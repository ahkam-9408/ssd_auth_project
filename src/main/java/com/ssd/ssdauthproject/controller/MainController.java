package com.ssd.ssdauthproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String loadIndexPage() throws Exception{
        return "redirect:/index.html";
    }
}
