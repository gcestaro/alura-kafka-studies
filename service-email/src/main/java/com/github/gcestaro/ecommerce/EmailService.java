package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.consumer.KafkaService;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var emailService = new EmailService();
    try (var kafkaService = new KafkaService<>(EmailService.class.getSimpleName(),
        "ECOMMERCE_SEND_EMAIL", emailService::parse, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, Message<Email>> record) {
    var message = record.value();

    System.out.println("---------------------------------------------");
    System.out.println("Sending email");
    System.out.println(record.key());
    System.out.println(message);
    System.out.println(record.partition());
    System.out.println(record.offset());
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Email sent");
  }
}
