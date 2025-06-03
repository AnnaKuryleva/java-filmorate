package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

public interface MpaDao {
    Collection<MpaRating> findAll();

    Optional<MpaRating> findById(Integer id);
}
