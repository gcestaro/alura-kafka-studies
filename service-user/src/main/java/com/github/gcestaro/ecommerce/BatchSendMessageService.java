package com.github.gcestaro.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class BatchSendMessageService {

  private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();
  private final Connection connection;

  BatchSendMessageService() throws SQLException {
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

  public static void main(String[] args)
      throws SQLException, ExecutionException, InterruptedException {
    var batchSendMessageService = new BatchSendMessageService();

    try (var kafkaService = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
        "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS", batchSendMessageService::parse, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException {
    var message = record.value();

    var topicName = message.getPayload();

    System.out.println("---------------------------------------------");
    System.out.println("Processing new batch");
    System.out.println("Topic: " + topicName);

    var users = findAllUsers();

    for (var user : users) {
      userDispatcher.sendAsync(topicName, user.getUuid(),
          message.getId().continueWith(
              BatchSendMessageService.class.getSimpleName()), user);

      System.out.println("Sent for user " + user.getUuid());
    }
  }

  private List<User> findAllUsers() throws SQLException {
    var exists = connection.prepareStatement(
        "select uuid from Users");
    var results = exists.executeQuery();

    var users = new ArrayList<User>();

    while (results.next()) {
      String uuid = results.getString("uuid");
      users.add(new User(uuid));
    }

    return users;
  }
}
