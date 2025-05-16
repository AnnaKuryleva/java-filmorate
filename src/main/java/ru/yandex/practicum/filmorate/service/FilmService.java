package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Collection<Film> findAll();

    Film create(Film newFilm);

    void update(Film newFilm);

    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);

    List<Film> getTopFilms(int count);

    Film findById(Long id);
}
