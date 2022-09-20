package com.github.gcestaro.ecommerce;

import java.util.Map;
import java.util.regex.Pattern;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

public class LogService {

  public static void main(String[] args) {
    var logService = new LogService();
    try (var kafkaService = new KafkaService<>(LogService.class.getSimpleName(),
        Pattern.compile("ECOMMERCE.*"), logService::parse,
        Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName()))) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, Message<String>> record) {
    var message = record.value();

    System.out.println("---------------------------------------------");
    System.out.println("LOG: " + record.topic());
    System.out.println(record.key());
    System.out.println(message.getPayload());
    System.out.println(record.partition());
    System.out.println(record.offset());
  }
}
