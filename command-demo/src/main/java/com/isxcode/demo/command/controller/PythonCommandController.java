package com.isxcode.demo.command.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/command")
@RequiredArgsConstructor
public class PythonCommandController {

    @GetMapping("/python")
    public String executePython(@RequestParam String processName) {

        String commands = "ps -e -o pid,command | grep " + processName;
        return executePythonCommand("192.168.16.79", "dehoop", commands);
    }

    public String executePythonCommand(String host, String user, String command) {

        List<String> sshCommand = new ArrayList<>();
        sshCommand.add("ssh");
        sshCommand.add(user + "@" + host);
        sshCommand.add("\"" + command + "\"");

        Process runProcess;
        try {
            runProcess = Runtime.getRuntime().exec(new String[]{"python3", "/data/dehoop/command.py", Strings.join(sshCommand, ' ')});
            BufferedReader in = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }
            in.close();
            runProcess.waitFor();
            log.debug("executePythonCommand:" + stringBuilder);
            return stringBuilder.toString();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}

