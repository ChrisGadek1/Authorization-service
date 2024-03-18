package com.example.authorization_service.domain.models;

import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("SecretKey")
public class SecretKey implements Serializable {

    public SecretKey(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    @Id
    private final String id;
    private final String key;
}
