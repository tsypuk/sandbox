package ua.com.smartjava.streaming;

import org.apache.spark.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;

import java.util.Arrays;

import scala.Tuple2;

public class Streaming {

    public static void main(String[] args) throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setMaster("local[1]").setAppName("NetworkWordCount");
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(5));

        // Create a DStream that will connect to hostname:port, like localhost:9999
        JavaReceiverInputDStream<String> lines = streamingContext.socketTextStream("localhost", 9999);

        lines.print();
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(x.split(" ")).iterator());

        // Count each word in each batch
        JavaPairDStream<String, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s, 1));
        JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey((i1, i2) -> i1 + i2);

        // Print the first ten elements of each RDD generated in this DStream to the console
        pairs.print();

        streamingContext.start();              // Start the computation
        streamingContext.awaitTermination();   // Wait for the computation to terminate

    }
}
