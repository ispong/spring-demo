package com.isxcode.demo.mysql.pojo;

import lombok.Data;

import java.util.Objects;

@Data
public class ConnectInfo {

    private String url;

    private String username;

    private String password;

    private String driver;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectInfo that = (ConnectInfo) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
