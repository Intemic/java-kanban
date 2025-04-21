package ru.practicum.api.serializer;

import com.google.gson.*;
import ru.practicum.task.Task;

import java.lang.reflect.Type;


/* так как нумерация идет автоматически при создании объекта,
нужно реализовать свой десериализатор что бы корректно отработала нумерация
*/
public class TaskChangeDeserializer implements JsonDeserializer<Task> {
     @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Task.deserilizationFromJSon(jsonElement, type, jsonDeserializationContext);
    }
}
