package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.consumer.ConsumerService;
import com.github.gcestaro.ecommerce.consumer.ServiceRunner;
import com.github.gcestaro.ecommerce.dispatcher.KafkaDispatcher;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailNewOrderService implements ConsumerService<Order> {

  private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

  public static void main(String[] args) {
    new ServiceRunner<>(EmailNewOrderService::new).start(1);
  }

  @Override
  public void parse(ConsumerRecord<String, Message<Order>> record)
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

  @Override
  public String getTopic() {
    return "ECOMMERCE_NEW_ORDER";
  }

  @Override
  public String getConsumerGroup() {
    return EmailNewOrderService.class.getSimpleName();
  }
}
