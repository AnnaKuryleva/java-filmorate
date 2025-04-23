package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение для ненайденных ресурсов.
 */

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}