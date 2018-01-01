package in.ua.smartjava.clientserver;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GRPCServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8080)
                .addService(new GreetingService())
                .build();

        server.start();
        server.awaitTermination();
    }
}
