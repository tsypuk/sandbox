package ua.in.smartjava.dataformats;

import ua.in.smartjava.dataformats.avro.AvroWriter;
import ua.in.smartjava.generated.StringPair;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class AvroTest {

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

        DatumReader<StringPair> reader = new SpecificDatumReader<>(StringPair.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        StringPair result = reader.read(null, decoder);

        assertThat(result.getLeft().toString()).isEqualTo("L");
        assertThat(result.getRight().toString()).isEqualTo("R");
    }

    @Test
    public void testWithSchemaVerification() throws IOException {
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

        DatumReader<GenericRecord> reader = new GenericDatumReader<>();
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(file, reader);
        assertThat(schema).isEqualTo(dataFileReader.getSchema());
        assertThat(dataFileReader.hasNext()).isTrue();
        GenericRecord result = dataFileReader.next();
        assertThat(result.get("left").toString()).isEqualTo("L");
        assertThat(result.get("right").toString()).isEqualTo("R");
        assertThat(dataFileReader.hasNext()).isFalse();
    }

    @Test
    public void testAvroVersioning() throws IOException {
        Schema oldSchema = new AvroWriter().getSchema("avro/StringPair.avsc");
        File file = new File("data.avro");

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(oldSchema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(writer);
        dataFileWriter.create(oldSchema, file);

        StringPair datum = new StringPair();
        datum.setLeft("Left");
        datum.setRight("Right");
        dataFileWriter.append(datum);
        dataFileWriter.close();

        InputStream in = new BufferedInputStream(new FileInputStream(file));

        Schema newSchema = new AvroWriter().getSchema("avro/StringPairV2.avsc");
        DatumReader<GenericRecord> reader = new GenericDatumReader<>(oldSchema, newSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(in, null);
        GenericRecord result = reader.read(null, decoder);
        assertThat(result.get("left").toString()).isEqualTo("Left");
        assertThat(result.get("right").toString()).isEqualTo("Right");
        assertThat(result.get("description").toString()).isEqualTo("");
    }

    @Test
    public void test222() throws IOException {
        Schema oldSchema = new AvroWriter().getSchema("avro/StringPair.avsc");
        Schema newSchema = new AvroWriter().getSchema("avro/StringPairV2.avsc");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(oldSchema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        GenericRecord datum = new GenericData.Record(oldSchema);
        datum.put("left", "L");
        datum.put("right", "R");
        writer.write(datum, encoder);
        encoder.flush();

        DatumReader<GenericRecord> reader = new GenericDatumReader<>(oldSchema, newSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(),null);
        GenericRecord result = reader.read(null, decoder);
        assertThat(result.get("left").toString()).isEqualTo("L");
        assertThat(result.get("right").toString()).isEqualTo("R");
        assertThat(result.get("description").toString()).isEqualTo("");
    }

}