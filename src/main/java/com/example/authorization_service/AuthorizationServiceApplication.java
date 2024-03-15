package com.example.authorization_service;

import com.example.authorization_service.data.repositories.UserRepository;
import com.example.authorization_service.domain.models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.NoSuchElementException;

@SpringBootApplication
//@EnableJpaRepositories("com.example.authorization_service.data.repositories")
public class AuthorizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserRepository repository) {
        return args -> {
            User user = new User();
            user.setName("John");
            user.setPassword(BCrypt.hashpw("qwerty123", BCrypt.gensalt()));
            user.setUsername("johny123");

            repository.save(user);
            User saved = repository.findById(user.getId()).orElseThrow(NoSuchElementException::new);
            System.out.println(saved.getName());
        };
    }

}
