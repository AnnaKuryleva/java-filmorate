package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Genre mapToGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setGenreId(rs.getLong("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, this::mapToGenre);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.query(sql, this::mapToGenre, id)
                .stream()
                .findFirst();
    }

    @Override
    public List<Genre> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id IN (" +
                String.join(",", Collections.nCopies(ids.size(), "?")) + ")";
        return jdbcTemplate.query(sql, ids.toArray(), this::mapToGenre);
    }
}
