package ru.practicum.manager;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String filename;

    public FileBackedTaskManager(final String filename) {
        this.filename = filename;
    }
}
