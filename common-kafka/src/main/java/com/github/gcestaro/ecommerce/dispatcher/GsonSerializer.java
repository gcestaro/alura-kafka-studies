package com.github.gcestaro.ecommerce.dispatcher;

import com.github.gcestaro.ecommerce.Message;
import com.github.gcestaro.ecommerce.MessageAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serializer;

public class GsonSerializer<T> implements Serializer<T> {

  private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Message.class, new MessageAdapter())
      .create();

  @Override
  public byte[] serialize(String topic, T data) {
    return gson.toJson(data).getBytes();
  }
}
