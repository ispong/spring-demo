package com.isxcode.demo.datasourcex.controller;

import com.dtstack.dtcenter.loader.cache.pool.config.PoolConfig;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.Table;
import com.dtstack.dtcenter.loader.dto.TableInfo;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.Mysql5SourceDTO;
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
public class TestMysqlController {

    public void test(ISourceDTO source, SqlQueryDTO queryDTO, IClient client) {

        StopWatch stopWatch = new StopWatch("测试mysql数据库");

        stopWatch.start("获取字段名称列表");
        List columnClassInfo = client.getColumnClassInfo(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("获取字段详情列表");
        List columnMetaData = client.getColumnMetaData(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("获取表名列表");
        List tableList = client.getTableList(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("获取数据列表");
        List list = client.getPreview(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("获取表详细信息");
        Table table = client.getTable(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("获取建表语句");
        //        String createTableSql = client.getCreateTableSql(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("获取当前数据源");
        String currentDatabase = client.getCurrentDatabase(source);
        stopWatch.stop();

        stopWatch.start("判断表是否存在");
        //        Boolean tableExistsInDatabase = client.isTableExistsInDatabase(source,
        // queryDTO.getTableName(), currentDatabase);
        stopWatch.stop();

        stopWatch.start("执行sql");
        client.executeSqlWithoutResultSet(source, queryDTO);
        stopWatch.stop();

        stopWatch.start("测试链接");
        Boolean aBoolean = client.testCon(source);
        stopWatch.stop();

        stopWatch.start("获取表简单信息");
        TableInfo tableInfo = client.getTableInfo(source, queryDTO.getTableName());
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }

    @PostMapping("/open/mysql")
    public void testMysql(@RequestBody DbxReq dbxReq) {

        IClient client = ClientCache.getClient(DataSourceType.MySQL.getVal());

        Mysql5SourceDTO source =
                Mysql5SourceDTO.builder()
                        .url(dbxReq.getUrl())
                        .username(dbxReq.getUsername())
                        .password(dbxReq.getPassword())
                        .poolConfig(PoolConfig.builder().build())
                        .build();

        SqlQueryDTO queryDTO =
                SqlQueryDTO.builder()
                        .tableName(dbxReq.getTableName())
                        .sql(dbxReq.getSql())
                        .limit(3)
                        .build();

        test(source, queryDTO, client);
    }
}
