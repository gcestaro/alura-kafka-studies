package com.github.gcestaro.ecommerce.consumer;

public interface ServiceFactory<T> {

  ConsumerService<T> create();
}
