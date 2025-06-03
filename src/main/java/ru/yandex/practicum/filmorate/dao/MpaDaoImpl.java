package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

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

    private MpaRating mapToMpaRating(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setRatingId(rs.getInt("rating_id"));
        mpaRating.setName(rs.getString("name"));
        return mpaRating;
    }

    @Override
    public Collection<MpaRating> findAll() {
        String sql = "SELECT * FROM rating";
        return jdbcTemplate.query(sql, this::mapToMpaRating);
    }

    @Override
    public Optional<MpaRating> findById(Integer id) {
        String sql = "SELECT * FROM rating WHERE rating_id = ?";
        return jdbcTemplate.query(sql, this::mapToMpaRating, id)
                .stream()
                .findFirst();
    }
}
