package com.integration.zoy.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home redirection to swagger api documentation 
 */
@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "redirect:/swagger-ui/";
    }
}

