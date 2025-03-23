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

    public static Status deserilization(String data) {
        return switch (data) {
            case "Новая" -> Status.NEW;
            case "В работе" -> Status.IN_PROGRESS;
            case "Выполнена" -> Status.DONE;
            default -> null;
        };
    }
}
