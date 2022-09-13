package com.github.gcestaro.ecommerce;

import java.util.UUID;

public class NewOrderMain {

  public static void main(String[] args) {

    try (var dispatcher = new KafkaDispatcher()) {
      for (int i = 0; i < 10; i++) {
        var key = UUID.randomUUID().toString();

        var value = "12345,312321,1234";
        dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);

        var emailMesssage = "Thank you for your order! We are processing your request.";
        dispatcher.send("ECOMMERCE_SEND_EMAIL", key, emailMesssage);
      }
    }
  }
}
