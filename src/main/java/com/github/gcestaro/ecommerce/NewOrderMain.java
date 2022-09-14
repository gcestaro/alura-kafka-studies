package com.github.gcestaro.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;

public class NewOrderMain {

  public static void main(String[] args) {

    try (var orderDispatcher = new KafkaDispatcher<Order>()) {
      try (var emailDispatcher = new KafkaDispatcher<Email>()) {
        for (int i = 0; i < 10; i++) {
          var userId = UUID.randomUUID().toString();
          var orderId = UUID.randomUUID().toString();
          var amount = Math.random() * 5000 + 1;

          var order = new Order(userId, orderId, new BigDecimal(amount));

          orderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);

          var emailMesssage = "Thank you for your order! We are processing your request.";
          var email = new Email("test@test.com", emailMesssage);
          emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, email);
        }
      }
    }
  }
}
