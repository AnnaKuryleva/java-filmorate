package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmDao filmDao;
    private final UserDao userDao;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;

    @Override
    public Collection<Film> findAll() {
        return filmDao.findAll();
    }

    @Override
    public Film create(Film newFilm) {
        if (newFilm.getMpa() == null || newFilm.getMpa().getRatingId() == null) {
            throw new IllegalArgumentException("Рейтинг MPA обязателен");
        }
        mpaDao.findById(newFilm.getMpa().getRatingId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " +
                        newFilm.getMpa().getRatingId() + " не найден"));
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            Set<Long> uniqueGenreIds = newFilm.getGenres().stream()
                    .map(genre -> {
                        if (genre.getGenreId() == null) {
                            throw new IllegalArgumentException("ID жанра обязателен");
                        }
                        return genre.getGenreId();
                    })
                    .collect(Collectors.toSet());
            List<Genre> existingGenres = genreDao.findAllByIds(uniqueGenreIds);
            if (existingGenres.size() != uniqueGenreIds.size()) {
                Set<Long> foundIds = existingGenres.stream()
                        .map(Genre::getGenreId)
                        .collect(Collectors.toSet());
                String missingIds = uniqueGenreIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                throw new NotFoundException("жанры с id = [" + missingIds + "] не найдены");
            }
        }
        return filmDao.save(newFilm);
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmDao.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        filmDao.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmDao.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        filmDao.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmDao.getTopFilms(count);
    }

    @Override
    public Film findById(Long id) {
        return filmDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    @Override
    public Film update(Film newFilm) {
        filmDao.findById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден"));
        if (newFilm.getMpa() == null || newFilm.getMpa().getRatingId() == null) {
            throw new IllegalArgumentException("Рейтинг MPA обязателен");
        }
        mpaDao.findById(newFilm.getMpa().getRatingId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + newFilm.getMpa().getRatingId() +
                        " не найден"));
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            Set<Long> uniqueGenreIds = newFilm.getGenres().stream()
                    .map(genre -> {
                        if (genre.getGenreId() == null) {
                            throw new IllegalArgumentException("ID жанра обязателен");
                        }
                        return genre.getGenreId();
                    })
                    .collect(Collectors.toSet());
            List<Genre> existingGenres = genreDao.findAllByIds(uniqueGenreIds);
            if (existingGenres.size() != uniqueGenreIds.size()) {
                Set<Long> foundIds = existingGenres.stream()
                        .map(Genre::getGenreId)
                        .collect(Collectors.toSet());
                String missingIds = uniqueGenreIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                throw new NotFoundException("Жанры с id = [" + missingIds + "] не найдены");
            }
        }
        filmDao.updateFilm(newFilm.getId(), newFilm);
        return newFilm;
    }
}