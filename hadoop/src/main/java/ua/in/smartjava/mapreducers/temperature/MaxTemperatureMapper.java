package ua.in.smartjava.mapreducers.temperature;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;

public class MaxTemperatureMapper implements Mapper<LongWritable, Text, Text, IntWritable> {

    private static final int MISSING = 9999;

    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> outputCollector, Reporter
            reporter) throws IOException {

        int count = 0;
        String line = value.toString();
        String year = line.substring(15, 19);
        String temp = line.substring(87, 92);
        if (!missing(temp)) {
            int airTemperature = Integer.parseInt(temp);
            outputCollector.collect(new Text(year), new IntWritable(airTemperature));
        }
    }

    private boolean missing(String temp) {
        return temp.equals("+9999");
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf jobConf) {

    }

}
