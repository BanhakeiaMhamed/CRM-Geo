package com.backend.backend.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


    @Controller
    public class WelcomeController {

        @GetMapping("/")
        public String home(Model model) {
            //model.addAttribute("message", "Welcome To The Index Page!");
            System.out.println("hello");
            return "hello";
        }

        @GetMapping("admin/greetMe")
        public String adminGreet(Model model) {
            String response = "Welcome admin ! You developed an amazing website! :)";

          //  model.addAttribute("response", response);
            System.out.println("admin");
            return "admin";
        }

        @GetMapping("user/greetMe")
        public String userGreet(Model model) {

            String response = "Welcome user ! God bless you with amazing future ahead! :)";
            System.out.println("user");
            //model.addAttribute("response", response);

            return "user";
        }
        @GetMapping("/register")
        public String redirectToCognitoRegister() {
            // URL de la page d'inscription Cognito
            String registrationUrl = "https://crm-geo.auth.us-east-1.amazoncognito.com/signup?client_id=7hgos2skpub85bgo8p6n7spf7v&response_type=code&scope=openid&redirect_uri=http://localhost:8081/login/oauth2/code/cognito";
            return "redirect:" + registrationUrl;
        }
    }

