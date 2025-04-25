package ru.practicum.api.handler;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.api.adapter.DurationAdapter;
import ru.practicum.api.adapter.LocalDateTimeAdapter;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedHttpHandler extends BaseHttpHandler {
    public PrioritizedHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void getHandler(HttpExchange exchange, Integer taskId) throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .create();

        try {
            if (taskId != null)
                sendBadRequest(exchange);
            else
                sendText(exchange, gson.toJson(manager.getPrioritizedTasks()));
        } catch (Exception e) {
            sendInternalError(exchange, gson.toJson(e.getMessage()));
        }
    }
}
