package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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

    var email = request.getParameter("email");
    var amount = new BigDecimal(request.getParameter("amount"));
    var orderId = UUID.randomUUID().toString();
    var order = new Order(orderId, amount, email);

    try {
      orderDispatcher.send("ECOMMERCE_NEW_ORDER", email,
          new CorrelationId(
              NewOrderServlet.class.getSimpleName()), order);

      var message = "New order process executed successfully";
      System.out.println(message);

      response.getWriter().println(message);
      response.setStatus(HttpServletResponse.SC_OK);

    } catch (ExecutionException | InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
