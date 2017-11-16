package ua.com.smartjava.message;

import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class MessagePublisher {
    @Autowired
    EventBus eventBus;

    @Autowired
    CountDownLatch latch;

    @Autowired
    Faker faker;

    public void publishMessages(int numberOfQuotes) throws InterruptedException {
        long start = System.currentTimeMillis();

        AtomicInteger counter = new AtomicInteger(1);

        for (int i = 0; i < numberOfQuotes; i++) {
            eventBus.notify("messages", Event.wrap(
                    Message.builder()
                            .name(faker.name().fullName())
                            .address(faker.address().fullAddress())
                            .id(faker.number().randomDigit())
                            .build()));
        }

        latch.await();

        long elapsed = System.currentTimeMillis() - start;

        System.out.println("Elapsed time: " + elapsed + "ms");
        System.out.println("Average time per quote: " + elapsed / numberOfQuotes + "ms");
    }

}
