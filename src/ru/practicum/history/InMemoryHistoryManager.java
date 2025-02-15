package ru.practicum.history;

import ru.practicum.task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList<Task> history = new LinkedList<>();
    private ConfigHistoryManager config;

    public InMemoryHistoryManager(ConfigHistoryManager config) {
        this.config = config;
    }

    @Override
    public void add(Task task) {
        if (task == null)
            return;

        if (config.getMaxSize() != 0 && history.size() == config.getMaxSize()) {
            history.removeFirst();
        }

        if (config.isNotDublicate() && history.contains(task))
            return;

        history.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
