package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Collection<Film> findAll();

    Film save(Film newFilm);

    Optional<Film> findById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    void updateFilm(Long id, Film updatedFilm);

    List<Film> getTopFilms(int count);
}
