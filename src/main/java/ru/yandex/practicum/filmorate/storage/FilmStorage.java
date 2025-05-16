package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film save(Film newFilm);

    Optional<Film> findById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}
