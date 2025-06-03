package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Genre {
    private Long genreId;
    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}
