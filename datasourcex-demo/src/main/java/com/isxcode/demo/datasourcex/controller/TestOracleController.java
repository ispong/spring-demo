package com.isxcode.demo.datasourcex.controller;// package com.isxcode.leo.demo.datasourcex.controller;
//
// import com.dtstack.dtcenter.loader.cache.pool.config.PoolConfig;
// import com.dtstack.dtcenter.loader.client.ClientCache;
// import com.dtstack.dtcenter.loader.client.IClient;
// import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
// import com.dtstack.dtcenter.loader.dto.Table;
// import com.dtstack.dtcenter.loader.dto.TableInfo;
// import com.dtstack.dtcenter.loader.dto.source.HiveSourceDTO;
// import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
// import com.dtstack.dtcenter.loader.dto.source.Mysql5SourceDTO;
// import com.dtstack.dtcenter.loader.dto.source.SqlserverSourceDTO;
// import com.dtstack.dtcenter.loader.source.DataSourceType;
// import com.isxcode.demo.datasourcex.pojo.DbxReq;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import java.util.List;
//
// @RestController
// @RequestMapping("/dbx")
// public class TestOracleController {
//
//    public void test(ISourceDTO source, SqlQueryDTO queryDTO, IClient client) {
//
//        List columnClassInfo = client.getColumnClassInfo(source, queryDTO);
//        List columnMetaData = client.getColumnMetaData(source, queryDTO);
//        List tableList = client.getTableList(source, queryDTO);
//        List list = client.getPreview(source, queryDTO);
//        Table table = client.getTable(source, queryDTO);
//        String createTableSql = client.getCreateTableSql(source, queryDTO);
//        String currentDatabase = client.getCurrentDatabase(source);
//        Boolean tableExistsInDatabase = client.isTableExistsInDatabase(source,
// queryDTO.getTableName(), currentDatabase);
//        client.executeSqlWithoutResultSet(source, queryDTO);
//        Boolean aBoolean = client.testCon(source);
//        TableInfo tableInfo = client.getTableInfo(source, queryDTO.getTableName());
//        Boolean aBoolean1 = client.executeSqlWithoutResultSet(source, queryDTO);
//    }
//
//    @PostMapping("/open/mysql")
//    public void testMysql(@RequestBody DbxReq dbxReq) {
//
//        IClient client = ClientCache.getClient(DataSourceType.MySQL.getVal());
//
//        Mysql5SourceDTO source = Mysql5SourceDTO.builder()
//            .url(dbxReq.getUrl())
//            .username(dbxReq.getUsername())
//            .password(dbxReq.getPassword())
//            .poolConfig(PoolConfig.builder().build())
//            .build();
//
//        SqlQueryDTO queryDTO = SqlQueryDTO.builder()
//            .tableName(dbxReq.getTableName())
//            .sql(dbxReq.getSql())
//            .limit(3)
//            .build();
//
//        test(source, queryDTO, client);
//    }
//
//    @PostMapping("/open/sqlserver")
//    public void testSqlserver(@RequestBody DbxReq dbxReq) {
//
//        IClient client = ClientCache.getClient(DataSourceType.SQLServer.getVal());
//        SqlserverSourceDTO source = SqlserverSourceDTO.builder()
//            .url(dbxReq.getUrl())
//            .username(dbxReq.getUsername())
//            .password(dbxReq.getPassword())
//            .poolConfig(PoolConfig.builder().build())
//            .build();
//
//        SqlQueryDTO queryDTO = SqlQueryDTO.builder()
//            .tableName(dbxReq.getTableName())
//            .sql(dbxReq.getSql())
//            .limit(3)
//            .build();
//
//        test(source, queryDTO, client);
//    }
//
//    @PostMapping("/open/hive")
//    public void testHive(@RequestBody DbxReq dbxReq) {
//
//        IClient client = ClientCache.getClient(DataSourceType.HIVE.getVal());
//        HiveSourceDTO source = HiveSourceDTO.builder()
//            .url(dbxReq.getUrl())
//            .username(dbxReq.getUsername())
//            .password(dbxReq.getPassword())
//            .poolConfig(PoolConfig.builder().build())
//            .build();
//
//        SqlQueryDTO queryDTO = SqlQueryDTO.builder()
//            .tableName(dbxReq.getTableName())
//            .sql(dbxReq.getSql())
//            .limit(3)
//            .build();
//
//        test(source, queryDTO, client);
//    }
// }
