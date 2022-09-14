package com.github.gcestaro.ecommerce;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class FraudDetectorService {

  public static void main(String[] args) {
    var fraudDetectorService = new FraudDetectorService();

    try (var kafkaService = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
        "ECOMMERCE_NEW_ORDER", fraudDetectorService::parse, Order.class, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, Order> record) {
    System.out.println("---------------------------------------------");
    System.out.println("Processing new order, checking for fraud");
    System.out.println(record.key());
    System.out.println(record.value());
    System.out.println(record.partition());
    System.out.println(record.offset());
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Order processed");
  }
}
