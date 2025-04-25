package ru.practicum.api.server;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.api.handler.*;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static HttpServer server;
    public static final String HOST = "localhost";
    public static final int PORT = 8080;

    public static void start(TaskManager manager) {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
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
