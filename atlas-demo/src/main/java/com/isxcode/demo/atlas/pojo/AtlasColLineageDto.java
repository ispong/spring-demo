package com.isxcode.demo.atlas.pojo;

import lombok.Data;

@Data
public class AtlasColLineageDto {

    private String fromColGuid;

    private String toColGuid;

    private String processGuid;

    private String fromColName;

    private String toColName;

    private String owner;

    private String description;

    private String name;
}
