package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<User> findAll();

    User create(User newUser);

    void update(User newUser);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> findCommonFriends(Long userId, Long otherUserId);

    User findById(Long id);

    List<User> findFriends(Long userId);
}

