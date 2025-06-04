package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Genre {
    @JsonProperty("id")
    private Long genreId;
    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}
