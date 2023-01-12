package com.isxcode.demo.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkConfig {

    @Bean("zkClient")
    public ZooKeeper initZkClient() {

        return new ZkClient().initZkClient("isxcode:2181");
    }

}

