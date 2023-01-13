package com.isxcode.demo.atlas.service;

import java.util.List;

import com.isxcode.demo.atlas.pojo.AtlasColLineageDto;
import com.isxcode.demo.atlas.pojo.AtlasReqDto;
import com.isxcode.demo.atlas.pojo.AtlasTableLineageDto;
import com.isxcode.demo.atlas.pojo.ParseSqlResultDto;
import lombok.RequiredArgsConstructor;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtlasService {

    private final AtlasUtilService atlasUtilService;

    private final ParseHiveSqlService parseHiveSqlService;

    public void parseHiveDDL(AtlasReqDto atlasReqDto) {

        ParseSqlResultDto sqlResultDto = parseHiveSqlService.parse(atlasReqDto.getDdlSql());

        if ("CREATE_TABLE".equals(sqlResultDto.getExecuteType())) {

            sqlResultDto.setDbName(atlasReqDto.getDbName());
            atlasUtilService.createTable(sqlResultDto);
        }

        if ("ALTER_TABLE".equals(sqlResultDto.getExecuteType())) {

            atlasUtilService.alterTable(sqlResultDto);
        }
    }

    public void parseHiveDML(AtlasReqDto atlasReqDto) {

        ParseSqlResultDto sqlResultDto = parseHiveSqlService.parse(atlasReqDto.getDmlSql());
        sqlResultDto.setDbGuid(atlasReqDto.getDbGuid());
        sqlResultDto.setJobName(atlasReqDto.getJobName());

        //        createLineages(sqlResultDto);
    }

    public void parseSqoop(AtlasReqDto atlasReqDto) {

        AtlasTableLineageDto atlasTableLineage = new AtlasTableLineageDto();
        atlasTableLineage.setFromTableName(atlasReqDto.getTableMapping().getFromTable());
        atlasTableLineage.setToTableName(atlasReqDto.getTableMapping().getToTable());
        atlasTableLineage.setJobName(atlasReqDto.getJobName());
        atlasTableLineage.setUserName(atlasReqDto.getUserName());
        atlasTableLineage.setQueryText(atlasReqDto.getQueryText());
        atlasTableLineage.setQueryPlan(atlasReqDto.getQueryPlan());

        String processGuid = atlasUtilService.linkTableLineage(atlasTableLineage);

        atlasReqDto
                .getColMapping()
                .forEach(
                        e -> {
                            AtlasColLineageDto metaColLineage = new AtlasColLineageDto();
                            metaColLineage.setProcessGuid(processGuid);
                            metaColLineage.setFromColName(e.getFromCol());
                            metaColLineage.setToColName(e.getToCol());
                            atlasUtilService.linkColLineage(metaColLineage);
                        });
    }

    public void deleteTable(AtlasReqDto atlasReqDto) {

        atlasUtilService.deleteTable(atlasReqDto);
    }

    public void createDatabase(AtlasReqDto atlasReqDto) {

        atlasUtilService.createDatabase(atlasReqDto);
    }

    public void deleteDatabase(AtlasReqDto atlasReqDto) {

        atlasUtilService.deleteDatabase(atlasReqDto);
    }

    public List<String> queryChangelogs(AtlasReqDto atlasReqDto) {

        return atlasUtilService.queryChangelogs(atlasReqDto);
    }

    public void createLabel(AtlasReqDto atlasReqDto) {

        atlasUtilService.createLabel(atlasReqDto.getLabelName());
    }

    public void deleteLabel(AtlasReqDto atlasReqDto) {

        atlasUtilService.deleteLabel(atlasReqDto.getLabelName());
    }

    public void linkToLabel(AtlasReqDto atlasReqDto) {

        atlasUtilService.linkToLabel(atlasReqDto.getTableName(), atlasReqDto.getLabelName());
    }

    public void removeToLabel(AtlasReqDto atlasReqDto) {

        atlasUtilService.removeToLabel(atlasReqDto.getTableName(), atlasReqDto.getLabelName());
    }

    public AtlasLineageInfo queryTableLineage(AtlasReqDto atlasReqDto) {

        return atlasUtilService.queryTableLineage(atlasReqDto.getTableName());
    }

    public AtlasLineageInfo queryColLineage(AtlasReqDto atlasReqDto) {

        return atlasUtilService.queryColLineage(atlasReqDto.getColName());
    }

    public void addCustomType(AtlasReqDto atlasReqDto) {

        atlasUtilService.addCustomType(atlasReqDto.getTypeName());
    }

    public void createApi(AtlasReqDto atlasReqDto) {

        atlasUtilService.createApi(atlasReqDto.getApiName());
    }

    public List<AtlasEntity> queryTables(AtlasReqDto atlasReqDto) {

        return atlasUtilService.queryTables(atlasReqDto.getTableGuids());
    }

    public ParseSqlResultDto getTable(AtlasReqDto atlasReqDto) {

        return atlasUtilService.getTable(atlasReqDto.getTableName());
    }

    public List<String> searchFullText(AtlasReqDto atlasReqDto) {

        return atlasUtilService.searchFullText(atlasReqDto.getSearchWord());
    }

    //  String jsonStr = "";
    //  AtlasEntity.AtlasEntityWithExtInfo entity = AtlasType.fromJson(jsonStr,
    // AtlasEntity.AtlasEntityWithExtInfo.class);

    //  AtlasEntityHeader firstEntityHeader = response.getFirstEntityUpdated() == null ?
    // response.getFirstEntityCreated() : response.getFirstEntityUpdated();
    //  tableEntity.setGuid(firstEntityHeader.getGuid());
}
