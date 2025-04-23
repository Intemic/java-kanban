package ru.practicum.api;

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
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskUrlsTest {
    private TaskManager manager;
    private HttpClient client;
    private URI url;
    private HttpRequest request;
    private final String path = "http://localhost:8080";
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
    public void checkCorrectOperationGet(){
        url = URI.create(path + "/tasks");
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
            assertEquals(task.getId(), jsonObject.get("id").getAsInt(), "Некорректный id задачи" );
            assertEquals(task.getName(), jsonObject.get("name").getAsString(),
                    "Некорректное наименование задачи" );
            assertEquals(task.getDescription(), jsonObject.get("description").getAsString(),
                    "Некорректное описание задачи" );

            Task task2 = new Task("Тестовая задача № 2 ", "Что то сделать 2",
                    LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
            manager.createTask(task2);

            url = URI.create(path + "/tasks/1");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, handler);
            assertEquals(200, response.statusCode(), "Код возврата не соответствует успешному");
            assertEquals(task.getId(), jsonObject.get("id").getAsInt(), "Некорректный id задачи" );
            assertEquals(task.getName(), jsonObject.get("name").getAsString(),
                    "Некорректное наименование задачи" );
            assertEquals(task.getDescription(), jsonObject.get("description").getAsString(),
                    "Некорректное описание задачи" );


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}