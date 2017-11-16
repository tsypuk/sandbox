package ua.com.smartjava;

import org.apache.spark.api.java.JavaRDD;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import scala.Serializable;
import scala.Tuple2;

public class WordService implements Serializable {

    public List<String> topX(JavaRDD<String> lines, int x) {
        return lines.map(String::toLowerCase)
                .flatMap(this::getWords)
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((key1, key2) -> key1 + key2)
                .mapToPair(Tuple2::swap)
                .sortByKey(false)
                .map(Tuple2::_2)
                .take(x);
    }

    public Iterator<String> getWords(String word){
        return Arrays.asList(word.split(" ")).iterator();
    }
}
