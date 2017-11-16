package ua.com.smartjava;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SparkTest extends BaseSparkTest implements Serializable {

    @Test
    public void test() {
        JavaRDD<String> fileStrings = sparkContext.parallelize(Arrays.asList("a", "aaa", "ba", "c"));
        JavaRDD<Integer> map = fileStrings
                .filter(str -> str.contains("a"))
                .map(String::length);
        int total = map.reduce((a, b) -> a + b);
        System.out.println("total: " + total);

        assertThat(total).isEqualTo(6);
    }

    @Test
    public void testFolder() throws IOException {
        JavaRDD<String> fileStrings = sparkContext.textFile(listFilesOnly("/Users/rtsypuk/"));
        List<String> collect = fileStrings.filter(str -> str.contains("ERROR"))
                .collect();
        System.err.println(collect);
    }

    @Test
    public void testSrcFolder() {
        JavaRDD<String> fileStrings = sparkContext.textFile("./src/main/java/ua/com/smartjava/*");
        List<String> collect = fileStrings.filter(str -> str.contains("main"))
                .collect();
        assertThat(collect).hasSize(2);
    }

    @Test
    public void testPathFolders() throws IOException {
        StringUtils.join(Files.list(Paths.get("/"))
                        .filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .map(fileName -> fileName = "./" + fileName)
                        .peek(System.err::println)
                        .collect(Collectors.toList()),
                ","
        );
    }

    private String listFilesOnly(String root) throws IOException {
        return StringUtils.join(Files.list(Paths.get(root))
                        .filter(Files::isRegularFile)
                        .filter(file -> {
                            try {
                                return !Files.isHidden(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                return true;
                            }
                        })
                        .map(path -> path.getFileName().toString())
//                        .filter(name -> !name.startsWith("."))
//                        .filter(name -> !name.contains("log"))
                        .filter(name -> name.contains("log"))
                        .map(fileName -> fileName = root + fileName)
                        .peek(System.err::println)
                        .collect(Collectors.toList()),
                ","
        );
    }

}