package ru.practicum.api.server;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.api.handler.TaskHttpHandler;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import ru.practicum.task.Task;

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
