package com.isxcode.demo.atlas.service;

import static org.apache.atlas.type.AtlasTypeUtil.*;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isxcode.demo.atlas.pojo.*;
import com.isxcode.demo.atlas.properties.AtlasProperties;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.audit.EntityAuditEventV2;
import org.apache.atlas.model.discovery.AtlasQuickSearchResult;
import org.apache.atlas.model.discovery.QuickSearchParameters;
import org.apache.atlas.model.discovery.SearchParameters;
import org.apache.atlas.model.instance.*;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.notification.EntityNotification;
import org.apache.atlas.model.typedef.*;
import org.eclipse.jetty.util.ajax.JSON;
import org.springframework.stereotype.Service;

@Service
public class AtlasUtilService {

    private final AtlasClientV2 clientV2;

    private final AtlasProperties atlasProperties;

    public AtlasUtilService(AtlasProperties atlasProperties) {

        this.atlasProperties = atlasProperties;
        this.clientV2 =
                new AtlasClientV2(
                        new String[] {atlasProperties.getHost()},
                        new String[] {atlasProperties.getUsername(), atlasProperties.getPassword()});
    }

    public void createTable(ParseSqlResultDto atlasDto) {

        AtlasEntity dbEntity = new AtlasEntity();
        String dbGuid = searchGuidByQualifiedName("hive_db", atlasDto.getDbName());
        dbEntity.setGuid(dbGuid);

        AtlasEntity tableEntity = new AtlasEntity();
        String tableGuid = initTable(atlasDto, dbEntity);
        tableEntity.setGuid(tableGuid);

        initColumn(atlasDto, tableEntity);
    }

    public void alterTable(ParseSqlResultDto atlasDto) {

        String tableGuid = searchGuidByQualifiedName("hive_table", atlasDto.getName());

        AtlasEntity tableEntity = getAtlasEntityByGuid(tableGuid);

        if (atlasDto.getNewName() != null) {
            tableEntity.setAttribute("name", atlasDto.getNewName());
            tableEntity.setAttribute("qualifiedName", atlasDto.getNewName());
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("changelog", atlasDto.getChangelog());
        tableEntity.setAttribute("parameters", parameters);

        try {
            AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo =
                    new AtlasEntity.AtlasEntityWithExtInfo(tableEntity);
            clientV2.updateEntity(atlasEntityWithExtInfo);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }

        if (atlasDto.getNewCol() != null) {

            String colGuid = searchGuidByQualifiedName("hive_column", atlasDto.getOldCol());

            AtlasEntity colEntity = getAtlasEntityByGuid(colGuid);
            colEntity.setGuid(colGuid);
            colEntity.setAttribute("name", atlasDto.getNewCol());
            colEntity.setAttribute("qualifiedName", atlasDto.getNewCol());
            colEntity.setAttribute("type", atlasDto.getNewColType());

            try {
                AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo =
                        new AtlasEntity.AtlasEntityWithExtInfo(colEntity);
                clientV2.updateEntity(atlasEntityWithExtInfo);
            } catch (AtlasServiceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String initTable(ParseSqlResultDto atlasDto, AtlasEntity dbEntity) {

        Set<String> superTypes = new HashSet<>();
        superTypes.add(AtlasBaseTypeDef.ATLAS_TYPE_DATASET);
        AtlasEntityDef tableDef = new AtlasEntityDef();
        tableDef.setSuperTypes(superTypes);

        AtlasEntity atlasEntity = new AtlasEntity(tableDef);
        atlasEntity.setTypeName("hive_table");

        // retention, sd, partitionKeys, columns, aliases, viewOriginalText, viewExpandedText, temporary
        atlasEntity.setAttribute("qualifiedName", atlasDto.getQualifiedName());
        atlasEntity.setAttribute("name", atlasDto.getName());
        atlasEntity.setAttribute("description", atlasDto.getDescription());
        atlasEntity.setAttribute("owner", atlasDto.getOwner());
        atlasEntity.setAttribute("createTime", System.currentTimeMillis());
        atlasEntity.setAttribute("lastAccessTime", System.currentTimeMillis());
        atlasEntity.setAttribute("comment", atlasDto.getComment());
        atlasEntity.setAttribute("tableType", atlasDto.getTableType());
        atlasEntity.setAttribute("parameters", atlasDto.getParameters());
        atlasEntity.setRelationshipAttribute("db", toAtlasRelatedObjectId(dbEntity));

        try {
            AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo =
                    new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity);
            EntityMutationResponse response = clientV2.createEntity(atlasEntityWithExtInfo);
            return response.getGuidAssignments().values().toArray(new String[] {})[0];
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void initColumn(ParseSqlResultDto atlasDto, AtlasEntity tableEntity) {

        List<AtlasEntity> colEntities = new ArrayList<>();

        Set<String> superTypes = new HashSet<>();
        superTypes.add(AtlasBaseTypeDef.ATLAS_TYPE_DATASET);
        AtlasEntityDef colDef = new AtlasEntityDef();
        colDef.setSuperTypes(superTypes);

        atlasDto
                .getAtlasCols()
                .forEach(
                        e -> {
                            AtlasEntity atlasEntity = new AtlasEntity(colDef);
                            atlasEntity.setTypeName("hive_column");
                            atlasEntity.setAttribute("qualifiedName", e.getQualifiedName());
                            atlasEntity.setAttribute("name", e.getName());
                            atlasEntity.setAttribute("description", e.getDescription());
                            atlasEntity.setAttribute("owner", e.getOwner());
                            atlasEntity.setAttribute("type", e.getType());
                            atlasEntity.setAttribute("comment", e.getComment());
                            atlasEntity.setRelationshipAttribute("table", toAtlasRelatedObjectId(tableEntity));

                            colEntities.add(atlasEntity);
                        });

        try {
            AtlasEntity.AtlasEntitiesWithExtInfo atlasEntitiesWithExtInfo =
                    new AtlasEntity.AtlasEntitiesWithExtInfo(colEntities);
            clientV2.createEntities(atlasEntitiesWithExtInfo);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public String linkTableLineage(AtlasTableLineageDto tableLineage) {

        Set<String> superTypes = new HashSet<>();
        superTypes.add(AtlasBaseTypeDef.ATLAS_TYPE_PROCESS);
        AtlasEntityDef lineageDef = new AtlasEntityDef();
        lineageDef.setSuperTypes(superTypes);

        AtlasEntity atlasEntity = new AtlasEntity(lineageDef);
        atlasEntity.setTypeName("hive_process");

        atlasEntity.setAttribute("qualifiedName", tableLineage.getJobName());
        atlasEntity.setAttribute("name", tableLineage.getJobName());
        atlasEntity.setAttribute("description", tableLineage.getDescription());
        atlasEntity.setAttribute("owner", tableLineage.getOwner());
        atlasEntity.setAttribute("startTime", System.currentTimeMillis());
        atlasEntity.setAttribute("endTime", System.currentTimeMillis());
        atlasEntity.setAttribute("userName", tableLineage.getUserName());
        atlasEntity.setAttribute("clusterName", tableLineage.getClusterName());
        atlasEntity.setAttribute("queryText", tableLineage.getQueryText());
        atlasEntity.setAttribute("queryPlan", tableLineage.getQueryPlan());
        atlasEntity.setAttribute("queryId", UUID.randomUUID().toString());
        atlasEntity.setAttribute(
                "operationType", EntityNotification.EntityNotificationV2.OperationType.ENTITY_CREATE);

        // 获取数据源实体
        String fromTableGuid = searchGuidByQualifiedName("hive_table", tableLineage.getFromTableName());
        AtlasEntity fromTableEntity = new AtlasEntity();
        fromTableEntity.setGuid(fromTableGuid);

        String toTableGuid = searchGuidByQualifiedName("hive_table", tableLineage.getToTableName());
        AtlasEntity toTableEntity = new AtlasEntity();
        toTableEntity.setGuid(toTableGuid);

        atlasEntity.setRelationshipAttribute(
                "inputs", toAtlasRelatedObjectIds(Collections.singletonList(fromTableEntity)));
        atlasEntity.setRelationshipAttribute(
                "outputs", toAtlasRelatedObjectIds(Collections.singletonList(toTableEntity)));

        try {
            AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo =
                    new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity);
            EntityMutationResponse response = clientV2.createEntity(atlasEntityWithExtInfo);
            return response.getGuidAssignments().values().toArray(new String[] {})[0];
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public String getColGuidByColName(String colName) {

        return searchGuidByQualifiedName("hive_column", colName);
    }

    public String searchGuidByQualifiedName(String typeName, String qualifiedName) {

        QuickSearchParameters quickSearchParameters = new QuickSearchParameters();
        quickSearchParameters.setTypeName(typeName);

        SearchParameters.FilterCriteria entityFilters = new SearchParameters.FilterCriteria();
        List<SearchParameters.FilterCriteria> criterion = new ArrayList<>();
        SearchParameters.FilterCriteria filterCriteria = new SearchParameters.FilterCriteria();
        filterCriteria.setAttributeName("qualifiedName");
        filterCriteria.setOperator(SearchParameters.Operator.EQ);
        filterCriteria.setAttributeValue(qualifiedName);
        criterion.add(filterCriteria);
        entityFilters.setCondition(SearchParameters.FilterCriteria.Condition.AND);
        entityFilters.setCriterion(criterion);
        quickSearchParameters.setEntityFilters(entityFilters);

        try {
            AtlasQuickSearchResult searchResult = clientV2.quickSearch(quickSearchParameters);
            List<AtlasEntityHeader> entityHeaders =
                    searchResult.getSearchResults().getEntities().stream()
                            .filter(e -> e.getStatus().equals(AtlasEntity.Status.ACTIVE))
                            .collect(Collectors.toList());
            return entityHeaders.get(0).getGuid();
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public AtlasEntity getAtlasEntityByGuid(String guid) {

        try {
            return clientV2.getEntityByGuid(guid).getEntity();
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void linkColLineage(AtlasColLineageDto atlasColLineage) {

        Set<String> superTypes = new HashSet<>();
        superTypes.add(AtlasBaseTypeDef.ATLAS_TYPE_PROCESS);
        AtlasEntityDef lineageDef = new AtlasEntityDef();
        lineageDef.setSuperTypes(superTypes);

        AtlasEntity atlasEntity = new AtlasEntity(lineageDef);
        atlasEntity.setTypeName("hive_column_lineage");

        // expression
        atlasEntity.setAttribute("description", atlasColLineage.getDescription());
        atlasEntity.setAttribute("owner", atlasColLineage.getOwner());
        atlasEntity.setAttribute("depenendencyType", "");

        String fromColGuid = getColGuidByColName(atlasColLineage.getFromColName());
        AtlasEntity fromColEntity = new AtlasEntity();
        fromColEntity.setGuid(fromColGuid);

        String toColGuid = getColGuidByColName(atlasColLineage.getToColName());
        AtlasEntity toColEntity = new AtlasEntity();
        toColEntity.setGuid(toColGuid);

        String jobName = fromColGuid + "->" + toColGuid;
        atlasEntity.setAttribute("qualifiedName", jobName);
        atlasEntity.setAttribute("name", jobName);

        AtlasEntity processEntity = new AtlasEntity();
        processEntity.setGuid(atlasColLineage.getProcessGuid());

        atlasEntity.setRelationshipAttribute(
                "inputs", toAtlasRelatedObjectIds(Collections.singletonList(fromColEntity)));
        atlasEntity.setRelationshipAttribute(
                "outputs", toAtlasRelatedObjectIds(Collections.singletonList(toColEntity)));
        atlasEntity.setRelationshipAttribute("query", toAtlasRelatedObjectId(processEntity));

        try {
            AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo =
                    new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity);
            clientV2.createEntity(atlasEntityWithExtInfo);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void createLineages(ParseSqlResultDto sqlResultDto) {

        for (AtlasTableLineageDto tableLineage : sqlResultDto.getTableLineages()) {
            tableLineage.setJobName(sqlResultDto.getJobName());
            String processGuid = linkTableLineage(tableLineage);

            for (AtlasColLineageDto colLineage : tableLineage.getColLineages()) {
                colLineage.setProcessGuid(processGuid);
            }
        }
    }

    public void createLink() {

        AtlasRelationship atlasRelationship = new AtlasRelationship();
        atlasRelationship.setTypeName("");
        atlasRelationship.setEnd1(toAtlasRelatedObjectId(null));
        atlasRelationship.setEnd2(toAtlasRelatedObjectId(null));
        try {
            clientV2.createRelationship(atlasRelationship);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTable(AtlasReqDto atlasReqDto) {

        String tableGuid = searchGuidByQualifiedName("hive_table", atlasReqDto.getTableName());

        try {
            clientV2.deleteEntityByGuid(tableGuid);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void createDatabase(AtlasReqDto atlasReqDto) {

        Set<String> superTypes = new HashSet<>();
        superTypes.add(AtlasBaseTypeDef.ATLAS_TYPE_ASSET);

        AtlasEntityDef dbDef = new AtlasEntityDef();
        dbDef.setSuperTypes(superTypes);
        AtlasEntity atlasEntity = new AtlasEntity(dbDef);
        atlasEntity.setTypeName("hive_db");

        atlasEntity.setAttribute("qualifiedName", atlasReqDto.getDbName());
        atlasEntity.setAttribute("name", atlasReqDto.getDbName());
        atlasEntity.setAttribute("description", "");
        atlasEntity.setAttribute("owner", "");
        atlasEntity.setAttribute("clusterName", "");
        atlasEntity.setAttribute("location", "");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("alive", "1");
        atlasEntity.setAttribute("parameters", parameters);
        atlasEntity.setAttribute("ownerName", "");
        atlasEntity.setIsIncomplete(true);

        try {
            AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo =
                    new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity);
            clientV2.createEntity(atlasEntityWithExtInfo);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDatabase(AtlasReqDto atlasReqDto) {

        String dbGuid = searchGuidByQualifiedName("hive_db", atlasReqDto.getDbName());

        try {
            clientV2.deleteEntityByGuid(dbGuid);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> queryChangelogs(AtlasReqDto atlasReqDto) {

        List<String> changelogs = new ArrayList<>();

        AtlasClientV3 clientV3 =
                new AtlasClientV3(
                        new String[] {atlasProperties.getHost()},
                        new String[] {atlasProperties.getUsername(), atlasProperties.getPassword()});

        String tableGuid = searchGuidByQualifiedName("hive_table", atlasReqDto.getTableName());
        try {
            List<EntityAuditEventV2> auditEvents =
                    clientV3.getAuditEvents(
                            tableGuid,
                            atlasReqDto.getPageSize(),
                            (atlasReqDto.getPage() - 1) * atlasReqDto.getPageSize(),
                            "desc",
                            "timestamp",
                            null);
            for (int i = 0; i < auditEvents.size(); i++) {
                EntityAuditEventV2 entityAuditEventV2 =
                        JSONObject.parseObject(JSON.toString(auditEvents.get(i)), EntityAuditEventV2.class);
                AtlasEntity atlasEntity =
                        JSONObject.parseObject(entityAuditEventV2.getDetails().substring(8), AtlasEntity.class);
                if (atlasEntity.getAttribute("parameters") != null) {
                    Map parameters =
                            JSONObject.parseObject(
                                    String.valueOf(atlasEntity.getAttribute("parameters")), Map.class);
                    changelogs.add(String.valueOf(parameters.get("changelog")));
                }
            }
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }

        return changelogs;
    }

    public void createLabel(String labelName) {

        AtlasClassificationDef atlasClassificationDef = new AtlasClassificationDef();
        atlasClassificationDef.setName(labelName);
        atlasClassificationDef.setDescription("");

        List<AtlasStructDef.AtlasAttributeDef> attributeDefs = new ArrayList<>();
        //        AtlasStructDef.AtlasAttributeDef atlasAttributeDef = new
        // AtlasStructDef.AtlasAttributeDef();
        //        atlasAttributeDef.setCardinality(AtlasStructDef.AtlasAttributeDef.Cardinality.SINGLE);
        //        atlasAttributeDef.setName("value1");
        //        atlasAttributeDef.setTypeName(AtlasEntityDef.ATLAS_TYPE_STRING);
        //        attributeDefs.add(atlasAttributeDef);
        atlasClassificationDef.setAttributeDefs(attributeDefs);

        List<AtlasClassificationDef> classificationDefs = new ArrayList<>();
        classificationDefs.add(atlasClassificationDef);

        AtlasTypesDef typesDef = new AtlasTypesDef();
        typesDef.setClassificationDefs(classificationDefs);
        try {
            clientV2.createAtlasTypeDefs(typesDef);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLabel(String labelName) {

        try {
            clientV2.deleteTypeByName(labelName);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void linkToLabel(String tableName, String labelName) {

        ClassificationAssociateRequest request = new ClassificationAssociateRequest();

        AtlasClassification classification = new AtlasClassification();
        classification.setTypeName(labelName);
        request.setClassification(classification);

        List<String> tableGuids = new ArrayList<>();
        String tableGuid = searchGuidByQualifiedName("hive_table", tableName);
        tableGuids.add(tableGuid);
        request.setEntityGuids(tableGuids);

        try {
            clientV2.addClassification(request);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeToLabel(String tableName, String labelName) {

        String tableGuid = searchGuidByQualifiedName("hive_table", tableName);

        try {
            clientV2.deleteClassification(tableGuid, labelName);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public AtlasLineageInfo queryTableLineage(String tableName) {

        String tableGuid = searchGuidByQualifiedName("hive_table", tableName);

        try {
            return clientV2.getLineageInfo(tableGuid, AtlasLineageInfo.LineageDirection.BOTH, 4);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public AtlasLineageInfo queryColLineage(String colName) {

        String colGuid = searchGuidByQualifiedName("hive_column", colName);

        try {
            return clientV2.getLineageInfo(colGuid, AtlasLineageInfo.LineageDirection.BOTH, 4);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCustomType(String typeName) {

        List<AtlasStructDef.AtlasAttributeDef> attributeDefs = new ArrayList<>();
        AtlasStructDef.AtlasAttributeDef atlasAttributeDef = new AtlasStructDef.AtlasAttributeDef();
        atlasAttributeDef.setTypeName(AtlasEntityDef.ATLAS_TYPE_STRING);
        atlasAttributeDef.setName("attr1");
        attributeDefs.add(atlasAttributeDef);

        AtlasStructDef.AtlasAttributeDef name = new AtlasStructDef.AtlasAttributeDef();
        atlasAttributeDef.setTypeName(AtlasEntityDef.ATLAS_TYPE_STRING);
        atlasAttributeDef.setName("name");
        attributeDefs.add(name);

        AtlasStructDef.AtlasAttributeDef qualifiedName = new AtlasStructDef.AtlasAttributeDef();
        atlasAttributeDef.setTypeName(AtlasEntityDef.ATLAS_TYPE_STRING);
        atlasAttributeDef.setName("qualifiedName");
        attributeDefs.add(qualifiedName);

        AtlasEntityDef atlasEntityDef = new AtlasEntityDef();
        atlasEntityDef.setName(typeName);
        atlasEntityDef.setDescription("api desc");
        atlasEntityDef.setAttributeDefs(attributeDefs);

        List<AtlasEntityDef> entityDefs = new ArrayList<>();
        entityDefs.add(atlasEntityDef);

        AtlasTypesDef typesDef = new AtlasTypesDef();
        typesDef.setEntityDefs(entityDefs);

        try {
            clientV2.createAtlasTypeDefs(typesDef);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void createApi(String apiName) {

        Set<String> superTypes = new HashSet<>();
        superTypes.add(AtlasBaseTypeDef.ATLAS_TYPE_DATASET);
        AtlasEntityDef tableDef = new AtlasEntityDef();
        tableDef.setSuperTypes(superTypes);

        AtlasEntity atlasEntity = new AtlasEntity(tableDef);
        atlasEntity.setTypeName("api3");

        atlasEntity.setAttribute("name", apiName);
        atlasEntity.setAttribute("qualifiedName", apiName);
        atlasEntity.setAttribute("attr1", "hhh");
        atlasEntity.setAttribute("attr2", "xxx");

        Map<String, String> customAttributes = new HashMap<>();
        customAttributes.put("custom1", "1");
        customAttributes.put("custom2", "2");
        customAttributes.put("custom3", "3");
        atlasEntity.setCustomAttributes(customAttributes);

        try {
            AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo =
                    new AtlasEntity.AtlasEntityWithExtInfo(atlasEntity);
            clientV2.createEntity(atlasEntityWithExtInfo);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AtlasEntity> queryTables(List<String> tableGuids) {

        try {
            return clientV2.getEntitiesByGuids(tableGuids).getEntities();
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public ParseSqlResultDto getTable(String tableName) {

        ParseSqlResultDto result = new ParseSqlResultDto();

        String tableGuid = searchGuidByQualifiedName("hive_table", tableName);

        try {
            AtlasEntity entity = clientV2.getEntityByGuid(tableGuid).getEntity();
            Map<String, Object> attributes = entity.getAttributes();
            result.setName(String.valueOf(attributes.get("name")));
            List<AtlasColDto> columns =
                    JSONArray.parseArray(JSON.toString(attributes.get("columns")), AtlasColDto.class);
            List<String> colGuids =
                    columns.stream()
                            .filter(e -> "hive_column".equals(e.getTypeName()))
                            .map(AtlasColDto::getGuid)
                            .collect(Collectors.toList());

            columns = new ArrayList<>();
            List<AtlasEntity> colEntities = clientV2.getEntitiesByGuids(colGuids).getEntities();
            for (AtlasEntity colEntity : colEntities) {

                AtlasColDto atlasColDto = new AtlasColDto();

                String type = String.valueOf(colEntity.getAttribute("type"));
                atlasColDto.setType(type);

                String qualifiedName = String.valueOf(colEntity.getAttribute("qualifiedName"));
                atlasColDto.setQualifiedName(qualifiedName);

                String name = String.valueOf(colEntity.getAttribute("name"));
                atlasColDto.setName(name);

                columns.add(atlasColDto);
            }
            result.setAtlasCols(columns);
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public List<String> searchFullText(String searchWord) {

        List<String> words = new ArrayList<>();

        try {
            List<AtlasEntityHeader> entities =
                    clientV2.dslSearchWithParams(searchWord, 2, 2).getEntities();
            List<String> guidCollects =
                    entities.stream()
                            .filter(e -> AtlasEntity.Status.ACTIVE.equals(e.getStatus()))
                            .map(AtlasEntityHeader::getGuid)
                            .collect(Collectors.toList());

            List<AtlasEntity> newEntities = clientV2.getEntitiesByGuids(guidCollects).getEntities();

            newEntities.forEach(e -> words.add(String.valueOf(e.getAttribute("name"))));
        } catch (AtlasServiceException e) {
            throw new RuntimeException(e);
        }

        return words;
    }
}
