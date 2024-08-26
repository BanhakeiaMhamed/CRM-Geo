package com.backend.backend.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("public") // Base URL for all API endpoints
public class WelcomeController {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        // Cette méthode peut retourner une chaîne de caractères pour indiquer que le service fonctionne
        return ResponseEntity.ok("Welcome to the API! This is the home endpoint.");
    }

    @GetMapping("/register")
    public ResponseEntity<String> redirectToCognitoRegister() {
        // URL de la page d'inscription Cognito
        //String registrationUrl = "https://crm-geo.auth.us-east-1.amazoncognito.com/signup?client_id=7hgos2skpub85bgo8p6n7spf7v&response_type=code&scope=openid&redirect_uri=http://localhost:8081/login/oauth2/code/cognito";
        String registrationUrl = "http://localhost:4200";
        System.out.println("register");
        // Redirection vers l'URL d'inscription Cognito
        return ResponseEntity.status(302).header("Location", registrationUrl).build();
    }

    @GetMapping("/login")
    public ResponseEntity<String> redirectToLogin() {
        // URL de redirection pour la page de login Cognito
        String loginUrl = "https://crm-geo.auth.us-east-1.amazoncognito.com/login?client_id=7hgos2skpub85bgo8p6n7spf7v&response_type=code&scope=openid&redirect_uri=http://localhost:8081/login/oauth2/code/cognito";
        System.out.println("/login");
        // Redirection vers l'IHM de login Cognito
        return ResponseEntity.status(302).header("Location", loginUrl).build();
    }
}
