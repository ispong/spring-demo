package com.isxcode.demo.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan({"com.isxcode.demo"})
public class DemoMainApplication {

    public static void main(String[] args) {

        SpringApplication.run(DemoMainApplication.class, args);
    }
}
