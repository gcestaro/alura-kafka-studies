package com.github.gcestaro.ecommerce;

import com.github.gcestaro.ecommerce.consumer.KafkaService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ReadingReportService {

  private static final Path path = new File("src/main/resources/report.txt").toPath();


  public static void main(String[] args) throws ExecutionException, InterruptedException {
    var readingReportService = new ReadingReportService();

    try (var kafkaService = new KafkaService<>(ReadingReportService.class.getSimpleName(),
        "ECOMMERCE_USER_GENERATE_READING_REPORT", readingReportService::parse, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
    var message = record.value();
    var user = message.getPayload();

    System.out.println("---------------------------------------------");
    System.out.println("Generating reading report for user " + user);

    var target = new File(user.getReportPath());
    IO.copyTo(path, target);
    IO.append(target, "Created for " + user.getUuid());

    System.out.println("File created: " + target.getAbsolutePath());
  }
}
