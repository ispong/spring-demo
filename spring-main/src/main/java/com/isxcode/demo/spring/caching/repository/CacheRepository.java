package com.isxcode.demo.spring.caching.repository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"dog"})
public class CacheRepository {

    @CachePut(key = "#dogId")
    public String addDog(String dogId, String username) {

        return username;
    }

    @Cacheable(key = "#dogId", condition = "#dogId != null")
    public String getDog(String dogId) {

        return null;
    }

    @CachePut(key = "#dogId")
    public String updateDog(String dogId, String username) {

        return username;
    }

    @CacheEvict(key = "#dogId")
    public String delDog(String dogId) {

        return dogId;
    }
}
