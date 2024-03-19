package com.example.authorization_service.services;

import com.example.authorization_service.data.repositories.SecretKeyRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Service
public class JwtService {

    private final SecretKeyRepository secretKeyRepository;

    public JwtService(SecretKeyRepository secretKeyRepository) {
        this.secretKeyRepository = secretKeyRepository;
    }

    public String generateJwt(HashMap<String, String> claims, UserDetails user, Date expiration) {
        if(secretKeyRepository.findById("SecretKey").isEmpty()) {
            throw new IllegalStateException("No secret key in redis found");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyRepository.findById("SecretKey").get().getKey());
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}
