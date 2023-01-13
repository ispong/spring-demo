package com.isxcode.demo.atlas.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtlasColDto {

    private String qualifiedName;

    private String name;

    private String description;

    private String owner;

    private String type;

    private String comment;

    private String guid;

    private String typeName;
}
