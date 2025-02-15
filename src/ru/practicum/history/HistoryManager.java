package ru.practicum.history;

import ru.practicum.task.Task;

import java.util.LinkedList;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}
