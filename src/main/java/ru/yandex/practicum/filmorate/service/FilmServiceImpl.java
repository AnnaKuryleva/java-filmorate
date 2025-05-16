package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private long getNextId() {
        long currentMaxId = filmStorage.findAll()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film create(Film newFilm) {
        newFilm.setId(getNextId());
        filmStorage.save(newFilm);
        return newFilm;
    }

    @Override
    public void update(Film newFilm) {
        Film oldFilm = filmStorage.findById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден"));
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        filmStorage.save(oldFilm);
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmStorage.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes() != null ? film.getLikes().size() : 0)
                        .reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

}

