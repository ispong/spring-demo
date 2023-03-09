package com.isxcode.demo.hana.controller;

import com.isxcode.demo.hana.pojo.HanaReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/hana")
public class HanaController {

    @PostMapping("/testConnect")
    public String testConnect(@RequestBody HanaReq oracleReq) {

        Connection connection = null;
        try {
            Class.forName("com.sap.db.jdbc.Driver");
            connection = DriverManager.getConnection(oracleReq.getJdbcUrl(), oracleReq.getUsername(), oracleReq.getPassword());
            return connection == null ? "connect为空，连接失败" : "连接成功";
        } catch (ClassNotFoundException | SQLException e) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(e.getMessage()).append("\n");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logBuilder.append(stackTraceElement.toString()).append("\n");
            }
            return logBuilder.toString();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

}
