package ua.in.smartjava.dataformats;

import ua.in.smartjava.generated.StringPair;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AvroWriterTest {

    @Test
    public void test() throws IOException {
        StringPair datum = new StringPair();
        datum.setLeft("L");
        datum.setRight("R");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<StringPair> writer = new SpecificDatumWriter<>(StringPair.class);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();

        DatumReader<StringPair> reader =
                new SpecificDatumReader<>(StringPair.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(),
                null);
        StringPair result = reader.read(null, decoder);

        assertThat(result.getLeft().toString()).isEqualTo("L");
        assertThat(result.getRight().toString()).isEqualTo("R");
    }

    @Test
    public void test1() throws IOException {
        Schema schema = new AvroWriter().getSchema("avro/StringPair.avsc");
        File file = new File("data.avro");
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(writer);
        dataFileWriter.create(schema, file);

        StringPair datum = new StringPair();
        datum.setLeft("L");
        datum.setRight("R");
        dataFileWriter.append(datum);
        dataFileWriter.close();

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> dataFileReader =
                new DataFileReader<GenericRecord>(file, reader);
        assertThat(schema).isEqualTo(dataFileReader.getSchema());
        assertThat(dataFileReader.hasNext()).isTrue();
        GenericRecord result = dataFileReader.next();
        assertThat(result.get("left").toString()).isEqualTo("L");
        assertThat(result.get("right").toString()).isEqualTo("R");
        assertThat(dataFileReader.hasNext()).isFalse();
    }

}