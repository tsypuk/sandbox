package ua.in.smartjava.dataformats.parquet;

import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
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

import parquet.avro.AvroParquetOutputFormat;
import parquet.example.data.Group;

public class TextToParquetWithAvro extends Configured implements Tool {

    private static final Schema SCHEMA = new Schema.Parser().parse(
            "{\n" +
                    "  \"type\": \"record\",\n" +
                    "  \"name\": \"Line\",\n" +
                    "  \"fields\": [\n" +
                    "    {\"name\": \"offset\", \"type\": \"long\"},\n" +
                    "    {\"name\": \"line\", \"type\": \"string\"}\n" +
                    "  ]\n" +
                    "}");

    public static class TextToParquetMapper
            extends Mapper<LongWritable, Text, Void, GenericRecord> {

        private GenericRecord record = new GenericData.Record(SCHEMA);

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            record.put("offset", key.get());
            record.put("line", value.toString());
            context.write(null, record);
        }
    }

    @Override
    public int run(String[] args) throws Exception {

        Job job = new Job(getConf(), "Text to Parquet");
        job.setJarByClass(getClass());

        FileInputFormat.addInputPath(job, new Path("hdfs://cloudera-1:8020/user/root/doyle.txt"));
        FileOutputFormat.setOutputPath(job, new Path("/tmp/results/parquet_with_avro"));

        job.setMapperClass(TextToParquetMapper.class);
        job.setNumReduceTasks(0);

        job.setOutputFormatClass(AvroParquetOutputFormat.class);
        AvroParquetOutputFormat.setSchema(job, SCHEMA);

        job.setOutputKeyClass(Void.class);
        job.setOutputValueClass(Group.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new TextToParquetWithAvro(), args);
        System.exit(exitCode);
    }
}