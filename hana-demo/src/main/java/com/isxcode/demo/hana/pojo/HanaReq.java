package com.isxcode.demo.hana.pojo;

import lombok.Data;

@Data
public class HanaReq {

    private String jdbcUrl;

    private String username;

    private String password;
}
