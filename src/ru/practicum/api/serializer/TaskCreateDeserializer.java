package ru.practicum.api.serializer;

import com.google.gson.*;
import ru.practicum.task.Task;

import java.lang.reflect.Type;

public class TaskCreateDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        /* при создании не должно быть элементов id, status, удалим их
            скорее всего есть проще способ развести перечень полей для создания/изменения,
            но не нашел другого
        */
        JsonObject object = jsonElement.deepCopy().getAsJsonObject();
        object.remove("id");
        object.remove("status");


        return Task.deserilizationFromJSon(object, type, jsonDeserializationContext);
    }
}