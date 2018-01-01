package in.ua.smartjava.chat;

import java.util.LinkedHashSet;

import in.ua.smartjava.grpc.Chat;
import in.ua.smartjava.grpc.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ChatService extends ChatServiceGrpc.ChatServiceImplBase {
    private LinkedHashSet<StreamObserver<Chat.ChatMessageFromServer>> observers = new LinkedHashSet<>();

    @Override
    public StreamObserver<Chat.ChatMessage> chat(StreamObserver<Chat.ChatMessageFromServer> responseObserver) {
        observers.add(responseObserver);
        return new StreamObserver<Chat.ChatMessage>() {
            @Override
            public void onNext(Chat.ChatMessage value) {
                Chat.ChatMessageFromServer message = Chat.ChatMessageFromServer.newBuilder()
                        .setMessage(value)
                        .build();
                observers.stream().forEach(
                        o -> o.onNext(message));
            }

            @Override
            public void onError(Throwable t) {
                observers.remove(responseObserver);
            }

            @Override
            public void onCompleted() {
                observers.remove(responseObserver);
            }
        };
    }
}
