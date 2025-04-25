package ru.practicum.api.handler;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.api.adapter.DurationAdapter;
import ru.practicum.api.adapter.LocalDateTimeAdapter;
import ru.practicum.api.serializer.TaskDeserializer;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskHttpHandler extends BaseHttpHandler {

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
            if (taskId == null)
                text = gson.toJson(manager.getTasks());
            else
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
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .serializeNulls()
                .create();

        try (InputStream body = exchange.getRequestBody()) {
            String text = new String(body.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(text, Task.class);
            manager.createTask(task);
            sendCreated(exchange);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendInternalError(exchange, gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void deleteHandler(HttpExchange exchange, Integer taskId) throws IOException {
        super.deleteHandler(exchange, taskId);

        manager.deleteTask(taskId);
        sendText(exchange, "");
    }

    @Override
    protected void patchHandler(HttpExchange exchange, Integer taskId) throws IOException {
        super.patchHandler(exchange, taskId);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new TaskDeserializer(taskId))
                .serializeNulls()
                .create();

        try (InputStream body = exchange.getRequestBody()) {
            String data = new String(body.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(data, Task.class);
            manager.modifyTask(task);
            sendCreated(exchange);
        } catch (Exception e) {
            sendInternalError(exchange, gson.toJson(e.getMessage()));
        }

    }

    @Override
    public void checkId(int id) {
        if (manager.getTask(id) == null)
            throw new NotFoundException("Элемент не найден");
    }
}