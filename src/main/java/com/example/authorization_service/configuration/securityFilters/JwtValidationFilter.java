package com.example.authorization_service.configuration.securityFilters;

import com.example.authorization_service.data.repositories.SecretKeyRepository;
import com.example.authorization_service.services.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;

@Order(1)
public class JwtValidationFilter implements Filter {

    private final SecretKeyRepository secretKeyRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtValidationFilter(SecretKeyRepository secretKeyRepository, CustomUserDetailsService customUserDetailsService) {
        this.secretKeyRepository = secretKeyRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    private void setUnauthorizedUser() {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(null, null);
        usernamePasswordAuthenticationToken.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    private void setAuthorizedUser(SecretKey key, String tokenFromResponse) {
        Claims parsedClaims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(tokenFromResponse)
                .getPayload();
        String type = parsedClaims.get("type", String.class);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(parsedClaims.getSubject());
        ArrayList<SimpleGrantedAuthority> authorities = (ArrayList<SimpleGrantedAuthority>) new ArrayList<>(userDetails.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + type));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse responseHttpServlet = (HttpServletResponse) response;
        HttpServletRequest requestHttpServlet = (HttpServletRequest) request;
        if (requestHttpServlet.getHeader("Authorization") == null) {
            setUnauthorizedUser();
        } else {
            String tokenFromResponse = requestHttpServlet.getHeader("Authorization").split(" ")[1];
            String secretKey = secretKeyRepository.findById("SecretKey").get().getKey();
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            try {
                setAuthorizedUser(key, tokenFromResponse);
            } catch (JwtException | UsernameNotFoundException e) {
                setUnauthorizedUser();
            }
        }
        chain.doFilter(requestHttpServlet, responseHttpServlet);
    }
}
