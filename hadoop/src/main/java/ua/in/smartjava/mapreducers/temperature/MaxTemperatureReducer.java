package ua.in.smartjava.mapreducers.temperature;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;

public class MaxTemperatureReducer implements Reducer<Text, IntWritable, Text, IntWritable> {

    protected static final String TARGET_WORD = "Watson";

    private boolean containsTargetWord(Text key) {
        return key.toString().equals(TARGET_WORD);
    }

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> outputCollector, Reporter reporter) throws IOException {
        System.out.println("Reducer processing");
        if (containsTargetWord(key)) {
            int wordCount = 0;
            for (Iterator<IntWritable> it = values; it.hasNext(); ) {
                IntWritable value = it.next();
                wordCount += value.get();
            }
            outputCollector.collect(key, new IntWritable(wordCount));
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf jobConf) {

    }
}
