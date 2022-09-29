package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.consumer.ConsumerService;
import com.github.gcestaro.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService implements ConsumerService<Email> {

  public static void main(String[] args) {
    new ServiceRunner<>(EmailService::new).start(5);
  }

  @Override
  public void parse(ConsumerRecord<String, Message<Email>> record) {
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

  @Override
  public String getTopic() {
    return "ECOMMERCE_SEND_EMAIL";
  }

  @Override
  public String getConsumerGroup() {
    return EmailService.class.getSimpleName();
  }
}
