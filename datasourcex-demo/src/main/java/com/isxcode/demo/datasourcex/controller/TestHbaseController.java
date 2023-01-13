package com.isxcode.demo.datasourcex.controller;

import com.dtstack.dtcenter.loader.cache.pool.config.PoolConfig;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IHbase;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.HbaseSourceDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.isxcode.demo.datasourcex.pojo.DbxReq;
import java.util.List;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dbx")
public class TestHbaseController {

    public void test(ISourceDTO source, SqlQueryDTO queryDTO, IClient client) {

        StopWatch stopWatch = new StopWatch("测试hbase数据库");

        stopWatch.start("测试联通性");
        Boolean isConnected = client.testCon(source);
        stopWatch.stop();

        stopWatch.start("测试联通性");
        List tableList = client.getTableList(source, null);
        stopWatch.stop();

        stopWatch.start("获取字段信息");
        List metaData = client.getColumnMetaData(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("数据预览");
        List result = client.getPreview(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("创建表");
        IHbase hbaseClient = ClientCache.getHbase(DataSourceType.HBASE.getVal());
        Boolean check =
                hbaseClient.createHbaseTable(source, "loader_test_2", new String[] {"info1", "info2"});
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }

    /*
     *  hbase中的 hbase.zookeeper.quorum 使用ip
     *
     * @ispong
     */
    @PostMapping("/open/hbase")
    public void testHbase(@RequestBody DbxReq dbxReq) {

        IClient client = ClientCache.getClient(DataSourceType.HBASE.getVal());

        HbaseSourceDTO source =
                HbaseSourceDTO.builder()
                        .url(dbxReq.getUrl())
                        .path(dbxReq.getPath())
                        .poolConfig(PoolConfig.builder().build())
                        .build();

        SqlQueryDTO queryDTO =
                SqlQueryDTO.builder()
                        .tableName(dbxReq.getTableName())
                        .sql(dbxReq.getSql())
                        .previewNum(10)
                        .limit(10)
                        .build();

        test(source, queryDTO, client);
    }
}
