package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    private long getNextId() {
        long currentMaxId = userStorage.findAll()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User create(User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        newUser.setId(getNextId());
        userStorage.save(newUser);
        return newUser;
    }

    @Override
    public void update(User newUser) {
        userStorage.findById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден"));
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        userStorage.updateUser(newUser.getId(), newUser);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        userStorage.addFriend(userId, friendId);
        userStorage.addFriend(friendId, userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        userStorage.removeFriend(userId, friendId);
        userStorage.removeFriend(friendId, userId);

    }

    @Override
    public List<User> findCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User otherUser = userStorage.findById(otherUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + otherUserId + " не найден"));
        if (user.getFriends() == null || otherUser.getFriends() == null) {
            return Collections.emptyList();
        }
        return user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(friendId -> userStorage.findById(friendId)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден")))
                .collect(Collectors.toList());
    }

    @Override
    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));

    }

    @Override
    public List<User> findFriends(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        if (user.getFriends() == null) {
            return Collections.emptyList();
        }
        return user.getFriends().stream()
                .map(friendId -> userStorage.findById(friendId)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден")))
                .collect(Collectors.toList());

    }
}
