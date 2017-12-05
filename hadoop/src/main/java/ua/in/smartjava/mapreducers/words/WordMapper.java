package ua.in.smartjava.mapreducers.words;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.StringTokenizer;

public class WordMapper implements Mapper<LongWritable, Text, Text, IntWritable> {

    private Text word = new Text();

    @Override
    public void map(LongWritable longWritable, Text value, OutputCollector<Text, IntWritable> outputCollector, Reporter reporter) throws IOException {
        String line = value.toString();
        StringTokenizer lineTokenizer = new StringTokenizer(line);
        while (lineTokenizer.hasMoreTokens()) {
            String cleaned = removeNonLettersOrNumbers(lineTokenizer.nextToken());
            word.set(cleaned);
            outputCollector.collect(word, new IntWritable(1));
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf jobConf) {

    }

    /**
     * Replaces all Unicode characters that are not either letters or numbers with
     * an empty string.
     * @param original  The original string.
     * @return  A string that contains only letters and numbers.
     */
    private String removeNonLettersOrNumbers(String original) {
        return original.replaceAll("[^\\p{L}\\p{N}]", "");
    }
}
