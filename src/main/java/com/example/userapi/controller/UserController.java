package com.example.userapi.controller;

import com.example.userapi.model.User;
import com.example.userapi.dto.UserCreateRequest;
import com.example.userapi.dto.UserUpdateRequest;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.exception.DuplicateUserException;
import javax.validation.Valid;
import com.example.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET /api/users - Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // GET /api/users/{id} - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            throw new UserNotFoundException(id);
        }
    }

    // GET /api/users/username/{username} - Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            throw new UserNotFoundException("username", username);
        }
    }

    // POST /api/users - Create new user
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest userRequest) {
        // Check for duplicate username
        if(userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new DuplicateUserException("username", userRequest.getUsername());
        }

        User user = new User(
            userRequest.getName(),
            userRequest.getEmail(),
            userRequest.getAge(),
            userRequest.getUsername(),
            userRequest.getPassword(),
            userRequest.getRole()
        );
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // PUT /api/users/{id} - Update user by ID
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userRequest) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if(userRequest.getName() != null) user.setName(userRequest.getName());
            if(userRequest.getEmail() != null) user.setEmail(userRequest.getEmail());
            if(userRequest.getAge() != null) user.setAge(userRequest.getAge());
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    // DELETE /api/users/{id} - Delete user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userRepository.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            throw new UserNotFoundException(id);
        }
    }
}
