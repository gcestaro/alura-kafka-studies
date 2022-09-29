package com.github.gcestaro.ecommerce.consumer;

import java.util.concurrent.Executors;

public class ServiceRunner<T> {

  private final ServiceProvider<T> provider;

  public ServiceRunner(ServiceFactory<T> factory) {
    this.provider = new ServiceProvider<>(factory);
  }

  public void start(int threadsAmount) {
    var pool = Executors.newFixedThreadPool(threadsAmount);

    for (int i = 0; i < threadsAmount; i++) {
      pool.submit(provider);
    }
  }
}
