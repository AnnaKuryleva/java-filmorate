package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.Collection;
import java.util.Optional;

@Service
public class MpaServiceImpl implements MpaService {

    private final MpaDao mpaDao;

    public MpaServiceImpl(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        return mpaDao.findAll();
    }

    @Override
    public Optional<MpaRating> getMpaRatingById(int id) {
        return mpaDao.findById(id);
    }
}

