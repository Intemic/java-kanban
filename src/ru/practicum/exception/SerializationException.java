package ru.practicum.exception;

public class SerializationException extends RuntimeException {
    public SerializationException() {
    }

    public SerializationException(final String message) {
        super(message);
    }
}
