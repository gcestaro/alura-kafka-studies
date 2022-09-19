package com.github.gcestaro.ecommerce;

import java.math.BigDecimal;

public class Order {

  private String orderId;

  private String email;
  private BigDecimal value;

  public Order(String orderId, BigDecimal value, String email) {
    this.orderId = orderId;
    this.value = value;
    this.email = email;
  }

  @Override
  public String toString() {
    return "Order{" +
        "orderId='" + orderId + '\'' +
        ", email='" + email + '\'' +
        ", value=" + value +
        '}';
  }

  public String getEmail() {
    return email;
  }
}
