package ru.practicum.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.exception.BadRequestException;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager manager;

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

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, null,  400);
    }

    protected void sendInternalError(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 500);
    }

    private void sendResponse(HttpExchange exchange, int code) throws IOException {
        sendResponse(exchange, null, code);
    }

    private void sendResponse(HttpExchange exchange, String text, int code) throws IOException {
        long length = 0;
        byte[] body = null;

        if (text != null) {
            body = text.getBytes(DEFAULT_CHARSET);
            length = body.length;
        }

        exchange.getResponseHeaders().set("Content-Type", "text/json; charset=utf-8");
        exchange.sendResponseHeaders(code, length);

        if (body != null) {
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(body);
            }
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
            sendBadRequest(exchange);
        }
    }
}