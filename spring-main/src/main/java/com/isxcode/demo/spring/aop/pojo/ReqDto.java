package com.isxcode.demo.spring.aop.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ReqDto {

    private String dogId;

    private List<String> dogIds;

    private String username;
}

