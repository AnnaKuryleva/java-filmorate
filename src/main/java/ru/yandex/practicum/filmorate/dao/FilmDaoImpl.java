package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Film mapToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getString("release_date"));
        film.setDuration(resultSet.getLong("duration"));
        Mpa mpa = new Mpa();
        mpa.setRatingId(resultSet.getInt("rating_id"));
        mpa.setName(resultSet.getString("rating_name"));
        film.setMpa(mpa);

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        film.setLikes(new HashSet<>(jdbcTemplate.query(likesSql,
                (rs, row) -> rs.getLong("user_id"), film.getId())));

        String genresSql = "SELECT g.genre_id, g.name " +
                "FROM genre_id_film_id fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        film.setGenres(new HashSet<>(jdbcTemplate.query(genresSql,
                (rs, row) -> {
                    Genre genre = new Genre();
                    genre.setGenreId(rs.getLong("genre_id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }, film.getId())));

        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.*, r.name AS rating_name " +
                "FROM films f " +
                "JOIN rating r ON f.rating_id = r.rating_id";
        return jdbcTemplate.query(sql, this::mapToFilm);
    }

    @Override
    public Film save(Film newFilm) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, newFilm.getName());
            ps.setString(2, newFilm.getDescription());
            ps.setString(3, newFilm.getReleaseDate());
            ps.setLong(4, newFilm.getDuration());
            ps.setInt(5, newFilm.getMpa().getRatingId());
            return ps;
        }, keyHolder);
        newFilm.setId(keyHolder.getKey().longValue());
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO genre_id_film_id (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : newFilm.getGenres()) {
                jdbcTemplate.update(genreSql, newFilm.getId(), genre.getGenreId());
            }
        }
        return newFilm;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT f.*, r.name AS rating_name " +
                "FROM films f " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "WHERE f.id = ?";
        return jdbcTemplate.query(sql, this::mapToFilm, id)
                .stream()
                .findFirst();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (Exception e) {
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, filmId, userId);
        if (rowsAffected == 0) {
            throw new NotFoundException("Лайк не найден");
        }
    }

    @Override
    public void updateFilm(Long id, Film updatedFilm) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                updatedFilm.getName(),
                updatedFilm.getDescription(),
                updatedFilm.getReleaseDate(),
                updatedFilm.getDuration(),
                updatedFilm.getMpa().getRatingId(),
                id);
        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        jdbcTemplate.update("DELETE FROM genre_id_film_id WHERE film_id = ?", id);
        if (updatedFilm.getGenres() != null && !updatedFilm.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO genre_id_film_id (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : updatedFilm.getGenres()) {
                jdbcTemplate.update(genreSql, id, genre.getGenreId());
            }
        }
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = "SELECT f.*, r.name AS rating_name, COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id, r.name " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapToFilm, count);
    }
}
