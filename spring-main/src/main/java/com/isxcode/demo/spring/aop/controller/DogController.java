package com.isxcode.demo.spring.aop.controller;

import com.isxcode.demo.spring.aop.pojo.ReqDto;
import com.isxcode.demo.spring.aop.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/spring/aop")
public class DogController {

    private final DogService dogService;

    @PostMapping("/test")
    public void testAop(@RequestBody ReqDto reqDto) {

        dogService.testWatchDog(reqDto);
    }
}
