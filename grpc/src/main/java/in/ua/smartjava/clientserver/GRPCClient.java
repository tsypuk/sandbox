package in.ua.smartjava.clientserver;

import java.util.Iterator;

import in.ua.smartjava.grpc.GreetingServiceGrpc;
import in.ua.smartjava.grpc.HelloRequest;
import in.ua.smartjava.grpc.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext(true)
                .build();
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        HelloResponse response = stub.greeting(buildRequest());
        System.out.println(response);
    }

    private static HelloRequest buildRequest() {
        return HelloRequest.newBuilder()
                .setFirstname("Roman")
                .setLastname("Tsypuk")
                .addHoobies("java")
                .addHoobies("radio")
                .addHoobies("sport")
                .putBagOfTricks("code", "education")
                .build();
    }
}
