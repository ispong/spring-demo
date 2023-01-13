package com.isxcode.demo.datasourcex.pojo;

import lombok.Data;

@Data
public class DbxReq {

    private String url;

    private String username;

    private String password;

    private String tableName;

    private String sql;

    private String schema;

    private String path;

    private String host;
}
