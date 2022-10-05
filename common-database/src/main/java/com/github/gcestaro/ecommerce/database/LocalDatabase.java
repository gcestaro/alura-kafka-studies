package com.github.gcestaro.ecommerce.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class LocalDatabase {

  private final Connection connection;

  public LocalDatabase(String name) throws SQLException {
    var url = "jdbc:sqlite:target/" + name + ".db";
    connection = DriverManager.getConnection(url);
  }

  public void createIfNotExists(String sql) {
    try {
      connection.createStatement().execute(sql);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public void insert(String sql, Map<Integer, Object> values) throws SQLException {
    try (var statement = createStatement(sql, values)) {
      statement.execute();
    }
  }

  public boolean exists(String sql, Map<Integer, Object> params) throws SQLException {
    try (var statement = createStatement(sql, params)) {

      var results = statement.executeQuery();
      return !results.next();
    }
  }

  private PreparedStatement createStatement(String sql, Map<Integer, Object> params)
      throws SQLException {
    var statement = connection.prepareStatement(sql);

    for (var entry : params.entrySet()) {
      statement.setObject(entry.getKey(), entry.getValue());
    }

    return statement;
  }
}
