package com.example.authorization_service.data.repositories;

import com.example.authorization_service.domain.models.SecretKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretKeyRepository extends CrudRepository<SecretKey, String> { }
