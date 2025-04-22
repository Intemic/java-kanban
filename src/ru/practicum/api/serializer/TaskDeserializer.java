package ru.practicum.api.serializer;

import com.google.gson.*;
import ru.practicum.task.Task;

import java.lang.reflect.Type;

public class TaskDeserializer implements JsonDeserializer<Task> {
    private final Integer taskId;

    public TaskDeserializer() {
        this.taskId = null;
    }


    public TaskDeserializer(Integer taskId) {
        this.taskId = taskId;
    }

    @Override
    public Task deserialize(JsonElement jsonElement, Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        /*
            по идее формат данных должен быть, так как его настроить не нашел, будем считать что приходит в таком виде
            {
                "name": "",
                "description": "",
                "status": "",
                "startTime": "",
                "duration": ""
            }
         */

        JsonObject object = jsonElement.getAsJsonObject();

        if (taskId != null) {
            object = jsonElement.deepCopy().getAsJsonObject();
            object.add("id", new JsonPrimitive(taskId));
        }

        return Task.deserilizationFromJSon(object, type, jsonDeserializationContext);
    }
}