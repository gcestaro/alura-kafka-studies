package com.github.gcestaro.ecommerce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;

public class GsonDeserializer<T> implements Deserializer<T> {

  public static final String TYPE_CONFIG = "";
  private final Gson gson = new GsonBuilder().create();

  private Class<T> clazz;

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    String typeName = String.valueOf(configs.get(TYPE_CONFIG));
    try {
      clazz = (Class<T>) Class.forName(typeName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Type for deserialization does not exist on classpath", e);
    } catch (ClassCastException e) {
      throw new RuntimeException("Type for deserialization is different than expected Class<T>", e);
    }
  }

  @Override
  public T deserialize(String topic, byte[] data) {
    return gson.fromJson(new String(data), clazz);
  }
}
