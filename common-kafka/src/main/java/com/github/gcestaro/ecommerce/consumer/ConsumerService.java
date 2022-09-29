package com.github.gcestaro.ecommerce.consumer;

import com.github.gcestaro.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {

  void parse(ConsumerRecord<String, Message<T>> record) throws Exception;

  String getTopic();

  String getConsumerGroup();
}
