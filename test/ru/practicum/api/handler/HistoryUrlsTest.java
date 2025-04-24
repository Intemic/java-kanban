package ru.practicum.api.handler;

import com.google.gson.JsonArray;
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
import ru.practicum.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryUrlsTest {
    private TaskManager manager;
    private HttpClient client;
    private URI url;
    private HttpRequest request;
    private final String path = "http://" + HttpTaskServer.HOST + ":" + HttpTaskServer.PORT + "/history";
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private HttpResponse<String> response;
    private SubTask subTask;
    private Epic epic;
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
        task = new Task("Тестовая задача", "Что то сделать",
                LocalDateTime.now(), Duration.ofMinutes(30));
        epic = new Epic("Эпик № 1", "Описание эпика №1");
        subTask = new SubTask("Тестовая задача", "Что то сделать", epic,
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubTask(subTask);

        manager.getEpic(epic.getId());
        manager.getSubTask(subTask.getId());
        manager.getTask(task.getId());

        url = URI.create(path);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Accept", "application/json")
                .build();
        try {
            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            JsonArray jsonArray = (JsonParser.parseString(response.body())).getAsJsonArray();
            assertEquals(3, jsonArray.size(), "Некорректное кол-во элементов в ответе");

            List<Task> history = manager.getHistory();
            for (int i = 0; i < jsonArray.size(); i++) {
                assertEquals(history.get(i).getId(),
                        jsonArray.get(i).getAsJsonObject().get("id").getAsInt(),
                        "Не соответствие элементов в результате");
            }

            manager.deleteAllSubTasks();
            manager.deleteAllEpics();
            manager.deleteAllTasks();

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            jsonArray = (JsonParser.parseString(response.body())).getAsJsonArray();
            assertEquals(0, jsonArray.size(), "Некорректное кол-во элементов в ответе");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void checkCorrectOperationPost() {
        JsonObject jsonObject = new JsonObject();
        String bodyString = "";
        byte[] body = bodyString.getBytes(StandardCharsets.UTF_8);

        url = URI.create(path);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .header("Accept", "application/json")
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(405, response.statusCode(), "Код возврата не соответствует 400");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkCorrectOperationDelete() {
        url = URI.create(path + "/1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(405, response.statusCode(), "Код возврата не соответствует 400");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkCorrectOperationPatch() {
        String bodyString = "";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(bodyString);

        url = URI.create(path + "/1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .method("PATCH", publisher)
                .build();

        try {
            response = client.send(request, handler);
            assertEquals(405, response.statusCode(), "Код возврата не соответствует 400");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}