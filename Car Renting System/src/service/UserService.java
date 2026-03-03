package service;

import models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages user registration and lookup.
 */
public class UserService {
    private final Map<String, User> users = new HashMap<>();

    public User registerUser(String name, String email, String phone) {
        String id = "USR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        User user = new User(id, name, email, phone);
        users.put(id, user);
        System.out.println("  [UserService] Registered: " + user);
        return user;
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
