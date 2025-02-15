package ru.practicum.manager;

import ru.practicum.history.ConfigHistoryManager;
import ru.practicum.history.HistoryManager;
import ru.practicum.history.InMemoryHistoryManager;

public class Managers {
   private static final int MAX_RECORD_IN_HISTORY = 10;

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory( ) {
        return new InMemoryHistoryManager(new ConfigHistoryManager(MAX_RECORD_IN_HISTORY));
    }
}