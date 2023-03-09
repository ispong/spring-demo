package com.isxcode.demo.oracle.controller;

import com.google.common.collect.Lists;
import com.isxcode.demo.oracle.pojo.ClickhouseReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/clickhouse")
public class ClickhouseController {

    @PostMapping("/testConnect")
    public String testConnect(@RequestBody ClickhouseReq clickhouseReq) throws ClassNotFoundException {

        List<String> result = new ArrayList<>();

        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
        try (Connection connection = DriverManager.getConnection(clickhouseReq.getJdbcUrl(), clickhouseReq.getUsername(), clickhouseReq.getPassword());) {
            if (Strings.isEmpty(clickhouseReq.getSql())) {
                return connection == null ? "connect为空，连接失败" : "连接成功";
            } else {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(clickhouseReq.getSql());
                while (resultSet.next()) {
                    result.add(String.valueOf(resultSet));
                }
                return String.valueOf(result);
            }
        } catch (Exception e) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(e.getMessage()).append("\n");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logBuilder.append(stackTraceElement.toString()).append("\n");
            }
            return logBuilder.toString();
        }

    }
}
