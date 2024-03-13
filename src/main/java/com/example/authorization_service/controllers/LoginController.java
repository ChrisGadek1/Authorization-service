package com.example.authorization_service.controllers;

import com.example.authorization_service.domain.requestBody.LoginForm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestBody LoginForm loginForm) {
        return "success!";
    }
}
