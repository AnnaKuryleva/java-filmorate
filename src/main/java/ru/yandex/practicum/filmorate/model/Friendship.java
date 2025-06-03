package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Friendship {
    @NotNull(message = "ID пользователя посылающего запрос на дружбу не может быть null")
    private Long inviterId;
    @NotNull(message = "ID пользователя получающего запрос на дружбу не может быть null")
    private Long acceptorId;
    @NotNull(message = "Статус подтверждения дружбы не может быть null")
    private boolean confirmationStatus;
}
