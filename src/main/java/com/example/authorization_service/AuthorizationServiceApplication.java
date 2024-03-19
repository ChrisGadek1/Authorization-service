package com.example.authorization_service;

import com.example.authorization_service.data.repositories.SecretKeyRepository;
import com.example.authorization_service.data.repositories.UserRepository;
import com.example.authorization_service.domain.models.SecretKey;
import com.example.authorization_service.domain.models.User;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;

@SpringBootApplication
public class AuthorizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserRepository repository, SecretKeyRepository secretKeyRepository) {
        return args -> {
            if(secretKeyRepository.findById("SecretKey").isEmpty()) {
                javax.crypto.SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
                String secretString =  Encoders.BASE64.encode(key.getEncoded());
                secretKeyRepository.save(new SecretKey("SecretKey", secretString));
            }
            User user = new User();
            user.setName("John");
            user.setPassword(BCrypt.hashpw("qwerty123", BCrypt.gensalt()));
            user.setUsername("johny123");

            repository.save(user);
        };
    }

}
