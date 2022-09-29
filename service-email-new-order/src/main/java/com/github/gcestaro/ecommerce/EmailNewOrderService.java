package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.consumer.KafkaService;
import com.github.gcestaro.ecommerce.dispatcher.KafkaDispatcher;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailNewOrderService {

  private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var emailService = new EmailNewOrderService();

    try (var kafkaService = new KafkaService<>(EmailNewOrderService.class.getSimpleName(),
        "ECOMMERCE_NEW_ORDER", emailService::parse, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, Message<Order>> record)
      throws ExecutionException, InterruptedException {
    var message = record.value();
    var order = message.getPayload();

    System.out.println("---------------------------------------------");
    System.out.println("Processing new order, preparing email");
    System.out.println(message);

    var emailMesssage = "Thank you for your order! We are processing your request.";
    var emailCode = new Email("test@test.com", emailMesssage);

    CorrelationId correlationId = message.getId()
        .continueWith(EmailNewOrderService.class.getSimpleName());

    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), correlationId, emailCode);
  }
}
