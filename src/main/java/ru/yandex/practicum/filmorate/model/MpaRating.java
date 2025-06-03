package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MpaRating {
    private Integer ratingId;
    @NotBlank(message = "Название рейтинга MPA не может быть пустым")
    private String name;
}
