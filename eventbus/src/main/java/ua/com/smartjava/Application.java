package ua.com.smartjava;

import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import reactor.Environment;
import reactor.bus.EventBus;
import ua.com.smartjava.message.MessagePublisher;
import ua.com.smartjava.message.MessageReceiver;
import ua.com.smartjava.quote.QuotePublisher;
import ua.com.smartjava.quote.QuoteReceiver;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static reactor.bus.selector.Selectors.$;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application implements CommandLineRunner {

    private static final int NUMBER_OF_QUOTES = 10;

    @Bean
    Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }

    @Autowired
    private EventBus eventBus;

    @Autowired
    private QuoteReceiver quoteReceiver;

    @Autowired
    private QuotePublisher quotePublisher;

    @Autowired
    MessagePublisher messagePublisher;

    @Autowired
    MessageReceiver messageReceiver;

    @Bean
    public CountDownLatch latch() {
        return new CountDownLatch(NUMBER_OF_QUOTES);
    }

    @Bean
    Faker faker() {
        return new Faker(new Locale("en"));
    }

    @Override
    public void run(String... args) throws Exception {
        eventBus.on($("quotes"), quoteReceiver);
        quotePublisher.publishQuotes(NUMBER_OF_QUOTES);

        eventBus.on($("messages"), messageReceiver);
        messagePublisher.publishMessages(100);
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext app = SpringApplication.run(Application.class, args);

        app.getBean(CountDownLatch.class).await(1, TimeUnit.SECONDS);

        app.getBean(Environment.class).shutdown();
    }

}
