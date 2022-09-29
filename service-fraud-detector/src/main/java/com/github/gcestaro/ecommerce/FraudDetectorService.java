package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.consumer.KafkaService;
import com.github.gcestaro.ecommerce.dispatcher.KafkaDispatcher;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class FraudDetectorService {

  private final KafkaDispatcher<Order> kafkaDispatcher = new KafkaDispatcher<>();

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var fraudDetectorService = new FraudDetectorService();

    try (var kafkaService = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
        "ECOMMERCE_NEW_ORDER", fraudDetectorService::parse, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, Message<Order>> record)
      throws ExecutionException, InterruptedException {
    var message = record.value();
    var order = message.getPayload();

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
      kafkaDispatcher.send("ECOMMERCE_ORDER_REJECTED",
          FraudDetectorService.class.getSimpleName(),
          message.getId().continueWith(
              FraudDetectorService.class.getSimpleName()),
          order);
    } else {
      kafkaDispatcher.send("ECOMMERCE_ORDER_APPROVED",
          order.getEmail(),
          message.getId().continueWith(
              FraudDetectorService.class.getSimpleName()),
          order);
      System.out.println("Order processed");
    }
  }

  private boolean isFraud(Order order) {
    // let's say it is a fraud if the value exceeds or is equal to 4.500

    return order.getValue().compareTo(BigDecimal.valueOf(4500)) >= 0;
  }
}
