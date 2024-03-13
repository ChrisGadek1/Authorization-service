package com.example.authorization_service.data.repositories;

import com.example.authorization_service.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    Optional<User> findById(Long id);
}
