package com.example.authorization_service.configuration;

import com.example.authorization_service.configuration.securityFilters.JwtValidationFilter;
import com.example.authorization_service.data.repositories.SecretKeyRepository;
import com.example.authorization_service.services.AccessDeniedHandler;
import com.example.authorization_service.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder getBcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SecretKeyRepository secretKeyRepository, CustomUserDetailsService customUserDetailsService) throws Exception {
        return http
                .addFilterBefore(new JwtValidationFilter(secretKeyRepository, customUserDetailsService), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/authorization/login").permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        httpSecurityExceptionHandlingConfigurer ->
                                httpSecurityExceptionHandlingConfigurer.
                                        accessDeniedHandler(new AccessDeniedHandler())
                )
                .build();
    }

    @Bean
    public InMemoryUserDetailsManager get() {
        UserDetails admin = User
                .withUsername("admin")
                .password(getBcryptPasswordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
