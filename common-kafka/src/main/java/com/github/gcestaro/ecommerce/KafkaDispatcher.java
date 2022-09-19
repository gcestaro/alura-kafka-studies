package com.github.gcestaro.ecommerce;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

class KafkaDispatcher<T> implements Closeable {

  private final KafkaProducer<String, T> producer;

  KafkaDispatcher() {
    this.producer = new KafkaProducer<>(getProperties());
  }

  void send(String topic, String key, T value) {
    Callback callback = getCallback();
    var record = new ProducerRecord<>(topic, key, value);

    try {
      producer.send(record, callback).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private static Callback getCallback() {
    return (data, ex) -> {
      if (ex != null) {
        ex.printStackTrace();
        return;
      }
      System.out.println("Success sending to " + data.topic()
          + ":::partition" + data.partition() + "/ offset" + data.offset()
          + "/ timestamp" + data.timestamp());
    };
  }

  private static Properties getProperties() {
    var properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        GsonSerializer.class.getName());
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");

    return properties;
  }

  @Override
  public void close() {
    producer.close();
  }
}
