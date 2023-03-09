package com.isxcode.demo.oracle.controller;

import com.isxcode.demo.oracle.pojo.ClickhouseReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping("/clickhouse")
public class ClickhouseController {

    @PostMapping("/testConnect")
    public String testConnect(@RequestBody ClickhouseReq clickhouseReq) throws ClassNotFoundException {

        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
        try (Connection connection = DriverManager.getConnection(clickhouseReq.getJdbcUrl(), clickhouseReq.getUsername(), clickhouseReq.getPassword());) {
            return connection == null ? "connect为空，连接失败" : "连接成功";
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
