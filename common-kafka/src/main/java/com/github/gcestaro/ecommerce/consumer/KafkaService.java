package com.github.gcestaro.ecommerce.consumer;

import com.github.gcestaro.ecommerce.ConsumerFunction;
import com.github.gcestaro.ecommerce.Message;
import com.github.gcestaro.ecommerce.dispatcher.GsonSerializer;
import com.github.gcestaro.ecommerce.dispatcher.KafkaDispatcher;
import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaService<T> implements Closeable {


  private final KafkaConsumer<String, Message<T>> consumer;
  private final ConsumerFunction<T> function;

  public KafkaService(String consumerGroup, String topic, ConsumerFunction<T> function,
      Map<String, String> properties) {
    this(consumerGroup, function, properties);
    consumer.subscribe(Collections.singletonList(topic));
  }

  public KafkaService(String consumerGroup, Pattern pattern, ConsumerFunction<T> function,
      Map<String, String> properties) {
    this(consumerGroup, function, properties);
    consumer.subscribe(pattern);
  }

  private KafkaService(String consumerGroup, ConsumerFunction<T> function,
      Map<String, String> properties) {
    this.function = function;
    this.consumer = new KafkaConsumer<>(getProperties(properties, consumerGroup));
  }

  public void run() throws ExecutionException, InterruptedException {
    try (var deadLetter = new KafkaDispatcher<>()) {

      while (true) {
        var records = consumer.poll(Duration.ofMillis(100));
        if (!records.isEmpty()) {
          System.out.println("Found " + records.count() + " records!");

          for (var record : records) {
            try {
              function.consume(record);
            } catch (Exception e) {
              // catch any exception and only logs. Process next message
              e.printStackTrace();
              var message = record.value();
              var correlationId = message.getId();

              deadLetter.send("ECOMMERCE_DEADLETTER", correlationId.toString(),
                  correlationId.continueWith("DeadLetter"),
                  new GsonSerializer<>().serialize("", message));
            }
          }
        }
      }
    }
  }

  private Properties getProperties(Map<String, String> overrideProperties, String consumerGroup) {
    var properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        GsonDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
    properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //latest

    properties.putAll(overrideProperties);

    return properties;
  }

  @Override
  public void close() {
    consumer.close();
  }
}
