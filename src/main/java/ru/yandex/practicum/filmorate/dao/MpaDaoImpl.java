package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Mpa mapToMpaRating(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setRatingId(rs.getInt("rating_id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }

    @Override
    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM rating ORDER BY rating_id";
        return jdbcTemplate.query(sql, this::mapToMpaRating);
    }

    @Override
    public Optional<Mpa> findById(Integer id) {
        String sql = "SELECT * FROM rating WHERE rating_id = ?";
        return jdbcTemplate.query(sql, this::mapToMpaRating, id)
                .stream()
                .findFirst();
    }
}
