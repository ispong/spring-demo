package com.isxcode.demo.atlas.pojo;

import lombok.Data;

@Data
public class TableMapping {

    private String fromDbGuid;

    private String fromTable;

    private String toDbGuid;

    private String toTable;
}
