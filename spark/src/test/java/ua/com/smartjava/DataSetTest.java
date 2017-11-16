package ua.com.smartjava;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DataSetTest extends BaseSparkTest{

    @Test
    public void testLoadParquet() {
        Dataset<Row> names = spark.read().format("parquet").load("./names.parquet");
        names.show();
    }

    @Test
    public void testLoadJson() {
        Dataset<Row> peopleDF =
                spark.read().format("json").load("./people.json");
        peopleDF.show();
//        peopleDF.select("name", "company").write().format("parquet").save("namesAndCompany.parquet");
        peopleDF.select("*").write().format("parquet").save(UUID.randomUUID() +"test.parquet");
    }

    @Test
    public void testLoadFromString() {
        List<String> jsonData = Arrays.asList(
                "{\"name\":\"Yin\",\"address\":{\"city\":\"Columbus\",\"state\":\"Ohio\"}}");
        Dataset<String> anotherPeopleDataset = spark.createDataset(jsonData, Encoders.STRING());
        anotherPeopleDataset.show();
    }

    @Test
    public void testSql() {
        Dataset<Row> people = spark.read().json("./people.json");
        people.printSchema();
        people.createOrReplaceTempView("people");

        // SQL statements can be run by using the sql methods provided by spark
        Dataset<Row> namesDF = spark.sql("SELECT name FROM people");
        namesDF.show();
    }

    @Test
    public void testParquetFile() {
        Dataset<Row> parquetNames = spark.sql("SELECT * FROM parquet.`./names.parquet`");
        parquetNames.show();

    }
}
