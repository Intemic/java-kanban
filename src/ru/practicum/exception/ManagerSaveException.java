package ru.practicum.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {
    }

    public ManagerSaveException(final String message) {
        super(message);
    }
}
