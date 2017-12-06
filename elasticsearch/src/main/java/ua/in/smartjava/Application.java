package ua.in.smartjava;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class Application implements CommandLineRunner {


    @Autowired
    private BookService bookService;

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
/*
        printElasticSearchInfo();

        bookService.save(new Book("1001", "Elasticsearch Basics", "Rambabu Posa", "23-FEB-2017"));
        bookService.save(new Book("1002", "Apache Lucene Basics", "Rambabu Posa", "13-MAR-2017"));
        bookService.save(new Book("1003", "Apache Solr Basics", "Rambabu Posa", "21-MAR-2017"));

        //fuzzey search
        Page<Book> books = bookService.findByAuthor("Rambabu", new PageRequest(0, 10));

        //List<Book> books = bookService.findByTitle("Elasticsearch Basics");

        books.forEach(x -> System.out.println(x));
*/

    }

    //useful for debug, print elastic search details
    private void printElasticSearchInfo() {

//        System.out.println("--ElasticSearch--");
//        Client client = es.getClient();
//        Map<String, String> asMap = client.settings().getAsMap();
//
//        asMap.forEach((k, v) -> {
//            System.out.println(k + " = " + v);
//        });
//        System.out.println("--ElasticSearch--");
    }

    @Bean
    public RestClient restHighLevelClient() {
        return
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                ).build();
    }

}