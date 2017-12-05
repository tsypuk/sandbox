package ua.in.smartjava.mapreducers.words;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

import static ua.in.smartjava.Utils.asStream;

public class AllWordReducer implements Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> outputCollector,
                       Reporter reporter) throws IOException {
        int wordCount = asStream(values).mapToInt(intWritable -> intWritable.get()).sum();
        outputCollector.collect(key, new IntWritable(wordCount));
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf jobConf) {

    }
}
