package com.github.gcestaro.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class CreateUserService {

  private final KafkaDispatcher<User> kafkaDispatcher = new KafkaDispatcher<>();
  private final Connection connection;

  CreateUserService() throws SQLException {
    var url = "jdbc:sqlite:target/users_database.db";
    connection = DriverManager.getConnection(url);

    try {
      connection.createStatement().execute("create table "
          + "Users ("
          + "uuid varchar(200) primary key, "
          + "email varchar(200)"
          + " )");
    } catch (SQLException ex) {
      System.out.println("Ignoring already created table Users");
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) throws SQLException {
    var createUserService = new CreateUserService();

    try (var kafkaService = new KafkaService<>(CreateUserService.class.getSimpleName(),
        "ECOMMERCE_NEW_ORDER", createUserService::parse, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
    var message = record.value();
    var order = message.getPayload();

    System.out.println("---------------------------------------------");
    System.out.println("Processing new order, checking for new user");
    System.out.println(order);

    if (isNewUser(order.getEmail())) {
      insertNewUser(order.getEmail());
    }
  }

  private void insertNewUser(String email) throws SQLException {
    var statement = connection.prepareStatement(
        "insert into Users (uuid, email) values (?, ?)");

    var userId = UUID.randomUUID().toString();

    statement.setString(1, userId);
    statement.setString(2, email);

    statement.execute();

    System.out.println("User " + userId + " , email " + email + " added.");

  }

  private boolean isNewUser(String email) throws SQLException {
    var exists = connection.prepareStatement(
        "select uuid from Users where email = ? limit 1");
    exists.setString(1, email);
    var results = exists.executeQuery();

    return !results.next();
  }
}
