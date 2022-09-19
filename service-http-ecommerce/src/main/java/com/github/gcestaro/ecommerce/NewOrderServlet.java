package com.github.gcestaro.ecommerce;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class NewOrderServlet extends HttpServlet {

  private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
  private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

  @Override
  public void destroy() {
    super.destroy();
    orderDispatcher.close();
    emailDispatcher.close();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    String email = request.getParameter("email");
    BigDecimal amount = new BigDecimal(request.getParameter("amount"));

    var orderId = UUID.randomUUID().toString();
    var order = new Order(orderId, amount, email);

    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

    var emailMesssage = "Thank you for your order! We are processing your request.";
    var emailCode = new Email("test@test.com", emailMesssage);
    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

    String message = "New order process executed successfully";
    System.out.println(message);
    try {
      response.getWriter().println(message);
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
