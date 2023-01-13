package com.isxcode.demo.atlas.pojo;

import java.util.List;
import lombok.Data;

@Data
public class AtlasTableLineageDto {

    private String fromTableGuid;

    private String fromTableName;

    private String toTableGuid;

    private String toTableName;

    private String jobName;

    private String description;

    private String owner;

    private String userName;

    private String clusterName;

    private String queryPlan;

    private String queryText;

    private List<AtlasColLineageDto> colLineages;
}
