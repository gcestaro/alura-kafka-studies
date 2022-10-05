package com.github.gcestaro.ecommerce.consumer;

import java.util.Map;
import java.util.concurrent.Callable;

public class ServiceProvider<T> implements Callable<Void> {

  private final ServiceFactory<T> factory;

  public ServiceProvider(ServiceFactory<T> factory) {
    this.factory = factory;
  }

  @Override
  public Void call() throws Exception {
    var service = factory.create();

    try (var kafkaService = new KafkaService<>(service.getConsumerGroup(),
        service.getTopic(), service::parse, Map.of())) {
      kafkaService.run();
    }
    return null;
  }
}
