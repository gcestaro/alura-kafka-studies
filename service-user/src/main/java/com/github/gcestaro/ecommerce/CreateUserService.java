package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.consumer.ConsumerService;
import com.github.gcestaro.ecommerce.consumer.ServiceRunner;
import com.github.gcestaro.ecommerce.database.LocalDatabase;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class CreateUserService implements ConsumerService<Order> {

  private static final String SQL_CREATE_TABLE_USERS = "create table "
      + "Users ("
      + "uuid varchar(200) primary key, "
      + "email varchar(200)"
      + " )";
  private static final String SQL_INSERT_USER = "insert into Users (uuid, email) values (?, ?)";
  private static final String SQL_EXISTS_USER = "select uuid from Users where email = ? limit 1";

  private final LocalDatabase database;

  public static void main(String[] args) {
    new ServiceRunner<>(CreateUserService::new).start(1);
  }

  public CreateUserService() throws SQLException {
    database = new LocalDatabase("users_database");
    database.createIfNotExists(SQL_CREATE_TABLE_USERS);
  }

  @Override
  public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
    var message = record.value();
    var order = message.getPayload();

    System.out.println("---------------------------------------------");
    System.out.println("Processing new order, checking for new user");
    System.out.println(order);

    if (isNewUser(order.getEmail())) {
      insertNewUser(order.getEmail());
    }
  }

  @Override
  public String getTopic() {
    return "ECOMMERCE_NEW_ORDER";
  }

  @Override
  public String getConsumerGroup() {
    return CreateUserService.class.getSimpleName();
  }

  private void insertNewUser(String email) throws SQLException {
    var userId = UUID.randomUUID().toString();

    database.insert(SQL_INSERT_USER, Map.of(1, userId, 2, email));

    System.out.println("User " + userId + " , email " + email + " added.");
  }

  private boolean isNewUser(String email) throws SQLException {
    return database.exists(SQL_EXISTS_USER, Map.of(1, email));
  }
}
