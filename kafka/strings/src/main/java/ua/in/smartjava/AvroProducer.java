package ua.in.smartjava;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Collections;
import java.util.Properties;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import ua.in.smartjava.data.Customer;

public class AvroProducer {
    public static void main(String[] args) {
        final String schemaUrl = "http://localhost:8081";
        final String topic = "avro-customer";

        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer", KafkaAvroSerializer.class);
        producerProps.put("value.serializer", KafkaAvroSerializer.class);
        producerProps.put("schema.registry.url", schemaUrl);

        String schemaString = "{\"namespace\": \"customerManagement.avro\",\"type\": \"record\", " +
                "\"name\": \"Customer\"," +
                "\"fields\": [" +
                "{\"name\": \"id\", \"type\": \"int\"}," +
                "{\"name\": \"name\", \"type\": \"string\"}," +
                "{\"name\": \"email\", \"type\": [\"null\",\"string\"], \"default\":\"null\" }" +
                "]}";

        Producer<String, GenericRecord> producer = new KafkaProducer<>(producerProps);
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(schemaString);

        Customer customer = Customer.builder().id(100).email("L@gmail.com").name("John").build();
        GenericRecord genericrecord = new GenericData.Record(schema);
        genericrecord.put("id", customer.getId());
        genericrecord.put("name", customer.getName());
        genericrecord.put("email", customer.getEmail());

        ProducerRecord<String, GenericRecord> data = new ProducerRecord<>(topic, genericrecord);
        producer.send(data);
        producer.close();

        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "localhost:9092");
        consumerProps.put("key.deserializer", KafkaAvroDeserializer.class);
        consumerProps.put("value.deserializer", KafkaAvroDeserializer.class);
        consumerProps.put("schema.registry.url", schemaUrl);

        Consumer<String, Customer> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singleton(topic));
        ConsumerRecords<String, Customer> records = consumer.poll(5000);
        for (ConsumerRecord<String, Customer> record : records) {
            System.out.println(record.value().getName());
        }
        consumer.commitSync();
        consumer.close();
    }

}
