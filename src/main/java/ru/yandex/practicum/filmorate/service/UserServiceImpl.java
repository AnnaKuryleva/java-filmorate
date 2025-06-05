package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserDao;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public Collection<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User create(User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return userDao.save(newUser);
    }

    @Override
    public void update(User newUser) {
        userDao.findById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден"));
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        userDao.updateUser(newUser.getId(), newUser);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userDao.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        Optional<Friendship> existingRequest = userDao.findFriendship(userId, friendId);

        if (existingRequest.isEmpty()) {
            userDao.addFriend(userId, friendId);
        } else if (!existingRequest.get().isConfirmationStatus() &&
                existingRequest.get().getInviterId().equals(friendId) &&
                existingRequest.get().getAcceptorId().equals(userId)) {
            userDao.confirmFriend(userId, friendId);
        } else {
            throw new RuntimeException("Заявка уже существует или дружба подтверждена!");
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userDao.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        userDao.removeFriend(userId, friendId);
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userDao.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        userDao.confirmFriend(userId, friendId);
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long otherUserId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User otherUser = userDao.findById(otherUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + otherUserId + " не найден"));
        if (user.getFriends() == null || otherUser.getFriends() == null) {
            return Collections.emptyList();
        }
        return user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(friendId -> userDao.findById(friendId)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден")))
                .collect(Collectors.toList());
    }

    @Override
    public User findById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));

    }

    @Override
    public List<User> findFriends(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        if (user.getFriends() == null) {
            return Collections.emptyList();
        }
        return user.getFriends().stream()
                .map(friendId -> userDao.findById(friendId)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден")))
                .collect(Collectors.toList());
    }
}
