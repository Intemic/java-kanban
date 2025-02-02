package ru.practucum.task;

public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;

    @Override
    public String toString() {
        switch (this) {
            case NEW:
                return "Новая";
            case IN_PROGRESS:
                return "В работе";
            case DONE:
                return "Выполнена";
            default:
                return "Не известен";
        }
    }
}
