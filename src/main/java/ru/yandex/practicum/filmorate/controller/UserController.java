package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

/**
 * Контроллер для управления пользователями.
 */

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        log.info("Получен запрос на поиск пользователя с id={}", id);
        return users.findById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriendsById(@PathVariable Long id) {
        log.info("Получен запрос на поиск друзей пользователя с id={}", id);
        return users.findFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос на поиск общих друзей пользователей с id={}", id);
        return users.findCommonFriends(id, otherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {
        users.create(newUser);
        log.info("Пользователь с id={} создан", newUser.getId());
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        users.update(newUser);
        log.info("Пользователь с id={} обновлен", newUser.getId());
        return newUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        users.addFriend(id, friendId);
        log.info("Пользователь с id={} добавил в друзья пользователя с id={}", id, friendId);
        return users.findById(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        users.deleteFriend(id, friendId);
        log.info("Пользователь с id={} удалил из в друзей пользователя с id={}", id, friendId);
        return users.findById(id);
    }
}
