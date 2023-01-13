package com.isxcode.demo.command.controller;

import com.isxcode.demo.command.service.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;

    @GetMapping("/exec")
    public void executeChunjun() {

        // 脚本
        String chunjunJson = "{}";

        // 将脚本写到本地临时文件中
        commandService.generateTmpFile(chunjunJson, "D://tmp//", "json", "123456789");

        // 服务器上需要提前创建/home/ispong/jsons/文件目录
        // 将临时文件推送到远程服务器
        commandService.executeScpCommand(
                "isxcode", "ispong", "D://tmp//123456789.json", "/home/ispong/jsons/");

        // 拼接执行命令
        String command =
                "bash /opt/chunjun/bin/chunjun-yarn-perjob.sh -job /home/ispong/jsons/123456789.json";

        // 将脚本写到本地临时文件中
        commandService.generateTmpFile(command, "D://tmp//", "sh", "123456789");

        // 将临时文件推送到远程服务器
        commandService.executeScpCommand(
                "isxcode", "ispong", "D://tmp//123456789.sh", "/home/ispong/commands/");

        // 执行远程命令并保存日志
        commandService.executeRemoteCommandStoreLog(
                "isxcode", "ispong", "/home/ispong/logs/", "/home/ispong/commands/", "123456789");
    }

    @GetMapping("/open/chunjun/log")
    public String getChunjunLog() {

        return commandService.executeCatCommand("isxcode", "ispong", "/home/ispong/logs/123456789.log");
    }


    @GetMapping("/open/chunjun/status")
    public String getChunjunStatus() {

        String getYarnStatus = "yarn application -status " + " application_1667184312043_0479";

        return commandService.executeRemoteCommandReturnLog("isxcode", "ispong", getYarnStatus, 5000);
    }

    @GetMapping("/open/chunjun/yarn")
    public String getChunjunYarn() {

        String getYarnStatus = "yarn logs -applicationId " + " application_1667184312043_0479";

        return commandService.executeRemoteCommandReturnLog("isxcode", "ispong", getYarnStatus, 10000);
    }
}

