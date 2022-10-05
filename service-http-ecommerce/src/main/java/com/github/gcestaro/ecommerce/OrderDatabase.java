package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.database.LocalDatabase;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class OrderDatabase implements AutoCloseable {

  private static final String SQL_CREATE_TABLE_ORDERS = "create table "
      + "Orders ("
      + "uuid varchar(200) primary key"
      + " )";

  private static final String SQL_EXISTS_ORDER = "select uuid from Orders where uuid = ? limit 1";

  private final LocalDatabase database;

  public OrderDatabase() throws SQLException {
    database = new LocalDatabase("orders_database");
    database.createIfNotExists(SQL_CREATE_TABLE_ORDERS);
  }

  public boolean saveNew(Order order) throws SQLException {
    if (wasProcessed(order)) {
      return false;
    }

    database.insert("insert into Orders (uuid) values (?)",
        Map.of(1, order.getOrderId()));
    return true;
  }

  private boolean wasProcessed(Order order) throws SQLException {
    return database.exists(SQL_EXISTS_ORDER, Map.of(1, order.getOrderId()));
  }

  @Override
  public void close() throws IOException {
    database.close();
  }
}
