package com.isxcode.demo.datasourcex.controller;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.source.FtpSourceDTO;
import com.dtstack.dtcenter.loader.dto.source.HdfsSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.isxcode.demo.datasourcex.pojo.DbxReq;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dbx")
public class TestFtpController {

    public void test(HdfsSourceDTO source) {

        StopWatch stopWatch = new StopWatch("测试hdfs数据库");

        stopWatch.start("测试链接");
        IClient hdfsClient = ClientCache.getClient(DataSourceType.HDFS.getVal());
        Boolean conn = hdfsClient.testCon(source);
        stopWatch.stop();

        stopWatch.start("创建路径");
        IHdfsFile client = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        Boolean hasCreated = client.createDir(source, "/ispong", null);
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }

    /*
     * ftp   --> 21
     * sftp  --> 22
     * @ispong
     */
    @PostMapping("/open/ftp")
    public void testFtp(@RequestBody DbxReq dbxReq) {

        FtpSourceDTO source =
                FtpSourceDTO.builder()
                        .url(dbxReq.getHost())
                        .hostPort("22")
                        .username(dbxReq.getUsername())
                        .password(dbxReq.getPassword())
                        .protocol("ftp")
                        .build();

        IClient client = ClientCache.getClient(DataSourceType.FTP.getVal());
        Boolean isConnected = client.testCon(source);
        System.out.println("==>" + isConnected);
    }
}
