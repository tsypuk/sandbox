package ua.com.smartjava;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Before;

public class BaseSparkTest {

    protected JavaSparkContext sparkContext;

    protected SparkSession spark;

    @Before
    public void init() {
        System.setProperty("spark.sql.warehouse.dir", "file:///tmp/spark-warehouse");
        SparkConf sparkConfig = new SparkConf().setAppName("SparkJoins").setMaster("local[*]")
                .set("spark.sql.warehouse.dir", "file:///tmp/spark-warehouse");
        this.sparkContext = new JavaSparkContext(sparkConfig);

        this.spark = SparkSession.builder().appName("Simple Application")
                .master("local[*]")
                .getOrCreate();
    }

    @After
    public void tearDown() {
        sparkContext.close();
    }

}
