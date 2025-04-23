package ru.practicum.api.server;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.api.handler.*;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import ru.practicum.task.Epic;
import ru.practicum.task.SubTask;
import ru.practicum.task.Task;

public class HttpTaskServer {
    private static HttpServer server;

    public static void start(TaskManager manager) {
//        Task task = new Task("Тестовая задача", "Что то сделать",
//                LocalDateTime.now(), Duration.ofMinutes(30));
//        manager.createTask(task);
//
//        Epic epic = new Epic("Эпик № 1", "Описание эпика № 1");
//        manager.createEpic(epic);
//
//        SubTask subTask = new SubTask("Подзадача № 1", "Описание подзадачи № 1", epic);
//        manager.createSubTask(subTask);
//
//        subTask = new SubTask("Подзадача № 2", "Описание подзадачи № 2", epic);
//        manager.createSubTask(subTask);

        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TaskHttpHandler(manager));
            server.createContext("/subtasks", new SubTaskHttpHandler(manager));
            server.createContext("/epics", new EpicHttpHandler(manager));
            server.createContext("/history", new HistoryHttpHandler(manager));
            server.createContext("/prioritized", new PrioritizedHttpHandler(manager));
            server.start();

        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    public static void main(String[] args) {
        start(Managers.getDefault());
    }
}
