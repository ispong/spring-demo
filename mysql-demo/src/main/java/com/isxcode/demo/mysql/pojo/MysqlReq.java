package com.isxcode.demo.mysql.pojo;

import lombok.Data;

@Data
public class MysqlReq {

    private String jdbcUrl;

    private String username;

    private String password;
}
