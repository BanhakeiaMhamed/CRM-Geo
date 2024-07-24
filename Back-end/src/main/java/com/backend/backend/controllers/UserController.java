package com.backend.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/geo")
public class UserController {

    @GetMapping("/test")
    public void test(){
        System.out.println(" Your project has been executed successfully");
    }
}
