package ru.practicum.task;

public enum Status {
    NEW("Новая"),
    IN_PROGRESS("В работе"),
    DONE("Выполнена");

    private String description;

    private Status(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
