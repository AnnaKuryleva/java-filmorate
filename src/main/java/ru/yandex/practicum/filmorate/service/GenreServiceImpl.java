package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl  implements GenreService{
    private final GenreDao genreDao;

    public Collection<Genre> findAll() {
        return genreDao.findAll();
    }

    public Genre findById(Long id) {
        return genreDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Жанр с id=" + id + " не найден"));
    }
}
