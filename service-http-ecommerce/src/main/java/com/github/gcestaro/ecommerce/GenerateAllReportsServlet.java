package com.github.gcestaro.ecommerce;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GenerateAllReportsServlet extends HttpServlet {

  private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

  @Override
  public void destroy() {
    super.destroy();
    batchDispatcher.close();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    batchDispatcher.send("SEND_MESSAGE_TO_ALL_USERS", "USER_GENERATE_READING_REPORT",
        "USER_GENERATE_READING_REPORT");

    System.out.println("Sent generate report to all users");
    response.setStatus(HttpServletResponse.SC_OK);
    try {
      response.getWriter().println("Report request generated");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
