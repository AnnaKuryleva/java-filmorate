package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaDao {
    Collection<Mpa> findAll();

    Optional<Mpa> findById(Integer id);
}
