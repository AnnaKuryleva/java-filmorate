package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.MinReleaseDate;
import ru.yandex.practicum.filmorate.validation.ValidDate;

/**
 * Film.
 */

@Data
public class Film {
    Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    String name;
    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(max = 200, message = "Описание фильма должно включать не более 200 символов")
    String description;
    @NotNull(message = "Дата релиза не может быть null")
    @ValidDate(message = "Дата релиза должна быть в формате yyyy-MM-dd")
    @MinReleaseDate
    String releaseDate;
    @NotNull(message = "Продолжительность фильма не может быть null")
    @Positive(message = "продолжительность фильма не может быть отрицательной или равной нулю")
    long duration;
}
