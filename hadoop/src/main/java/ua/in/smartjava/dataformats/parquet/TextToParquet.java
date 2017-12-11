package ua.in.smartjava.dataformats.parquet;

import java.io.IOException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import parquet.example.data.Group;
import parquet.example.data.GroupFactory;
import parquet.example.data.simple.SimpleGroupFactory;
import parquet.hadoop.example.ExampleOutputFormat;
import parquet.schema.MessageType;
import parquet.schema.MessageTypeParser;

public class TextToParquet extends Configured implements Tool {
    private static final MessageType SCHEMA = MessageTypeParser.parseMessageType(
            "message Line {\n" +
                    "  required int64 offset;\n" +
                    "  required binary line (UTF8);\n" +
                    "}");

    public static class TextToParquetMapper
            extends Mapper<LongWritable, Text, Void, Group> {

        private GroupFactory groupFactory = new SimpleGroupFactory(SCHEMA);

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            Group group = groupFactory.newGroup()
                    .append("offset", key.get())
                    .append("line", value.toString());
            context.write(null, group);
        }
    }

    @Override
    public int run(String[] args) throws Exception {

        Job job = new Job(getConf(), "Text to Parquet");
        job.setJarByClass(getClass());

        FileInputFormat.addInputPath(job, new Path("hdfs://cloudera-1:8020/user/root/doyle.txt"));
        FileOutputFormat.setOutputPath(job, new Path("/tmp/results/parquet"));

        job.setMapperClass(TextToParquetMapper.class);
        job.setNumReduceTasks(0);

        job.setOutputFormatClass(ExampleOutputFormat.class);
        ExampleOutputFormat.setSchema(job, SCHEMA);

        job.setOutputKeyClass(Void.class);
        job.setOutputValueClass(Group.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new TextToParquet(), args);
        System.exit(exitCode);
    }
}
