package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

public interface MpaService {
    Collection<MpaRating> getAllMpaRatings();

    Optional<MpaRating> getMpaRatingById(int id);
}
