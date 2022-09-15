package com.github.gcestaro.ecommerce;

import java.math.BigDecimal;
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
    Order order = record.value();

    System.out.println("---------------------------------------------");
    System.out.println("Processing new order, checking for fraud");
    System.out.println(record.key());
    System.out.println(order);
    System.out.println(record.partition());
    System.out.println(record.offset());
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    if (isFraud(order)) {

      System.out.println("It is a fraud!");
    } else {

      System.out.println("Order processed");
    }
  }

  private boolean isFraud(Order order) {
    // let's say it is a fraud if the value exceeds or is equal to 4.500

    return order.getValue().compareTo(BigDecimal.valueOf(4500)) >= 0;
  }
}
