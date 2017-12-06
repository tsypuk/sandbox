package ua.in.smartjava.simple;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Client {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        final String hostPort = "cloudera-1:2181";
        ZooKeeper zooKeeper = new ZooKeeper(hostPort, 3000, null);
        ZooKeeper.States state = zooKeeper.getState();
        byte[] bn = zooKeeper.getData("/zookeeper", false, null);
        String data = new String(bn, "UTF-8");
        System.out.println(data);

        byte[] ztTest = zooKeeper.getData("/zk_test", false, null);
        System.out.println(new String(ztTest, "UTF-8"));

        List<ACL> zk_test = zooKeeper.getACL("/zk_test", null);
        System.out.println(state);

        String path = "/";
        Stat stat = znode_exists(path, zooKeeper);
        viewChilds(path, stat, zooKeeper);

    }

    private static void viewChilds(String path, Stat stat, ZooKeeper zk) throws KeeperException,
            InterruptedException, UnsupportedEncodingException {
        if (stat != null) {
            List<String> children = zk.getChildren(path, false);
            for (int i = 0; i < children.size(); i++) {
                System.out.println(children.get(i));
                if ("/".equals(path)) {
                    path += children.get(i);
                } else {
                    path = path + "/" + children.get(i);
                }
                viewChilds(path, znode_exists(path, zk), zk);
            }
        } else {
            System.out.println("Node does not exists");
        }
    }

    public static Stat znode_exists(String path, ZooKeeper zk) throws
            KeeperException, InterruptedException {
        return zk.exists(path, true);
    }

}
