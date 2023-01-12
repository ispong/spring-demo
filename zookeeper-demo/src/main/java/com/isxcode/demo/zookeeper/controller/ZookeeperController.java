package com.isxcode.demo.zookeeper.controller;

import lombok.RequiredArgsConstructor;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/zookeeper")
@RequiredArgsConstructor
public class ZookeeperController {

    private final ZooKeeper zkClient;

    @GetMapping("/getChild")
    public List<String> getChild() throws InterruptedException, KeeperException {

        return zkClient.getChildren("/", false);
    }
}
