package com.example.userapi.repository;

import com.example.userapi.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    
    private String DATA_FILE = "users.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicLong idCounter = new AtomicLong(1);
    private List<User> users = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadUsersFromFile();
        // Update ID counter to avoid conflicts
        if (!users.isEmpty()) {
            long maxId = users.stream().mapToLong(User::getId).max().orElse(0);
            idCounter.set(maxId + 1);
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public Optional<User> findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public User save(User user) {
        if (user.getId() == null) {
            // Create new user
            user.setId(idCounter.getAndIncrement());
            users.add(user);
        } else {
            // Update existing user
            Optional<User> existingUser = findById(user.getId());
            if (existingUser.isPresent()) {
                int index = users.indexOf(existingUser.get());
                users.set(index, user);
            } else {
                users.add(user);
            }
        }
        saveUsersToFile();
        return user;
    }

    public boolean deleteById(Long id) {
        boolean removed = users.removeIf(user -> user.getId().equals(id));
        if (removed) {
            saveUsersToFile();
        }
        return removed;
    }

    private void loadUsersFromFile() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                users = objectMapper.readValue(file, new TypeReference<List<User>>() {});
            } else {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
            users = new ArrayList<>();
        }
    }

    private void saveUsersToFile() {
        try {
            objectMapper.writeValue(new File(DATA_FILE), users);
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }
}
