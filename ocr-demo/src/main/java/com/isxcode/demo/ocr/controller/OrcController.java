package com.isxcode.demo.ocr.controller;

import com.isxcode.demo.ocr.pojo.OrcResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/ocr")
public class OrcController {

    @GetMapping("/getImageText")
    public String getImageText() throws IOException {

        getExcelText();

        return "执行成功";
    }

    public List<List<String>> getExcelText() throws IOException {

        String fileDir = "D://imgDir";

        File[] files = new File(fileDir).listFiles();
        List<List<String>> tables = new ArrayList<>();

        assert files != null;
        for (File file : files) {

            try (FileInputStream imageInputStream = new FileInputStream(file.getPath())) {
                byte[] bytes = new byte[imageInputStream.available()];
                imageInputStream.read(bytes);

                String url = "https://tysbgpu.market.alicloudapi.com/api/predict/ocr_general";

                Map<String, String> requestHeader = new HashMap<>();
                String appcode = "";
                requestHeader.put("Authorization", "APPCODE " + appcode);
                requestHeader.put("Content-Type", "application/json; charset=UTF-8");

                String requestBody = "{\n" +
                        "  \"image\": \"" + Base64.getEncoder().encodeToString(bytes) + "\",\n" +
                        "  \"configure\": {\n" +
                        "    \"min_size\": 16,\n" +
                        "    \"output_prob\": true,\n" +
                        "    \"output_keypoints\": false,\n" +
                        "    \"skip_detection\": false,\n" +
                        "    \"without_predicting_direction\": false\n" +
                        "  }\n" +
                        "}";

                OrcResponse orcResponse = httpUtils(url, requestHeader, requestBody, OrcResponse.class);

                final List<String>[] col = new List[]{new ArrayList<>()};
                orcResponse.getRet().forEach(e -> {
                    if (e.getRect().getLeft() < 1900) {
                        col[0].add(e.getWord());
                    } else {
                        col[0].add(e.getWord());
                        tables.add(col[0]);
                        col[0] = new ArrayList<>();
                    }
                });
            }
        }

        return tables;
    }

    public <A> A httpUtils(String url, Map<String, String> headerParams, String requestBodyStr, Class<A> targetClass) {

        HttpHeaders headers = new HttpHeaders();
        headerParams.forEach(headers::add);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyStr, headers);

        return new RestTemplate().exchange(url, HttpMethod.POST, requestEntity, targetClass).getBody();
    }
}
