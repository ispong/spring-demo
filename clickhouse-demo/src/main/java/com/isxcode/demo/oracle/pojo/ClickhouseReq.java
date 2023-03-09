package com.isxcode.demo.oracle.pojo;

import lombok.Data;

@Data
public class ClickhouseReq {

    private String jdbcUrl;

    private String username;

    private String password;
}
