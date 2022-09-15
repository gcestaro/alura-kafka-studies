package com.github.gcestaro.ecommerce;

import java.math.BigDecimal;

public class Order {

  private String userId;
  private String orderId;
  private BigDecimal value;

  public Order(String userId, String orderId, BigDecimal value) {
    this.userId = userId;
    this.orderId = orderId;
    this.value = value;
  }

  public BigDecimal getValue() {
    return value;
  }
}
