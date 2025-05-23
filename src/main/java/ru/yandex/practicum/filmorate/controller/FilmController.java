package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

/**
 * Контроллер для управления фильмами.
 */

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService films;

    public FilmController(FilmService films) {
        this.films = films;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.findAll();
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение списка самых популярных фильмов");
        return films.getTopFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {
        films.create(newFilm);
        log.info("Фильм с id={} создан", newFilm.getId());
        return newFilm;
    }


    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        films.update(newFilm);
        log.info("Фильм с id={} обновлён", newFilm.getId());
        return newFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        films.addLike(userId, filmId);
        log.info("Лайк пользователя с id={} добавлен к фильму с id={}", userId, filmId);
        return films.findById(filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        films.deleteLike(userId, id);
        log.info("Лайк пользователя с id={} у фильма с id={} удалён", userId, id);
        return films.findById(id);
    }
}

