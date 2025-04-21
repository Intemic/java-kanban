package ru.practicum.server;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ru.practicum.task.Status;
import ru.practicum.task.Task;

class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.format(formatter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), formatter);
    }
}

class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null)
            jsonWriter.value("null");
        else
            jsonWriter.value(duration.toString());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();

        if (value.equals("null"))
            return null;
        else
            return Duration.parse(value);
    }
}

//class TaskSerialize implements JsonSerializer<Task> {
//
//    @Override
//    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
//        return null;
//    }
//}

/* так как нумерация идет автоматически при создании объекта,
нужно реализовать свой десериализатор
*/
class TaskDeserializer implements JsonDeserializer<Task> {

    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return new Task(jsonObject.get("name").getAsString(),
                jsonObject.get("description").getAsString(),
                jsonDeserializationContext.deserialize(jsonObject.get("startTime"), LocalDateTime.class),
                jsonDeserializationContext.deserialize(jsonObject.get("duration"), Duration.class));
    }
}


abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager manager;
    protected Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .serializeNulls()
            .create();

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 201);
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 406);
    }

    protected void sendBadRequest(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 400);
    }

    protected void sendInternalError(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 500);
    }

    private void sendResponse(HttpExchange exchange, int code) throws IOException {
        sendResponse(exchange, "", code);
    }

    private void sendResponse(HttpExchange exchange, String text, int code) throws IOException {
        byte[] body = text.getBytes(DEFAULT_CHARSET);

        exchange.getResponseHeaders().set("Content-Type", "text/json; charset=utf-8");
        exchange.sendResponseHeaders(code, body.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        }
    }

    // проверим на соответствие ТЗ "ручек"
    protected Integer checkURL(final String method, final String path) {
        String[] parts = path.split("/");

        switch (method) {
            case "GET":
                if (!(path.matches("/\\w+$") || path.matches("/\\w+/\\d+$")))
                    throw new BadRequestException("Некорректный запрос");

                if (path.matches("/\\w+/\\d+$"))
                    return Integer.parseInt(parts[2]);

                break;
            case "POST":
                if (parts.length != 2)
                    throw new BadRequestException("Некорректный запрос");
                break;
            case "DELETE":
            case "PATCH":
                if (!path.matches("/\\w+/\\d+$"))
                    throw new BadRequestException("Некорректный запрос");

                return Integer.parseInt(parts[2]);

            default:
                throw new BadRequestException("Некорректный запрос");
        }

        return null;
    }

    protected abstract void getHandler(HttpExchange exchange, Integer taskId) throws IOException;

    protected abstract void postHandler(HttpExchange exchange) throws IOException;

    protected abstract void deleteHandler(HttpExchange exchange, Integer taskId) throws IOException;

    protected abstract void patchHandler(HttpExchange exchange, Integer taskId) throws IOException;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Integer taskId = checkURL(exchange.getRequestMethod(), exchange.getRequestURI().getPath());

            switch (exchange.getRequestMethod()) {
                case "GET":
                    getHandler(exchange, taskId);
                    break;
                case "POST":
                    postHandler(exchange);
                    break;
                case "DELETE":
                    deleteHandler(exchange, taskId);
                    break;
            /* так как реализация не поддерживает создание элементов с пустым ID добавил, а в ТЗ есть
               требование обновления данных то добавил реализацию еще одного метода
             */
                case "PATCH":
                    patchHandler(exchange, taskId);
                    break;
            }

        } catch (BadRequestException e) {
            // TODO: реализовать обработку
            sendBadRequest(exchange, "");
        }
    }
}

class TaskHttpHandler extends BaseHttpHandler {
    public TaskHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void getHandler(HttpExchange exchange, Integer taskId) throws IOException {
        String text = null;

        try {
            if (taskId == null) {
                List<Task> list = manager.getTasks();
                text = gson.toJson(list);
            } else
                text = gson.toJson(manager.getTask(taskId));

            sendText(exchange, text);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "");
        } catch (Exception e) {
            sendInternalError(exchange, gson.toJson(e.getMessage()));
        }
    }

    @Override
    protected void postHandler(HttpExchange exchange) throws IOException {
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

    }

    @Override
    protected void patchHandler(HttpExchange exchange, Integer taskId) throws IOException {

    }
}

public class HttpTaskServer {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Тестовая задача", "Что то сделать");
        manager.createTask(task);


        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TaskHttpHandler(manager));
//            server.createContext("/subtasks", );
//            server.createContext("/epics", );
//            server.createContext("/history", );
//            server.createContext("/prioritized", );
            server.start();

        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }
}
