package com.github.gcestaro.ecommerce;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

class KafkaService implements Closeable {


  private final KafkaConsumer<String, String> consumer;
  private final ConsumerFunction function;

  KafkaService(String consumerGroup, String topic, ConsumerFunction function) {
    this.function = function;
    this.consumer = new KafkaConsumer<>(getProperties(consumerGroup));
    consumer.subscribe(Collections.singletonList(topic));
  }

  void run() {
    while (true) {
      var records = consumer.poll(Duration.ofMillis(100));
      if (!records.isEmpty()) {
        System.out.println("Found " + records.count() + " records!");

        for (var record : records) {
          function.consume(record);
        }
      }
    }
  }

  private Properties getProperties(String consumerGroup) {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
    properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");

    return properties;
  }

  @Override
  public void close() {
    consumer.close();
  }
}
