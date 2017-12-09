package ua.in.smartjava.dataformats;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ua.in.smartjava.generated.PersonCountry;
import ua.in.smartjava.generated.StringPair;

public class AvroWriter {

    private Faker faker = new Faker(Locale.UK);

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

    public void generateFakeData(int count) throws Exception {
        Schema schema = getSchema("avro/PersonCountry.avsc");
        File file = new File("person.avro");

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);

        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(writer)) {
            dataFileWriter.create(schema, file);

            Stream.generate(personFakerSupplier)
                    .limit(count)
                    .forEach(person -> appendToDataFile(dataFileWriter, person));
        }
    }

    private void appendToDataFile(DataFileWriter<GenericRecord> dataFileWriter, GenericRecord stringPair) {
        try {
            dataFileWriter.append(stringPair);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Supplier<PersonCountry> personFakerSupplier = () -> PersonCountry.newBuilder()
            .setName(faker.name())
            .setCountry(faker.lastName()).build();

    public Schema getSchema(String schemaFile) throws IOException {
        Schema.Parser parser = new Schema.Parser();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = loader.getResourceAsStream(schemaFile);
        return parser.parse(resourceAsStream);
    }

    public static void main(String[] args) throws Exception {
        new AvroWriter().generateFakeData(5_000);
    }
}
