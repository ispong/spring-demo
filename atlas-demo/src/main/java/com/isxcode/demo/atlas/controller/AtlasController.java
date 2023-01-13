package com.isxcode.demo.atlas.controller;

import java.util.List;

import com.isxcode.demo.atlas.pojo.AtlasReqDto;
import com.isxcode.demo.atlas.pojo.ParseSqlResultDto;
import com.isxcode.demo.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atlas")
@RequiredArgsConstructor
public class AtlasController {

    private final AtlasService atlasService;

    @PostMapping("/open/parseHiveDDL")
    public void parseHiveDDL(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.parseHiveDDL(atlasReqDto);
    }

    @PostMapping("/open/parseHiveDML")
    public void parseHiveDML(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.parseHiveDML(atlasReqDto);
    }

    @PostMapping("/open/parseSqoop")
    public void parseSqoop(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.parseSqoop(atlasReqDto);
    }

    @PostMapping("/open/deleteTable")
    public void deleteTable(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.deleteTable(atlasReqDto);
    }

    @PostMapping("/open/createDatabase")
    public void createDatabase(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.createDatabase(atlasReqDto);
    }

    @PostMapping("/open/deleteDatabase")
    public void deleteDatabase(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.deleteDatabase(atlasReqDto);
    }

    @PostMapping("/open/queryChangelog")
    public List<String> queryChangelogs(@RequestBody AtlasReqDto atlasReqDto) {

        return atlasService.queryChangelogs(atlasReqDto);
    }

    @PostMapping("/open/createLabel")
    public void createLabel(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.createLabel(atlasReqDto);
    }

    @PostMapping("/open/deleteLabel")
    public void deleteLabel(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.deleteLabel(atlasReqDto);
    }

    @PostMapping("/open/linkToLabel")
    public void linkToLabel(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.linkToLabel(atlasReqDto);
    }

    @PostMapping("/open/removeToLabel")
    public void removeToLabel(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.removeToLabel(atlasReqDto);
    }

    @PostMapping("/open/queryTableLineage")
    public AtlasLineageInfo queryTableLineage(@RequestBody AtlasReqDto atlasReqDto) {

        return atlasService.queryTableLineage(atlasReqDto);
    }

    @PostMapping("/open/queryColLineage")
    public AtlasLineageInfo queryColLineage(@RequestBody AtlasReqDto atlasReqDto) {

        return atlasService.queryColLineage(atlasReqDto);
    }

    @PostMapping("/open/addCustomType")
    public void addCustomType(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.addCustomType(atlasReqDto);
    }

    @PostMapping("/open/createApi")
    public void createApi(@RequestBody AtlasReqDto atlasReqDto) {

        atlasService.createApi(atlasReqDto);
    }

    @PostMapping("/open/queryTables")
    public List<AtlasEntity> queryTables(@RequestBody AtlasReqDto atlasReqDto) {

        return atlasService.queryTables(atlasReqDto);
    }

    @PostMapping("/open/getTable")
    public ParseSqlResultDto getTable(@RequestBody AtlasReqDto atlasReqDto) {

        return atlasService.getTable(atlasReqDto);
    }

    @PostMapping("/open/searchFullText")
    public List<String> searchFullText(@RequestBody AtlasReqDto atlasReqDto) {

        return atlasService.searchFullText(atlasReqDto);
    }
}
