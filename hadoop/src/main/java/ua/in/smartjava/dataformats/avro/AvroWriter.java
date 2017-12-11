package ua.in.smartjava.dataformats.avro;

import com.github.javafaker.Faker;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ua.in.smartjava.generated.PersonCountry;
import ua.in.smartjava.generated.WeatherRecord;

public class AvroWriter {

    private static final Faker faker = new Faker(Locale.UK);
    private static final Random random = new Random();

    private static final Supplier<PersonCountry> personFakerSupplier = () ->
            PersonCountry.newBuilder()
                    .setName(faker.name())
                    .setCountry(faker.lastName()).build();

    private static final Supplier<WeatherRecord> weatherFakerSupplier = () ->
            WeatherRecord.newBuilder()
                    .setTemperature(generatedRandomTemperature(-40, 40))
                    .setYear(generateYear(1990, 27))
                    .setStationId(faker.country())
                    .build();

    private static final int generatedRandomTemperature(int min, int max) {
        return min + random.nextInt(max - min);
    }
    private static final int generateYear(int startYear, int delta) {
        return startYear + random.nextInt(delta);
    }

    public void write() throws IOException {
        Schema schema = getSchema("avro/StringPair.avsc");

        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new GenericDatumWriter(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();
    }

    public void generateFakeData(int count, String schemaName, String fileName, Supplier<? extends GenericRecord>
            supplier) throws Exception {
        Schema schema = getSchema(schemaName);

        Configuration conf = new Configuration();
        try (FileSystem fs = FileSystem.get(URI.create(fileName), conf, "hdfs")) {

            if (!fs.exists(new Path(fileName))) {
                createNewFile(fileName);
            }
            FSDataOutputStream outputStream = fs.create(new Path(fileName));

            DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);

            try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(writer)) {
                dataFileWriter.create(schema, outputStream);

                Stream.generate(supplier)
                        .limit(count)
                        .forEach(person -> appendToDataFile(dataFileWriter, person));
            }
        }

    }

    private void appendToDataFile(DataFileWriter<GenericRecord> dataFileWriter, GenericRecord genericRecord) {
        try {
            dataFileWriter.append(genericRecord);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createNewFile(String dst) {
        Configuration conf = new Configuration();
        try (FileSystem fs = FileSystem.get(URI.create(dst), conf, "hdfs")) {
            fs.create(new Path(dst));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Schema getSchema(String schemaFile) throws IOException {
        Schema.Parser parser = new Schema.Parser();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = loader.getResourceAsStream(schemaFile);
        return parser.parse(resourceAsStream);
    }

    public static void main(String[] args) throws Exception {
//        new AvroWriter().generateFakeData(5_000, "avro/PersonCountry.avsc", "person.avro", personFakerSupplier);
        new AvroWriter().generateFakeData(1_000_000, "avro/AvroTemperature.avsc",
                "hdfs://cloudera-1:8020/user/root/weather.avro", weatherFakerSupplier);
    }
}
