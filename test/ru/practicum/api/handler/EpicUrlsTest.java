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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicUrlsTest {
    private TaskManager manager;
    private HttpClient client;
    private URI url;
    private HttpRequest request;
    private final String path = "http://" + HttpTaskServer.HOST + ":" + HttpTaskServer.PORT + "/epics";
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private HttpResponse<String> response;
    private Epic epic;

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

            epic = new Epic("Эпик № 1", "Описание эпика № 1");
            manager.createEpic(epic);

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соотвествует успешному");
            jsonArray = (JsonParser.parseString(response.body())).getAsJsonArray();
            assertEquals(1, jsonArray.size(), "Пустой массив задач");

            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            assertEquals(epic.getId(), jsonObject.get("id").getAsInt(), "Некорректный id задачи");
            assertEquals(epic.getName(), jsonObject.get("name").getAsString(),
                    "Некорректное наименование задачи");
            assertEquals(epic.getDescription(), jsonObject.get("description").getAsString(),
                    "Некорректное описание задачи");

            Epic epic2 = new Epic("Эпик № 2", "Описание эпика № 2");
            manager.createEpic(epic2);

            url = URI.create(path + "/" + epic.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals(epic.getId(), jsonObject.get("id").getAsInt(), "Некорректный id задачи");
            assertEquals(epic.getName(), jsonObject.get("name").getAsString(),
                    "Некорректное наименование задачи");
            assertEquals(epic.getDescription(), jsonObject.get("description").getAsString(),
                    "Некорректное описание задачи");

            url = URI.create(path + "/100");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, handler);
            assertEquals(404, response.statusCode(), "Код возврата не соответствует успешному");

            SubTask subTask = new SubTask("Подзадача №1", "Описание поздачачи № 1", epic2,
                    LocalDateTime.now(), Duration.ofMinutes(10));
            manager.createSubTask(subTask);
            subTask = new SubTask("Подзадача №2", "Описание поздачачи № 2", epic2,
                    LocalDateTime.now().plusDays(1), Duration.ofMinutes(10));
            manager.createSubTask(subTask);
            subTask = new SubTask("Подзадача №3", "Описание поздачачи № 3", epic2,
                    LocalDateTime.now().plusDays(2), Duration.ofMinutes(10));
            manager.createSubTask(subTask);

            url = URI.create(path + "/" + epic2.getId() + "/subtasks");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            jsonArray = (JsonParser.parseString(response.body())).getAsJsonArray();
            assertEquals(3, jsonArray.size(), "Пустой массив подзадач");

            List<SubTask> subTaskList = manager.getEpic(epic2.getId()).getSubTasks();
            for (int i = 0; i < jsonArray.size(); i++) {
                assertEquals(subTaskList.get(i).getId(),
                        jsonArray.get(i).getAsJsonObject().get("id").getAsInt(),
                        "Не соответствие элементов в результате");
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkCorrectOperationPost() {
        JsonObject jsonObject = new JsonObject();
        String bodyString = "{\"name\": \"Эпик № 2\"," +
                "\"description\": \"Описание эпика № 2\"," +
                "\"status\": \"NEW\"," +
                "\"startTime\": \"23.06.2025 08:08\"," +
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
            assertEquals("Эпик № 2", manager.getEpics().get(0).getName(),
                    "Наименование задачи не соответствует");
            assertEquals("Описание эпика № 2", manager.getEpics().get(0).getDescription(),
                    "Наименование задачи не соответствует");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkCorrectOperationDelete() {
        epic = new Epic("Эпик № 1", "Описание эпика № 1");
        manager.createEpic(epic);
        Epic epic2 = new Epic("Эпик № 2", "Описание эпика № 2");
        manager.createEpic(epic2);

        url = URI.create(path + "/" + epic2.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals(1, manager.getEpics().size(), "Кол-во элементов не сопадает");
            assertEquals(epic.getId(), manager.getEpics().get(0).getId(), "Удален не правильный элемент");

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
        epic = new Epic("Эпик № 1", "Описание эпика № 1");
        manager.createEpic(epic);

        String bodyString = "{\"name\": \"Эпик № 2\"," +
                "\"description\": \"Описание эпика № 2\"," +
                "\"startTime\": \"23.06.2025 08:08\"," +
                "\"duration\": \"PT30M\"}";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(bodyString);

        url = URI.create(path + "/" + epic.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .method("PATCH", publisher)
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(201, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals("Эпик № 2", manager.getEpics().get(0).getName(),
                    "Не изменено наименование задачи");
            assertEquals("Описание эпика № 2", manager.getEpics().get(0).getDescription(),
                    "Не изменено описание задачи");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}