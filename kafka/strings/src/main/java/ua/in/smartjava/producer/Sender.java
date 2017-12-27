package ua.in.smartjava.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic}")
    private String topic;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    //  @Scheduled(fixedRate = 5000)
    private void sendFireAndForget() {
        sendFireAndForget(topic, "KAFKA" + atomicInteger.getAndIncrement());
    }

//    @Scheduled(fixedRate = 5000)
    private void sendSync() throws ExecutionException, InterruptedException {
        sendSync(topic, "KAFKA" + atomicInteger.getAndIncrement());
    }

    public void sendFireAndForget(String topic, String payload) {
        LOGGER.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }

    public void sendSync(String topic, String payload) throws ExecutionException, InterruptedException {
        LOGGER.info("sending payload='{}' to topic='{}'", payload, topic);
        SendResult<String, String> stringStringSendResult = kafkaTemplate.send(topic, payload).get();
        LOGGER.info(stringStringSendResult.toString());
    }

    public void sendAsync(String topic, String payload) throws ExecutionException, InterruptedException {
//        LOGGER.info("sending payload='{}' to topic='{}'", payload, topic);
        SendResult<String, String> stringStringSendResult = kafkaTemplate.send(topic, payload).get();
        LOGGER.info(stringStringSendResult.toString());
    }

    private class DemoProducerCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
            LOGGER.info(String.valueOf(MessageFormatter.format("{} {}",
                    "CallBack returned",
                    recordMetadata.topic() + recordMetadata.offset())));
        }
    }
}
