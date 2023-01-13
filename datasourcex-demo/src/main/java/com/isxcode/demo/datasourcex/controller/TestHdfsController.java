package com.isxcode.demo.datasourcex.controller;

import com.alibaba.fastjson.JSON;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.source.HdfsSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.isxcode.demo.datasourcex.pojo.DbxReq;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dbx")
public class TestHdfsController {

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

    @PostMapping("/open/hdfs")
    public void testDoris(@RequestBody DbxReq dbxReq) {

        Map<String, String> hdfsConfig = new HashMap<>();
        //        非集群模式
        hdfsConfig.put("dfs.namenode.rpc-address", "isxcode:30148");
        //        下面是ha模式
        //        hdfsConfig.put("dfs.nameservices", "ns1");
        //        hdfsConfig.put("dfs.ha.namenodes.ns1", "nn1,nn2");
        //        hdfsConfig.put("dfs.namenode.rpc-address.ns1.nn2", "kudu2:9000");
        //        hdfsConfig.put("dfs.namenode.rpc-address.ns1.nn1", "kudu1:9000");
        //        hdfsConfig.put("dfs.client.failover.proxy.provider.ns1",
        // "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

        //        Map<String, Object> kerberosConfig = new HashMap<>();
        //        kerberosConfig.put("principal", "hdfs");
        //        kerberosConfig.put("principalFile", "xxx");
        //        kerberosConfig.put("java.security.krb5.conf", "xxx");

        HdfsSourceDTO source =
                HdfsSourceDTO.builder()
                        .defaultFS("hdfs://isxcode:30116")
                        .config(JSON.toJSONString(hdfsConfig))
                        .user("ispong")
                        //            .kerberosConfig(kerberosConfig)
                        .build();

        test(source);
    }
}
