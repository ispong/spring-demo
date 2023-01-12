package com.isxcode.demo.ocr.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class OrcResponse {

    @JsonAlias("request_id")
    private String requestId;

    private List<Ret> Ret;

    private Boolean success;
}
