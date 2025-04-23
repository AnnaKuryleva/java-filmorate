package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.PastDate;
import ru.yandex.practicum.filmorate.validation.ValidDate;

/**
 * User.
 */

@Data
public class User {
    Long id;
    @NotNull(message = "электронная почта не может быть null")
    @Email(message = "поле должно иметь формат адреса электронной почты")
    String email;
    @NotNull(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    String login;
    String name;
    @NotNull(message = "День рождение пользователя не может быть null")
    @ValidDate(message = "Дата дня рождения должна быть в формате yyyy-MM-dd")
    @PastDate
    String birthday;
}
