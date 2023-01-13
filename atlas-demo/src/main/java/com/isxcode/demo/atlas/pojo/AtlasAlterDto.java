package com.isxcode.demo.atlas.pojo;

import lombok.Data;

@Data
public class AtlasAlterDto {

    private String table;

    private String oldCol;

    private String newCol;

    private String newColType;
}
