package com.isxcode.demo.atlas.pojo;

import java.util.List;
import lombok.Data;

@Data
public class AtlasReqDto {

    private String dbName;

    private String dbGuid;

    private String ddlSql;

    private String dmlSql;

    private String jobName;

    private String userName;

    private String queryText;

    private String queryPlan;

    private TableMapping tableMapping;

    private List<ColMapping> colMapping;

    private String tableName;

    private Integer page;

    private Integer pageSize;

    private String labelName;

    private String colName;

    private String typeName;

    private String apiName;

    private List<String> tableGuids;

    private String searchWord;
}
