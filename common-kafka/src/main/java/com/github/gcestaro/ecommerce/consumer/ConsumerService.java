package com.github.gcestaro.ecommerce.consumer;

import com.github.gcestaro.ecommerce.Message;
import java.io.IOException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {

  void parse(ConsumerRecord<String, Message<T>> record) throws IOException;

  String getTopic();

  String getConsumerGroup();
}
