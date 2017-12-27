package ua.in.smartjava;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class SteamKafka {
    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> source = builder.stream("wordcount-input");

        /* Direct writes
        source.to("direct-stream");
        */

        /* Splitted to words
        source
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .to("no-space-stream");
        */

        // GroupBy operations through KTable
        final Pattern pattern = Pattern.compile("\\W+");
        source
                .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
                .map((key, value) -> new KeyValue<>(value, value))
                .filter((key, value) -> (!"the".equals(value)))
                .groupByKey()
                .count("CountStore")
                .mapValues(value -> value + ":" + Long.toString(value)).toStream()
                .to("wordcount-output");

        KafkaStreams directStream = new KafkaStreams(builder, props);
        directStream.start();

        Thread.sleep(60_000);
        directStream.close();

    }
}
