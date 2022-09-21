package com.github.gcestaro.ecommerce;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

class KafkaDispatcher<T> implements Closeable {

  private final KafkaProducer<String, Message<T>> producer;

  KafkaDispatcher() {
    this.producer = new KafkaProducer<>(getProperties());
  }

  void send(String topic, String key, CorrelationId correlationId, T payload)
      throws ExecutionException, InterruptedException {
    sendAsync(topic, key, correlationId, payload).get();
  }

  Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId correlationId,
      T payload) {
    var message = new Message<>(correlationId, payload);
    var callback = getCallback();
    var record = new ProducerRecord<>(topic, key, message);

    return producer.send(record, callback);
  }

  private Callback getCallback() {
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

  private Properties getProperties() {
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
