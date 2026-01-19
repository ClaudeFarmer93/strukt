package org.example.backend.exception;

public class HabitAlreadyExistsException extends RuntimeException {
    public HabitAlreadyExistsException(String message) {
        super(message);
    }
}
