package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getString("birthday"));
        String friendsSql = "SELECT acceptor_id FROM friendship WHERE inviter_id = ? " +
                "UNION " +
                "SELECT inviter_id FROM friendship WHERE acceptor_id = ? AND confirmation_status = true";
        user.setFriends(new HashSet<>(jdbcTemplate.query(friendsSql,
                (rs2, rowNum2) -> rs2.getLong(1), user.getId(), user.getId())));
        return user;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapToUser);
    }

    @Override
    public User save(User newUser) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, newUser.getEmail());
            ps.setString(2, newUser.getLogin());
            ps.setString(3, newUser.getName());
            ps.setString(4, newUser.getBirthday());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            newUser.setId(keyHolder.getKey().longValue());
        } else {
            throw new RuntimeException("Не удалось получить сгенерированный ключ");
        }
        return newUser;
    }


    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapToUser, id)
                .stream()
                .findFirst();
    }


    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friendship (inviter_id, acceptor_id, confirmation_status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, false);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendship WHERE inviter_id = ? AND acceptor_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void updateUser(Long id, User updatedUser) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, updatedUser.getEmail(), updatedUser.getLogin(),
                updatedUser.getName(), updatedUser.getBirthday(), id);
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        String sql = "UPDATE friendship SET confirmation_status = ? WHERE inviter_id = ? AND acceptor_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, true, friendId, userId);
        if (rowsAffected == 0) {
            throw new NotFoundException("Заявка на дружбу от пользователя с id = " + friendId + " не найдена");
        }
        String insertSql = "INSERT INTO friendship (inviter_id, acceptor_id, confirmation_status) " +
                "SELECT ?, ?, ? WHERE NOT EXISTS (" +
                "SELECT 1 FROM friendship WHERE inviter_id = ? AND acceptor_id = ?)";
        jdbcTemplate.update(insertSql, userId, friendId, true, userId, friendId);
    }

    @Override
    public Optional<Friendship> findFriendship(Long userId, Long friendId) {
        String sql = "SELECT * FROM friendship WHERE (inviter_id = ? AND acceptor_id = ?) OR (inviter_id = ? AND acceptor_id = ?)";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[]{userId, friendId, friendId, userId},
                    (rs, rowNum) -> {
                        Friendship friendship = new Friendship();
                        friendship.setInviterId(rs.getLong("inviter_id"));
                        friendship.setAcceptorId(rs.getLong("acceptor_id"));
                        friendship.setConfirmationStatus(rs.getBoolean("confirmation_status"));
                        return friendship;
                    }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
