package ua.com.smartjava.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

import reactor.bus.Event;
import reactor.fn.Consumer;


@Service
public class MessageReceiver implements Consumer<Event<Message>> {

    @Autowired
    CountDownLatch latch;

    @Override
    public void accept(Event<Message> ev) {
        Message message = ev.getData();
        System.out.println("Message received: " + message);
        latch.countDown();
    }
}