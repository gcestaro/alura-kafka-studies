package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.consumer.ConsumerService;
import com.github.gcestaro.ecommerce.consumer.ServiceRunner;
import com.github.gcestaro.ecommerce.database.LocalDatabase;
import com.github.gcestaro.ecommerce.dispatcher.KafkaDispatcher;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class FraudDetectorService implements ConsumerService<Order> {

  private static final String SQL_CREATE_TABLE_FRAUDS = "create table "
      + "Orders ("
      + "uuid varchar(200) primary key, "
      + "is_fraud boolean"
      + " )";
  private static final String SQL_EXISTS_ORDER = "select uuid from Orders where uuid = ? limit 1";

  private final KafkaDispatcher<Order> kafkaDispatcher = new KafkaDispatcher<>();

  private final LocalDatabase database;

  public FraudDetectorService() throws SQLException {
    database = new LocalDatabase("order_database");
    database.createIfNotExists(SQL_CREATE_TABLE_FRAUDS);
  }

  public static void main(String[] args) {
    new ServiceRunner<>(FraudDetectorService::new).start(1);
  }

  @Override
  public void parse(ConsumerRecord<String, Message<Order>> record)
      throws ExecutionException, InterruptedException, SQLException {
    var message = record.value();
    var order = message.getPayload();

    System.out.println("---------------------------------------------");
    System.out.println("Processing new order, checking for fraud");
    System.out.println(record.key());
    System.out.println(order);
    System.out.println(record.partition());
    System.out.println(record.offset());

    if (wasProcessed(order)) {
      System.out.println("Ignoring order " + order.getOrderId()
          + "due to it was already processed by fraud detector");
      return;
    }

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    if (isFraud(order)) {
      database.insert("insert into Orders (uuid, is_fraud) values (?, true)",
          Map.of(1, order.getOrderId()));

      System.out.println("It is a fraud!");
      kafkaDispatcher.send("ECOMMERCE_ORDER_REJECTED",
          FraudDetectorService.class.getSimpleName(),
          message.getId().continueWith(
              FraudDetectorService.class.getSimpleName()),
          order);
    } else {
      database.insert("insert into Orders (uuid, is_fraud) values (?, false)",
          Map.of(1, order.getOrderId()));

      kafkaDispatcher.send("ECOMMERCE_ORDER_APPROVED",
          order.getEmail(),
          message.getId().continueWith(
              FraudDetectorService.class.getSimpleName()),
          order);
      System.out.println("Order processed");
    }
  }

  private boolean wasProcessed(Order order) throws SQLException {
    return database.exists(SQL_EXISTS_ORDER, Map.of(1, order.getOrderId()));
  }

  @Override
  public String getTopic() {
    return "ECOMMERCE_NEW_ORDER";
  }

  @Override
  public String getConsumerGroup() {
    return FraudDetectorService.class.getSimpleName();
  }

  private boolean isFraud(Order order) {
    // let's say it is a fraud if the value exceeds or is equal to 4.500

    return order.getValue().compareTo(BigDecimal.valueOf(4500)) >= 0;
  }
}
