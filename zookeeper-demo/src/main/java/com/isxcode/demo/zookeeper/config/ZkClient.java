package com.isxcode.demo.zookeeper.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkClient implements Watcher {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZooKeeper initZkClient(String connectString) {

        ZooKeeper zookeeper;
        try {
            zookeeper = new ZooKeeper(connectString, 2000, this);
            countDownLatch.await();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        return zookeeper;
    }

    @Override
    public void process(WatchedEvent event) {

        if (event.getState() == Event.KeeperState.SyncConnected) {
            countDownLatch.countDown();
        }
    }

}
