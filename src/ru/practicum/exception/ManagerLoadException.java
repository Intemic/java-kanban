package ru.practicum.exception;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException() {
    }

    public ManagerLoadException(final String message) {
        super(message);
    }
}
