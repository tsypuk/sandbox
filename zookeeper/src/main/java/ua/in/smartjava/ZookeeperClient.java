package ua.in.smartjava;

public class ZookeeperClient {

    public static void main(String[] args) {

//        if (args.length < 4)
//
//        {
//            System.err
//                    .println("USAGE: Executor hostPort znode filename program [args ...]");
//            System.exit(2);
//        }
//
        String hostPort = "cloudera-1:2181";
//        String znode = args[1];
        String znode = "zk_test";
        String filename = "zookeeper.log";
//        String exec[] = new String[args.length - 3];
        String exec[] = new String[1];
//        System.arraycopy(args, 3, exec, 0, exec.length);
        try {
            new Executor(hostPort, znode, filename, exec).run();
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }
}