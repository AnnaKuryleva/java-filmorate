package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Mpa {
    @JsonProperty("id")
    private Integer ratingId;
    @NotBlank(message = "Название рейтинга MPA не может быть пустым")
    private String name;
}
