package com.isxcode.demo.mysql.controller;

import com.isxcode.demo.mysql.pojo.ConnectInfo;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/mysql")
@RequiredArgsConstructor
public class MysqlController {

//    private final ConnectRepository connectRepository;

    public static final Map<ConnectInfo, JdbcTemplate> jdbcTemplateMap = new ConcurrentHashMap<>();

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/addDatasource")
    public Integer addDatasource(@RequestBody ConnectInfo connectInfo) {

        ConnectInfo defaultConnectInfo = new ConnectInfo();
        defaultConnectInfo.setName("default");
        jdbcTemplateMap.put(defaultConnectInfo, jdbcTemplate);

        JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(connectInfo);
        if (jdbcTemplate == null) {

            DataSourceProperties dataSourceProperties = new DataSourceProperties();
            dataSourceProperties.setUrl(connectInfo.getUrl());
            dataSourceProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSourceProperties.setUsername(connectInfo.getUsername());
            dataSourceProperties.setPassword(connectInfo.getPassword());

            HikariDataSource dataSource = DataSourceBuilder.create(dataSourceProperties.getClassLoader())
                    .driverClassName(dataSourceProperties.getDriverClassName())
                    .url(dataSourceProperties.getUrl())
                    .username(dataSourceProperties.getUsername())
                    .password(dataSourceProperties.getPassword())
                    .type(HikariDataSource.class)
                    .build();

            // 这样写才生效
            dataSource.setMaximumPoolSize(2);

            jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplateMap.put(connectInfo, jdbcTemplate);
        }

        return jdbcTemplateMap.size();

    }

    @GetMapping("/printDataPool")
    public String printDataPool() {

        jdbcTemplateMap.forEach((k, v) -> {
            try {
                System.out.println(((HikariDataSource) v.getDataSource()).getConnection().getMetaData().getURL());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return "";
    }

    @GetMapping("/executeSql")
    public void executeSql() {

        jdbcTemplateMap.forEach((k, v) -> {
            System.out.println(v.queryForList("show tables"));
        });
    }

    @GetMapping("/executeTemplate")
    public void executeTemplate() {

        System.out.println(jdbcTemplate.queryForList("show tables"));
    }

    @PostMapping("/addConnectToDao")
    public String addConnectToDao(@RequestBody ConnectInfo connectInfo) {

        Map<ConnectInfo, String> objectObjectHashMap = new HashMap<>();

        objectObjectHashMap.put(newConnect("1","1"), "1");
        objectObjectHashMap.put(newConnect("1", "2"), "2");

        return objectObjectHashMap.get(newConnect("1", "1"));
    }

    public ConnectInfo newConnect(String name, String url) {

        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setName(name);
        connectInfo.setUrl(url);
        return connectInfo;
    }

}
