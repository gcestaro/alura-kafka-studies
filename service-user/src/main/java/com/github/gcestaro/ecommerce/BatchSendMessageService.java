package com.github.gcestaro.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class BatchSendMessageService {

  private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();
  private final Connection connection;

  BatchSendMessageService() throws SQLException {
    String url = "jdbc:sqlite:service-user/target/users_database.db";
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
    var batchSendMessageService = new BatchSendMessageService();

    try (var kafkaService = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
        "SEND_MESSAGE_TO_ALL_USERS", batchSendMessageService::parse, String.class, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, String> record) throws SQLException {
    String topicName = record.value();

    System.out.println("---------------------------------------------");
    System.out.println("Processing new batch");
    System.out.println("Topic: " + topicName);

    List<User> users = findAllUsers();

    for (User user : users) {
      userDispatcher.send(topicName, user.getUuid(), user);
    }
  }

  private List<User> findAllUsers() throws SQLException {
    var exists = connection.prepareStatement(
        "select uuid from Users");
    var results = exists.executeQuery();

    List<User> users = new ArrayList<>();
    while (results.next()) {
      String uuid = results.getString("uuid");
      users.add(new User(uuid));
    }

    return users;
  }
}
