package com.example.authorization_service.controllers;

import com.example.authorization_service.data.repositories.UserRepository;
import com.example.authorization_service.domain.models.User;
import com.example.authorization_service.domain.requestBody.LoginForm;
import com.example.authorization_service.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class LoginController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping(path ="/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginForm loginForm) {
        Optional<User> optionalUser = userRepository.findByUsername(loginForm.username());
        if(optionalUser.isPresent() && optionalUser.get().isEnabled() && BCrypt.checkpw(loginForm.password(), optionalUser.get().getPassword())) {
            return new ResponseEntity<>(Map.of("token", jwtService.generateJwt(new HashMap<>(), optionalUser.get())), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
