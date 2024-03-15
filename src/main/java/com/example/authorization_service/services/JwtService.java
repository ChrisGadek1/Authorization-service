package com.example.authorization_service.services;

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
    public String generateJwt(HashMap<String, String> claims, UserDetails user) {
        byte[] keyBytes = Decoders.BASE64.decode("mysecretmysecretmysecretmysecretmysecretmysecretmysecretmysecretmysecretmysecret");
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)))
                .signWith(key)
                .compact();
    }
}
