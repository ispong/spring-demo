package com.isxcode.demo.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/hadoop")
public class HadoopController {

    @GetMapping("/createDir")
    public String createDir() {

        Configuration configuration = new Configuration();
        configuration.set("dfs.replication", "1");
        configuration.set("dfs.client.use.datanode.hostname", "true");
        configuration.set("fs.defaultFS", "hdfs://isxcode:30116");

        try {
            FileSystem fileSystem =
                    FileSystem.get(new URI("hdfs://isxcode:30116"), configuration, "ispong");
            FSDataOutputStream out = fileSystem.create(new Path("/ispong"), true, 4096);
            return "执行成功";
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
