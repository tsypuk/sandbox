package ua.in.smartjava.simple;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;
import java.util.List;

public class Client {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        final String hostPort = "cloudera-1:2181";
        ZooKeeper zooKeeper = new ZooKeeper(hostPort, 3000, null);
        ZooKeeper.States state = zooKeeper.getState();
        byte[] bn = zooKeeper.getData("/zookeeper", false, null);
        String data = new String(bn,"UTF-8");
        List<ACL> zk_test = zooKeeper.getACL("/zk_test", null);

        System.out.println(state);

    }
}
