package com.isxcode.demo.poi.controller;

import com.isxcode.demo.jsoup.controller.JsoupController;
import com.isxcode.demo.ocr.controller.OrcController;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/poi")
@RequiredArgsConstructor
public class PoiController {

    private final OrcController orcController;

    private final JsoupController jsoupController;

    @GetMapping("/listToExcel")
    public String listToExcel() throws IOException {

        String excelFilePath = "D://one.xlsx";

        // 爬取图片
        jsoupController.downloadImages();

        // orc分析图片,返回list
        List<List<String>> table = orcController.getExcelText();

        // list生成excel文件
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet sheet = workbook.createSheet();

            table.forEach(row -> {
                XSSFRow metaRow = sheet.createRow(table.indexOf(row));
                row.forEach(data -> {
                    XSSFCell cell = metaRow.createCell(row.indexOf(data));
                    cell.setCellValue(data);
                });
            });

            workbook.write(Files.newOutputStream(Paths.get(excelFilePath)));
        }

        return "执行完成";
    }
}
