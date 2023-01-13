package com.isxcode.demo.command.service;


import org.springframework.stereotype.Service;

@Service
public interface CommandService {

    /*
     * 执行本地命令
     *
     * @ispong
     */
    int executeLocalCommand(String command, long waitingTime);

    /*
     * 直接执行远程命令
     *
     * @ispong
     */
    int executeRemoteCommand(String host, String user, String command, long waitingTime);

    /*
     * 将文本内容写到临时文件中
     *
     * @ispong
     */
    void generateTmpFile(String content, String tmpDir, String fileSuffix, String executeId);

    /*
     * 执行远程命令并存储日志文件
     *
     * @ispong
     */
    int executeRemoteCommandStoreLog(
            String host, String user, String logDir, String commandDir, String executeId);

    /*
     * 执行远程命令并返回日志
     *
     * @ispong
     */
    String executeRemoteCommandReturnLog(String host, String user, String command, long waitingTime);

    /*
     * 将本地文本推送到远程服务器
     *
     * @ispong
     */
    int executeScpCommand(String host, String user, String sourceFilePath, String targetDir);

    /*
     * 获取远程服务器文件内容
     *
     * @ispong
     */
    String executeCatCommand(String host, String user, String filePath);
}
