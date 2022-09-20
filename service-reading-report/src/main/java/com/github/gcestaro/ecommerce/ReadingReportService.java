package com.github.gcestaro.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ReadingReportService {

  private static final Path path = new File("src/main/resources/report.txt").toPath();


  public static void main(String[] args) {
    var readingReportService = new ReadingReportService();

    try (var kafkaService = new KafkaService<>(ReadingReportService.class.getSimpleName(),
        "USER_GENERATE_READING_REPORT", readingReportService::parse, User.class, Map.of())) {
      kafkaService.run();
    }
  }

  private void parse(ConsumerRecord<String, User> record) throws IOException {
    User user = record.value();

    System.out.println("---------------------------------------------");
    System.out.println("Generating reading report for user " + user);

    File target = new File(user.getReportPath());
    IO.copyTo(path, target);
    IO.append(target, "Created for " + user.getUuid());

    System.out.println("File created: " + target.getAbsolutePath());
  }
}
