package ua.in.smartjava;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.StringUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

public class WritableTest {

    @Test
    public void testIntWritable() throws IOException {
        // Given
        Writable writable = new IntWritable(127);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);

        // When
        writable.write(dataOut);
        dataOut.close();

        byte[] bytes = out.toByteArray();

        // Then
        assertThat(bytes.length).isEqualTo(4);
        assertThat(StringUtils.byteToHexString(bytes)).isEqualTo("0000007f");

        IntWritable intWritable = new IntWritable();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        intWritable.readFields(dataInputStream);
        dataInputStream.close();

        assertThat(intWritable.get()).isEqualTo(127);
    }
}
