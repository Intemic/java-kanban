package ru.practicum.api.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.api.server.HttpTaskServer;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskUrlsTest {
    private TaskManager manager;
    private HttpClient client;
    private URI url;
    private HttpRequest request;
    private final String path = "http://" + HttpTaskServer.HOST + ":" + HttpTaskServer.PORT + "/tasks";
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private HttpResponse<String> response;
    private Task task;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
        HttpTaskServer.start(manager);
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void afterEach() {
        HttpTaskServer.stop();
    }

    @Test
    public void checkCorrectOperationGet() {
        url = URI.create(path);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");

            JsonElement jsonElement = JsonParser.parseString(response.body());
            assertTrue(jsonElement.isJsonArray());
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            assertTrue(jsonArray.isEmpty());

            task = new Task("Тестовая задача", "Что то сделать",
                    LocalDateTime.now(), Duration.ofMinutes(30));
            manager.createTask(task);

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соотвествует успешному");
            jsonArray = (JsonParser.parseString(response.body())).getAsJsonArray();
            assertEquals(1, jsonArray.size(), "Пустой массив задач");

            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            assertEquals(task.getId(), jsonObject.get("id").getAsInt(), "Некорректный id задачи");
            assertEquals(task.getName(), jsonObject.get("name").getAsString(),
                    "Некорректное наименование задачи");
            assertEquals(task.getDescription(), jsonObject.get("description").getAsString(),
                    "Некорректное описание задачи");

            Task task2 = new Task("Тестовая задача № 2 ", "Что то сделать 2",
                    LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
            manager.createTask(task2);

            url = URI.create(path + "/" + task.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals(task.getId(), jsonObject.get("id").getAsInt(), "Некорректный id задачи");
            assertEquals(task.getName(), jsonObject.get("name").getAsString(),
                    "Некорректное наименование задачи");
            assertEquals(task.getDescription(), jsonObject.get("description").getAsString(),
                    "Некорректное описание задачи");

            url = URI.create(path + "/100");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код возврата не соответствует успешному");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkCorrectOperationPost() {
        JsonObject jsonObject = new JsonObject();
        String bodyString = "{\"name\": \"Тестовая задача 3\", \"description\": \"Что то сделать\"," +
                "\"status\": \"Новая\",\"startTime\": \"11.04.2025 19:03\"," +
                "\"duration\": \"PT30M\"}";
        byte[] body = bodyString.getBytes(StandardCharsets.UTF_8);

        url = URI.create(path);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .header("Accept", "application/json")
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(201, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals("Тестовая задача 3", manager.getTasks().get(0).getName(),
                    "Наименование задачи не соответствует");
            assertEquals("Что то сделать", manager.getTasks().get(0).getDescription(),
                    "Наименование задачи не соответствует");

            response = client.send(request, handler);
            assertEquals(406, response.statusCode(), "Код возврата не соответствует 406");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkCorrectOperationDelete() {
        task = new Task("Тестовая задача № 1 ", "Что то сделать 1",
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createTask(task);

        Task task2 = new Task("Тестовая задача № 2 ", "Что то сделать 2",
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
        manager.createTask(task2);

        url = URI.create(path + "/" + task2.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals(1, manager.getTasks().size(), "Кол-во элементов не сопадает");
            assertEquals(task.getId(), manager.getTasks().get(0).getId(), "Удален не правильный элемент");

            url = URI.create(path + "/1000");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код возврата не соответствует 404");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkCorrectOperationPatch() {
        task = new Task("Тестовая задача № 1 ", "Что то сделать 1",
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createTask(task);

        String bodyString = "{\"name\": \"Изменено\",\"description\": \"Измененная задача\"," +
                "\"status\": \"Новая\",\"startTime\": \"20.05.2025 19:03\"," +
                "\"duration\": \"PT30M\"}";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(bodyString);

        url = URI.create(path + "/" + task.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .method("PATCH", publisher)
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(201, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals("Изменено", manager.getTasks().get(0).getName(),
                    "Не изменено наименование задачи");
            assertEquals("Измененная задача", manager.getTasks().get(0).getDescription(),
                    "Не изменено описание задачи");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}