package com.isxcode.demo.jsoup.controller;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/jsoup")
public class JsoupController {

    @GetMapping("/scanAndDownloadPic")
    public String scanAndDownloadPic() throws IOException {


        String htmlUrl = "http://shsxwjyxh.cn/edu/h5/html/noticeDetail.html?id=43";
        Document document = Jsoup.connect(htmlUrl).get();

        Element content = document.getElementsByClass("detailBox").get(0);

        return "执行成功";
    }

    @GetMapping("/parseHtmlText")
    public List<String> parseHtmlText() {

        return getHtmlImageList();
    }

    public List<String> getHtmlImageList() {
        String htmlText = "<p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbf0id61a6t17f71v8jtau1ak52.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbf7krfklo1duh1ub31tkmjm13.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbf9q4d1t0b11dqvg51lr5kug4.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbfaudnl7v1rqs1f8n1keb1cpr5.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbfc6jf1snai4k16ca1t8m1a156.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbfeq3e1an51kqsghv1ofb85f7.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbffev02pe1fn68o4rjo15us8.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbfh11e1mgn1mbugs4or31n3f9.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbfhnmtfilbihd14padm8a.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbfinfv10451tgqq581gds1f6vb.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbfjns61ue61deeo5n1kf31vtac.png\\\" style=\\\"width: 898px;\\\"></p><p><img src=\\\"http://shsxwjyxh.cn/file/edu/summernote/o_1glbfkg8qig8bnrj6q6891j6vd.png\\\" style=\\\"width: 898px;\\\"></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p><p><br></p>";

        Elements pElements = Jsoup.parse(htmlText).body().getElementsByTag("p");

        List<String> imgUrls = new ArrayList<>();
        for (Element p : pElements) {
            String imgUrl = p.getElementsByTag("img").attr("src").replace("\\\"", "");
            if(!imgUrl.isEmpty()){
                imgUrls.add(imgUrl);
            }
        }
        return imgUrls;
    }

    @GetMapping("/downloadImages")
    public String downloadImages() throws IOException {

        String storeDir = "D://imgDir";
        String fileNameSuffix = ".png";
        String fileNamePrefix = "img_";
        List<String> htmlImageList = getHtmlImageList();

        for (String url : htmlImageList) {
            writeUrlToFile(url, storeDir, fileNamePrefix + htmlImageList.indexOf(url) + fileNameSuffix);
        }

        return "执行成功";
    }

    public void writeUrlToFile(String imgUrl, String storeDir,String fileName) throws IOException {

        URL url = new URL(imgUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();

        Path dirPath = Paths.get(storeDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path filePath = Paths.get(storeDir + File.separator + fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            IOUtils.copy(inputStream, outputStream);
        }
    }
}
