package com.backend.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class TestController {
    @GetMapping("/ad")
    public ResponseEntity<String> adminGreet() {
        String response = "Welcome admin! You developed an amazing website! :)";
        return ResponseEntity.ok(response);
    }

    @GetMapping("/us")
    public ResponseEntity<String> userGreet() {
        String response = "Welcome user! God bless you with an amazing future ahead! :)";
        return ResponseEntity.ok(response);
    }
}
