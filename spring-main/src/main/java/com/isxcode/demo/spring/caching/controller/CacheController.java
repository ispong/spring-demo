package com.isxcode.demo.spring.caching.controller;

import com.isxcode.demo.spring.caching.repository.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/spring/caching")
@RestController
public class CacheController {

    private final CacheRepository cacheRepository;

    @GetMapping("/addDog")
    public String addDog() {

        return cacheRepository.addDog("1", "hangman");
    }

    @GetMapping("/updateDog")
    public String updateDog() {

        return cacheRepository.updateDog("1", "wuhan");
    }

    @GetMapping("/deleteDog")
    public String deleteDog() {

        return cacheRepository.delDog("1");
    }

    @GetMapping("/getDog")
    public String getDog() {

        return cacheRepository.getDog("1");
    }

}
