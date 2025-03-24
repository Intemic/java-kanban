package ru.practicum.exception;

public class DeserilizationException extends RuntimeException {
    public DeserilizationException() {
    }

    public DeserilizationException(final String message) {
        super(message);
    }
}
