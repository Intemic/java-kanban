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
import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskUrlsTest {
    private TaskManager manager;
    private HttpClient client;
    private URI url;
    private HttpRequest request;
    private final String path = "http://" + HttpTaskServer.HOST + ":" + HttpTaskServer.PORT + "/subtasks";
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private HttpResponse<String> response;
    private SubTask subTask;
    private Epic epic;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
        epic = new Epic("Эпик № 1", "Описание эпика №1");
        manager.createEpic(epic);

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

            subTask = new SubTask("Тестовая задача", "Что то сделать", epic,
                    LocalDateTime.now(), Duration.ofMinutes(30));
            manager.createSubTask(subTask);

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соотвествует успешному");
            jsonArray = (JsonParser.parseString(response.body())).getAsJsonArray();
            assertEquals(1, jsonArray.size(), "Пустой массив подзадач");

            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            assertEquals(subTask.getId(), jsonObject.get("id").getAsInt(), "Некорректный id задачи");
            assertEquals(subTask.getName(), jsonObject.get("name").getAsString(),
                    "Некорректное наименование задачи");
            assertEquals(subTask.getDescription(), jsonObject.get("description").getAsString(),
                    "Некорректное описание задачи");

            SubTask subTask2 = new SubTask("Тестовая задача № 2 ", "Что то сделать 2", epic,
                    LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
            manager.createSubTask(subTask2);

            url = URI.create(path + "/" + subTask.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals(subTask.getId(), jsonObject.get("id").getAsInt(), "Некорректный id задачи");
            assertEquals(subTask.getName(), jsonObject.get("name").getAsString(),
                    "Некорректное наименование задачи");
            assertEquals(subTask.getDescription(), jsonObject.get("description").getAsString(),
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
        String bodyString = "{\"parentId\": " + epic.getId() + "," +
                "\"name\": \"Подзадача № 2\"," +
                "\"description\": \"Описание подзадачи № 2\"," +
                "\"status\": \"Новая\"," +
                "\"startTime\": \"22.04.2025 19:31\"," +
                "\"duration\": \"PT30M\"" +
                "}";
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
            assertEquals("Подзадача № 2", manager.getSubTasks().get(0).getName(),
                    "Наименование задачи не соответствует");
            assertEquals("Описание подзадачи № 2", manager.getSubTasks().get(0).getDescription(),
                    "Наименование задачи не соответствует");

            response = client.send(request, handler);
            assertEquals(406, response.statusCode(), "Код возврата не соответствует 406");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkCorrectOperationDelete() {
        subTask = new SubTask("Тестовая задача № 1 ", "Что то сделать 1", epic,
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createSubTask(subTask);

        SubTask subTask2 = new SubTask("Тестовая задача № 2 ", "Что то сделать 2", epic,
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
        manager.createSubTask(subTask2);

        url = URI.create(path + "/" + subTask2.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals(1, manager.getSubTasks().size(), "Кол-во элементов не сопадает");
            assertEquals(subTask.getId(), manager.getSubTasks().get(0).getId(), "Удален не правильный элемент");

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
        subTask = new SubTask("Тестовая задача № 1 ", "Что то сделать 1", epic,
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createSubTask(subTask);

        String bodyString = "{\"parentId\": " + epic.getId() + "," +
                "\"name\": \"Изменено\"," +
                "\"description\": \"Измененная задача\"," +
                "\"status\": \"Новая\"," +
                "\"startTime\": \"22.04.2025 19:31\"," +
                "\"duration\": \"PT30M\"" +
                "}";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(bodyString);

        url = URI.create(path + "/" + subTask.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .method("PATCH", publisher)
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(201, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals("Изменено", manager.getSubTasks().get(0).getName(),
                    "Не изменено наименование задачи");
            assertEquals("Измененная задача", manager.getSubTasks().get(0).getDescription(),
                    "Не изменено описание задачи");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}