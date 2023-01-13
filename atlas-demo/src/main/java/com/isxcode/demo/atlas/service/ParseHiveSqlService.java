package com.isxcode.demo.atlas.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import com.isxcode.demo.atlas.pojo.AtlasColDto;
import com.isxcode.demo.atlas.pojo.ParseSqlResultDto;
import org.apache.hadoop.hive.ql.lib.*;
import org.apache.hadoop.hive.ql.parse.*;
import org.springframework.stereotype.Service;

@Service
public class ParseHiveSqlService implements NodeProcessor {

    public ParseSqlResultDto parse(String sql) {

        ASTNode tree;
        try {
            tree = ParseUtils.parse(sql, null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
            tree = (ASTNode) tree.getChild(0);
        }

        Map<Rule, NodeProcessor> rules = new LinkedHashMap<>();
        ParseSqlResultDto parseSqlResultDto = new ParseSqlResultDto();
        Dispatcher dispatcher = new DefaultRuleDispatcher(this, rules, parseSqlResultDto);
        GraphWalker ogw = new DefaultGraphWalker(dispatcher);
        ArrayList<Node> topNodes = new ArrayList<>();
        topNodes.add(tree);
        try {
            ogw.startWalking(topNodes, null);
        } catch (SemanticException e) {
            throw new RuntimeException(e);
        }
        return parseSqlResultDto;
    }

    @Override
    public Object process(
            Node node, Stack<Node> stack, NodeProcessorCtx nodeProcessorCtx, Object... objects) {

        ASTNode pt = (ASTNode) node;
        ParseSqlResultDto parseHiveSqlDto = (ParseSqlResultDto) nodeProcessorCtx;
        switch (pt.getToken().getType()) {
            case HiveParser.TOK_CREATETABLE:
                parseHiveSqlDto.setExecuteType("CREATE_TABLE");
                String tableName = BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(0));
                parseHiveSqlDto.setName(tableName);
                parseHiveSqlDto.setQualifiedName(tableName);
                break;
            case HiveParser.TOK_TABLECOMMENT:
                String tableComment = BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(0));
                parseHiveSqlDto.setComment(tableComment);
                break;
            case HiveParser.TOK_TABCOL:
                String colName = BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(0));
                String colType = BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(1));
                String colComment = "";
                try {
                    colComment = BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(2));
                } catch (NullPointerException ignored) {

                }

                if (parseHiveSqlDto.getAtlasCols() == null) {
                    parseHiveSqlDto.setAtlasCols(new ArrayList<>());
                }

                parseHiveSqlDto
                        .getAtlasCols()
                        .add(
                                AtlasColDto.builder()
                                        .name(colName)
                                        .qualifiedName(colName)
                                        .comment(colComment)
                                        .type(parseColType(colType))
                                        .build());
                break;
            case HiveParser.TOK_TAB:
                parseHiveSqlDto.setOutputTable(
                        BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(0)));
                break;
            case HiveParser.TOK_TABREF:
                ASTNode tabTree = (ASTNode) pt.getChild(0);
                String inputTableName =
                        tabTree.getChildCount() == 1
                                ? BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(0))
                                : BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(0))
                                        + "."
                                        + tabTree.getChild(1);
                if (parseHiveSqlDto.getInputTables() == null) {
                    parseHiveSqlDto.setInputTables(new ArrayList<>());
                }
                parseHiveSqlDto.getInputTables().add(inputTableName);
                break;
            case HiveParser.TOK_ALTERTABLE:
                parseHiveSqlDto.setName(BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(0)));
                break;
            case HiveParser.TOK_ALTERTABLE_RENAME:
                String newName =
                        BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(0).getChild(0));
                String changeLog = "表名从" + parseHiveSqlDto.getName() + "更改为" + newName;
                parseHiveSqlDto.setExecuteType("ALTER_TABLE");
                parseHiveSqlDto.setNewName(newName);
                parseHiveSqlDto.setChangelog(changeLog);
                break;
            case HiveParser.TOK_ALTERTABLE_RENAMECOL:
                String oldCol = BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(0));
                String newCol = BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(1));
                colType = BaseSemanticAnalyzer.getUnescapedName((ASTNode) pt.getChild(2));

                parseHiveSqlDto.setExecuteType("ALTER_TABLE");
                parseHiveSqlDto.setOldCol(oldCol);
                parseHiveSqlDto.setNewCol(newCol);
                parseHiveSqlDto.setNewColType(colType);
                String changelog;
                if (oldCol.equals(newCol)) {
                    changelog = "更改字段 " + oldCol + " 字段类型为 " + colType.substring(4);
                } else {
                    changelog = "更改字段名称 " + oldCol + " 为 " + newCol + " ，且字段类型为 " + colType.substring(4);
                }
                parseHiveSqlDto.setChangelog(changelog);
                break;
        }

        return parseHiveSqlDto;
    }

    public String parseColType(String colType) {

        switch (colType) {
            case "TOK_STRING":
                return "string";
            case "TOK_INT":
                return "int";
            case "TOK_DATE":
                return "date";
            default:
                return null;
        }
    }
}
