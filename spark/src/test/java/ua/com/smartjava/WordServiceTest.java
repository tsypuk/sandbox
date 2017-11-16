package ua.com.smartjava;

import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WordServiceTest extends BaseSparkTest {

    private WordService wordService = new WordService();

    @Test
    public void topX() throws Exception {
        JavaRDD<String> stringsFromFile = sparkContext.parallelize(
                Arrays.asList("a", "b", "c", "d", "e", "b", "a", "a"));
        List<String> topXStrings = wordService.topX(stringsFromFile, 5);
        assertThat(topXStrings.get(0)).isEqualTo("a");
    }

}