package ru.yandex.app.exceptions;

public class TaskAddingException extends RuntimeException {
    public TaskAddingException(String message) {
        super(message);
    }
}
