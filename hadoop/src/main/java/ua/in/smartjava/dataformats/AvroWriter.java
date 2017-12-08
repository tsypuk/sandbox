package ua.in.smartjava.dataformats;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;

public class AvroWriter {

    public void write() throws IOException {
        Schema schema = getSchema();

        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer =
                new GenericDatumWriter(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();
    }

    public Schema getSchema() throws IOException {
        Schema.Parser parser = new Schema.Parser();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = loader.getResourceAsStream("avro/StringPair.avsc");
        return parser.parse(resourceAsStream);
    }

    public static void main(String[] args) throws IOException {
        new AvroWriter().write();
    }
}
