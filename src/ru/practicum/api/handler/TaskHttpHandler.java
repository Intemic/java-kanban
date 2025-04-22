package ru.practicum.api.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.api.adapter.DurationAdapter;
import ru.practicum.api.adapter.LocalDateTimeAdapter;
import ru.practicum.api.serializer.TaskCreateDeserializer;
import ru.practicum.api.strategy.TaskCreateExclusionStrategy;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHttpHandler extends BaseHttpHandler {
    private Gson gson;
//            new GsonBuilder()
//            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
//            .registerTypeAdapter(Duration.class, new DurationAdapter())
//            .serializeNulls()
//            .create();

    public TaskHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void getHandler(HttpExchange exchange, Integer taskId) throws IOException {
        super.getHandler(exchange, taskId);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .create();

        String text = null;

        try {
            if (taskId == null) {
                List<Task> list = manager.getTasks();
                text = gson.toJson(list);
            } else
                text = gson.toJson(manager.getTask(taskId));

            sendText(exchange, text);
        } catch (Exception e) {
            sendInternalError(exchange, gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void postHandler(HttpExchange exchange) throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskCreateDeserializer())
              //  .setExclusionStrategies(new TaskCreateExclusionStrategy())
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .create();

        try (InputStream body = exchange.getRequestBody()) {
            String text = new String(body.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(text, Task.class);
            manager.createTask(task);
            sendCreated(exchange);
        } catch (Exception e) {
            sendInternalError(exchange, gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void deleteHandler(HttpExchange exchange, Integer taskId) throws IOException {
        super.deleteHandler(exchange, taskId);

        manager.deleteTask(taskId);
    }

    @Override
    protected void patchHandler(HttpExchange exchange, Integer taskId) throws IOException {
        super.patchHandler(exchange, taskId);

        try (InputStream body = exchange.getRequestBody()) {
          String data = new String(body.readAllBytes(), DEFAULT_CHARSET);
          Task task = gson.fromJson(data, Task.class);
          manager.modifyTask(task);
        }

    }

    @Override
    public void checkId(int id) {
        if (manager.getTask(id) == null)
            throw new NotFoundException("Элемент не найден");
    }
}