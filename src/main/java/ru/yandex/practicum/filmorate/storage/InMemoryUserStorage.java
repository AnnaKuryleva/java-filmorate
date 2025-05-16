package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User save(User newUser) {
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        user.getFriends().add(friendId);
        save(user);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        if (user.getFriends() != null) {
            user.getFriends().remove(friendId);
            save(user);
        }
    }

    @Override
    public void updateUser(Long id, User updatedUser) {
        User user = users.get(id);
        user.setEmail(updatedUser.getEmail());
        user.setLogin(updatedUser.getLogin());
        user.setName(updatedUser.getName());
        user.setBirthday(updatedUser.getBirthday());
        save(user);
    }
}
