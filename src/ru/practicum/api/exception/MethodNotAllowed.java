package ru.practicum.api.exception;

public class MethodNotAllowed extends RuntimeException {
   public MethodNotAllowed(String message) {
       super(message);
   }
}
