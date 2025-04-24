package ru.practicum.api.handler;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.api.adapter.DurationAdapter;
import ru.practicum.api.adapter.LocalDateTimeAdapter;
import ru.practicum.api.serializer.TaskDeserializer;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager manager) {
        super(manager);
    }

    // проверим на соответствие ТЗ "ручек"
    @Override
    protected Integer checkURL(final String method, final String path) {
        String[] parts = path.split("/");

        switch (method) {
            case "GET":
                if (!(path.matches("/\\w+$") || path.matches("/\\w+/\\d+$")
                        || path.matches("/\\w+/\\d+/subtasks$")))
                    throw new BadRequestException("Некорректный запрос");

                if (path.matches("/\\w+/\\d+$") || path.matches("/\\w+/\\d+/subtasks$"))
                    return Integer.parseInt(parts[2]);

                break;
            default:
                return super.checkURL(method, path);

        }

        return null;
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
                text = gson.toJson(manager.getEpics());
            else if (!exchange.getRequestURI().getPath().endsWith("subtasks"))
                text = gson.toJson(manager.getEpic(taskId));
            else
                text = gson.toJson(manager.getEpic(taskId).getSubTasks());

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
                .registerTypeAdapter(Epic.class, new TaskDeserializer())
                .serializeNulls()
                .create();

        try (InputStream body = exchange.getRequestBody()) {
            String text = new String(body.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(text, Epic.class);
            manager.createEpic(epic);
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

        manager.deleteEpic(taskId);
        sendText(exchange, "");
    }

    @Override
    protected void patchHandler(HttpExchange exchange, Integer taskId) throws IOException {
        super.patchHandler(exchange, taskId);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Epic.class, new TaskDeserializer(taskId))
                .serializeNulls()
                .create();

        try (InputStream body = exchange.getRequestBody()) {
            String data = new String(body.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(data, Epic.class);
            manager.modifyEpic(epic);
            sendCreated(exchange);
        } catch (Exception e) {
            sendInternalError(exchange, gson.toJson(e.getMessage()));
        }

    }

    @Override
    public void checkId(int id) {
        if (manager.getEpic(id) == null)
            throw new NotFoundException("Элемент не найден");
    }
}
