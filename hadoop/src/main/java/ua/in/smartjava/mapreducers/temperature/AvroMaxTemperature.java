package ua.in.smartjava.mapreducers.temperature;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import ua.in.smartjava.generated.WeatherRecord;

public class AvroMaxTemperature extends Configured implements Tool {
    private static final Schema SCHEMA = WeatherRecord.getClassSchema();
//            new Schema.Parser().parse(
//            "{" +
//                    "  \"type\": \"record\"," +
//                    "  \"name\": \"WeatherRecord\"," +
//                    "  \"doc\": \"A weather reading.\"," +
//                    "  \"fields\": [" +
//                    "    {\"name\": \"year\", \"type\": \"int\"}," +
//                    "    {\"name\": \"temperature\", \"type\": \"int\"}," +
//                    "    {\"name\": \"stationId\", \"type\": \"string\"}" +
//                    "  ]" +
//                    "}"
//    );

    public static class MaxTemperatureMapperFromNcdc extends Mapper<LongWritable, Text, AvroKey<Integer>,
            AvroValue<GenericRecord>> {
        private NcdcRecordParser parser = new NcdcRecordParser();
        private GenericRecord record = new GenericData.Record(SCHEMA);

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            parser.parse(value.toString());
            if (parser.isValidTemperature()) {
                record.put("year", parser.getYearInt());
                record.put("temperature", parser.getAirTemperature());
                record.put("stationId", parser.getStationId());
                context.write(new AvroKey<>(parser.getYearInt()),
                        new AvroValue<>(record));
            }
        }
    }

    public static class MaxTemperatureMapperGeneric extends Mapper<AvroKey<WeatherRecord>, NullWritable,
            AvroKey<Integer>,
            AvroValue<GenericRecord>> {
        private GenericRecord record = new GenericData.Record(SCHEMA);

        @Override
        protected void map(AvroKey<WeatherRecord> key, NullWritable value, Context context)
                throws IOException, InterruptedException {
            record.put("year", key.datum().get("year"));
            record.put("temperature", key.datum().get("temperature"));
            record.put("stationId", key.datum().get("stationId"));
            context.write(new AvroKey<>((Integer) key.datum().get("year")),
                    new AvroValue<>(record));
        }
    }

    public static class MaxTemperatureMapper extends Mapper<AvroKey<WeatherRecord>, NullWritable, AvroKey<Integer>,
            AvroValue<WeatherRecord>> {

        @Override
        protected void map(AvroKey<WeatherRecord> key, NullWritable value, Context context)
                throws IOException, InterruptedException {
            WeatherRecord weatherRecord = key.datum();
            context.write(new AvroKey<>((Integer) weatherRecord.get("year")),
                    new AvroValue<>(weatherRecord));
        }
    }

    public static class MaxTemperatureReducerGeneric extends Reducer<AvroKey<Integer>, AvroValue<GenericRecord>,
            AvroKey<GenericRecord>, NullWritable> {

        @Override
        protected void reduce(AvroKey<Integer> key, Iterable<AvroValue<GenericRecord>>
                values, Context context) throws IOException, InterruptedException {
            GenericRecord max = null;
            for (AvroValue<GenericRecord> value : values) {
                GenericRecord record = value.datum();
                if (max == null ||
                        (Integer) record.get("temperature") > (Integer) max.get("temperature")) {
                    max = record;
                }
            }
            context.write(new AvroKey(max), NullWritable.get());
        }
    }

    private static WeatherRecord newWeatherRecord(GenericRecord value) {
        return WeatherRecord.newBuilder()
                .setYear((Integer) value.get("year"))
                .setTemperature((Integer) value.get("temperature"))
                .setStationId((CharSequence) value.get("stationId"))
                .build();
    }

    public static class MaxTemperatureReducer extends Reducer<AvroKey<Integer>, AvroValue<GenericRecord>,
            AvroKey<WeatherRecord>, NullWritable> {

        @Override
        protected void reduce(AvroKey<Integer> key, Iterable<AvroValue<GenericRecord>> values, Context context)
                throws IOException, InterruptedException {
            WeatherRecord maxWeatherRecord = StreamSupport.stream(values.spliterator(), false)
                    .map(avroValue -> avroValue.datum())
                    .map(AvroMaxTemperature::newWeatherRecord)
                    .max(Comparator.comparingInt(WeatherRecord::getTemperature))
                    .orElse(null);
            context.write(new AvroKey(maxWeatherRecord), NullWritable.get());
        }
    }

    public static class AvrTemperatureReducer extends Reducer<AvroKey<Integer>, AvroValue<GenericRecord>,
            AvroKey<WeatherRecord>, NullWritable> {

        @Override
        protected void reduce(AvroKey<Integer> key, Iterable<AvroValue<GenericRecord>> values, Context context)
                throws IOException, InterruptedException {
            Set<String> stations = new HashSet<>();
            Double avrTemp = StreamSupport.stream(values.spliterator(), false)
                    .map(avroValue -> avroValue.datum())
                    .map(AvroMaxTemperature::newWeatherRecord)
                    .peek(weatherRecord -> stations.add(weatherRecord.getStationId().toString()))
                    .mapToInt(weather -> weather.getTemperature())
                    .average().getAsDouble();

            WeatherRecord avrWeather = WeatherRecord.newBuilder()
                    .setYear(key.datum())
                    .setStationId(stations.stream().collect(Collectors.joining(", ")))
                    .setTemperature(avrTemp.intValue())
                    .build();
            context.write(new AvroKey(avrWeather), NullWritable.get());
        }
    }

    public int run(String[] args) throws Exception {
        Job job = new Job(getConf(), "Max temperature avro");
        job.setJarByClass(getClass());

        job.getConfiguration().setBoolean(Job.MAPREDUCE_JOB_USER_CLASSPATH_FIRST, true);

        FileInputFormat.addInputPath(job, new Path("hdfs://cloudera-1:8020/user/root/weather.avro"));
        FileOutputFormat.setOutputPath(job, new Path("/tmp/results/avro"));

        AvroJob.setInputKeySchema(job, WeatherRecord.getClassSchema());
        AvroJob.setMapOutputKeySchema(job, Schema.create(Schema.Type.INT));
        AvroJob.setMapOutputValueSchema(job, SCHEMA);
        AvroJob.setOutputKeySchema(job, SCHEMA);
        job.setOutputFormatClass(AvroKeyOutputFormat.class);

//        job.setInputFormatClass(TextInputFormat.class);
//        job.setMapperClass(MaxTemperatureMapperFromNcdc.class);
        job.setInputFormatClass(AvroKeyInputFormat.class);
        job.setMapperClass(MaxTemperatureMapper.class);
        job.setReducerClass(AvrTemperatureReducer.class);
//        job.setReducerClass(MaxTemperatureReducer.class);
//        job.setMapperClass(MaxTemperatureMapperGeneric.class);
//        job.setReducerClass(MaxTemperatureReducerGeneric.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new AvroMaxTemperature(), args);
        System.exit(exitCode);
    }
}