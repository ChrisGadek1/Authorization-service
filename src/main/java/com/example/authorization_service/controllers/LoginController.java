package com.example.authorization_service.controllers;

import com.example.authorization_service.data.repositories.UserRepository;
import com.example.authorization_service.domain.models.User;
import com.example.authorization_service.domain.requestBody.LoginForm;
import com.example.authorization_service.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/authorization")
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    private final JwtService jwtService;


    @PostMapping(path ="/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginForm loginForm) {
        Optional<User> optionalUser = userRepository.findByUsername(loginForm.username());
        if(optionalUser.isPresent() && optionalUser.get().isEnabled() && BCrypt.checkpw(loginForm.password(), optionalUser.get().getPassword())) {
            return new ResponseEntity<>(Map.of(
                    "token", jwtService.generateJwt(new HashMap<>(), optionalUser.get(), new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))),
                    "refresh_token", jwtService.generateJwt(new HashMap<>(), optionalUser.get(), new Date(System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000)))
            ), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> refresh(@AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(Map.of(
                "token", jwtService.generateJwt(new HashMap<>(), userDetails, new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))),
                "refresh_token", jwtService.generateJwt(new HashMap<>(), userDetails, new Date(System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000)))
        ), HttpStatus.OK);
    }
}
