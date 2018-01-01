package in.ua.smartjava.chat;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ChatServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server chatServer = ServerBuilder.forPort(7777)
                .addService(new ChatService())
                .build();

        chatServer.start();
        chatServer.awaitTermination();
    }
}
