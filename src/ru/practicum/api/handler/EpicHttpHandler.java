package ru.practicum.api.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.TaskManager;

import java.io.IOException;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void postHandler(HttpExchange exchange) throws IOException {

    }

    @Override
    public void checkId(int id) {

    }
}
