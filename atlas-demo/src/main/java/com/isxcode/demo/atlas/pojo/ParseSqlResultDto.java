package com.isxcode.demo.atlas.pojo;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.hadoop.hive.ql.lib.NodeProcessorCtx;

@Data
public class ParseSqlResultDto implements NodeProcessorCtx {

    /*
     * ALTER_TABLE
     * CREATE_TABLE
     *
     * @ispong
     */
    private String executeType;

    private String name;

    private String newName;

    private String qualifiedName;

    private String description;

    private String dbGuid;

    private String dbName;

    private String tableGuid;

    private String owner;

    private String comment;

    private String jobName;

    private Map<String, String> parameters;

    private String tableType;

    private List<AtlasColDto> atlasCols;

    private AtlasAlterDto alterCol;

    private List<AtlasTableLineageDto> tableLineages;

    private String outputTable;

    private List<String> inputTables;

    private String changelog;

    private String newCol;

    private String oldCol;

    private String newColType;
}
