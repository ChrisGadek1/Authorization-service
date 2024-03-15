package com.example.authorization_service.controllers;

import com.example.authorization_service.data.repositories.UserRepository;
import com.example.authorization_service.domain.models.User;
import com.example.authorization_service.domain.requestBody.LoginForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginForm loginForm) {
        Optional<User> optionalUser = userRepository.findByUsername(loginForm.username());
        if(optionalUser.isPresent() && BCrypt.checkpw(loginForm.password(), optionalUser.get().getPasswordHashed())) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
