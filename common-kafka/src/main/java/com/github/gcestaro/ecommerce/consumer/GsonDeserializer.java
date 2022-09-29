package com.github.gcestaro.ecommerce.consumer;

import com.github.gcestaro.ecommerce.Message;
import com.github.gcestaro.ecommerce.MessageAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

public class GsonDeserializer implements Deserializer<Message> {

  private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Message.class, new MessageAdapter())
      .create();

  @Override
  public Message deserialize(String topic, byte[] data) {
    return gson.fromJson(new String(data), Message.class);
  }
}
