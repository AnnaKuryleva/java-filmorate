package ru.yandex.practicum.filmorate.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GenreNotFoundException extends NotFoundException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}