package com.github.gcestaro.ecommerce;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

  @Override
  public JsonElement serialize(Message message, Type type,
      JsonSerializationContext jsonSerializationContext) {

    var jsonObject = new JsonObject();
    jsonObject.addProperty("type", message.getPayload().getClass().getName());
    jsonObject.add("payload", jsonSerializationContext.serialize(message.getPayload()));
    jsonObject.add("correlationId", jsonSerializationContext.serialize(message.getId()));
    return jsonObject;
  }

  @Override
  public Message deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext context) throws JsonParseException {

    var object = jsonElement.getAsJsonObject();
    var payloadType = object.get("type").getAsString();
    var correlationId = (CorrelationId) context.deserialize(object.get("correlationId"),
        CorrelationId.class);

    try {
      // maybe use an "accept list" for classes
      var payload = context.deserialize(object.get("payload"), Class.forName(payloadType));

      return new Message<>(correlationId, payload);
    } catch (ClassNotFoundException e) {
      // deal with this exception?
      throw new JsonParseException(e);
    }
  }
}
