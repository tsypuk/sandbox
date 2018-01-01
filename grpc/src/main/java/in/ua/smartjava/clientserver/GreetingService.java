package in.ua.smartjava.clientserver;

import in.ua.smartjava.grpc.GreetingServiceGrpc;
import in.ua.smartjava.grpc.HelloRequest;
import in.ua.smartjava.grpc.HelloResponse;
import io.grpc.stub.StreamObserver;

public class GreetingService extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void greeting(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        System.out.println(request);
        responseObserver.onNext(
                HelloResponse.newBuilder()
                        .setGreeting("Hello " + request.getFirstname())
                        .build());
        responseObserver.onCompleted();
    }
}
