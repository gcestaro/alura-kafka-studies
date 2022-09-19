package com.github.gcestaro.ecommerce;

import java.math.BigDecimal;

public class Order {

  private String orderId;
  private BigDecimal value;
  private String email;

  public Order(String orderId, BigDecimal value, String email) {
    this.orderId = orderId;
    this.value = value;
    this.email = email;
  }

  public BigDecimal getValue() {
    return value;
  }


  @Override
  public String toString() {
    return "Order{" +
        "orderId='" + orderId + '\'' +
        ", value=" + value +
        '}';
  }

  public String getEmail() {
    return email;
  }
}
