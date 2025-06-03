package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {
    Collection<User> findAll();

    User save(User newUser);

    Optional<User> findById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    void updateUser(Long id, User updatedUser);

    void confirmFriend(Long userId, Long friendId);

    Optional<Friendship> findFriendship(Long userId, Long friendId);
}
