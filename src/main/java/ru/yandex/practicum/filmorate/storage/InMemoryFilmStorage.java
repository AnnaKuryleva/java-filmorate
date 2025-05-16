package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film save(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(userId);
        save(film);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film.getLikes() != null) {
            film.getLikes().remove(userId);
            save(film);
        }
    }
}
