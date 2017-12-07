package ua.in.smartjava.mapreducers.temperature;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;

public class MaxTemperatureReducer implements Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> outputCollector,
                       Reporter reporter) throws IOException {
        int maxValue = Integer.MIN_VALUE;
        for (Iterator<IntWritable> it = values; it.hasNext(); ) {
            IntWritable value = it.next();
            maxValue = Math.max(maxValue, value.get());
        }
        outputCollector.collect(key, new IntWritable(maxValue));
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf jobConf) {

    }
}
