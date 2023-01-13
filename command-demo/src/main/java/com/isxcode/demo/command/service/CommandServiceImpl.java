package com.isxcode.demo.command.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@Service
public class CommandServiceImpl implements CommandService {

    private static CommandLine generateCommandLine(String command) {

        CommandLine cmdLine;
        String[] cmd;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            cmd = new String[] {"/c", command};
            cmdLine = CommandLine.parse("C:\\Windows\\System32\\cmd.exe");
        } else {
            cmd = new String[] {"-c", command};
            cmdLine = CommandLine.parse("/bin/sh");
        }
        cmdLine.addArguments(cmd, false);
        return cmdLine;
    }

    @Override
    public int executeLocalCommand(String command, long waitingTime) {

        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = generateCommandLine(command);

        try {
            // set watchdog for waiting
            ExecuteWatchdog watchdog = new ExecuteWatchdog(waitingTime);
            executor.setWatchdog(watchdog);
            return executor.execute(cmdLine);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("执行命令异常:" + command);
        }
    }

    @Override
    public int executeRemoteCommand(String host, String user, String command, long waitingTime) {

        String remoteCommand = "ssh " + user + "@" + host + " \" " + command + " \" ";

        return executeLocalCommand(remoteCommand, waitingTime);
    }

    @Override
    public void generateTmpFile(String content, String tmpDir, String fileSuffix, String executeId) {

        // 检查并创建临时文件夹
        Path dir = Paths.get(tmpDir);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException("创建临时文件夹异常");
            }
        }

        // 检查并创建临时文件
        Path file = Paths.get(tmpDir + File.separator + executeId + "." + fileSuffix);
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException("创建临时文件异常");
            }
        }

        // 将内容写入临时文件
        try {
            Files.write(file, content.getBytes(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("写入临时文件异常");
        }
    }

    @Override
    public int executeRemoteCommandStoreLog(
            String host, String user, String logDir, String commandDir, String executeId) {

        // 拼接执行命令
//        String nohupCommand = "nohup " + "ssh " + user + "@" + host + " \" bash " + commandDir + executeId + ".sh" + "\"" + " >> " + logDir + executeId + ".log" + " 2>&1 &";
//        return executeLocalCommand(nohupCommand, 3000);

        String nohupCommand = "nohup bash " + commandDir + executeId + ".sh" + " >> " + logDir + executeId + ".log" + " 2>&1 &";
        return executeRemoteCommand(host, user, nohupCommand, 5000);

    }

    @Override
    public String executeRemoteCommandReturnLog(
            String host, String user, String command, long waitingTime) {

        DefaultExecutor executor = new DefaultExecutor();

        String genRemoteLogCommand = "ssh " + user + "@" + host + " \" " + command + " \" ";

        CommandLine cmdLine = generateCommandLine(genRemoteLogCommand);

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            PumpStreamHandler streamHandler =
                    new PumpStreamHandler(byteArrayOutputStream, byteArrayOutputStream, null);
            executor.setStreamHandler(streamHandler);

            // set watchdog for waiting
            ExecuteWatchdog watchdog = new ExecuteWatchdog(waitingTime);
            executor.setWatchdog(watchdog);
            executor.execute(cmdLine);

            byte[] bytes = byteArrayOutputStream.toByteArray();
            return new String(bytes, 0, bytes.length);

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("执行命令异常" + genRemoteLogCommand);
        }
    }

    @Override
    public int executeScpCommand(String host, String user, String sourceFilePath, String targetDir) {

        String scpCommand = "scp " + sourceFilePath + " " + user + "@" + host + ":" + targetDir;

        return executeLocalCommand(scpCommand, 3000);
    }

    @Override
    public String executeCatCommand(String host, String user, String filePath) {

        DefaultExecutor executor = new DefaultExecutor();

        String remoteCatCommand = "ssh " + user + "@" + host + " \" cat " + filePath + " \" ";

        CommandLine cmdLine = generateCommandLine(remoteCatCommand);

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            PumpStreamHandler streamHandler =
                    new PumpStreamHandler(byteArrayOutputStream, byteArrayOutputStream, null);
            executor.setStreamHandler(streamHandler);

            // set watchdog for waiting
            ExecuteWatchdog watchdog = new ExecuteWatchdog(3000);
            executor.setWatchdog(watchdog);
            executor.execute(cmdLine);

            byte[] bytes = byteArrayOutputStream.toByteArray();
            return new String(bytes, 0, bytes.length);

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("执行命令异常:" + remoteCatCommand);
        }
    }
}
