package com.example.userapi.controller;

import com.example.userapi.model.Role;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication controller handling user registration and login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user account.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        if (isUsernameAlreadyTaken(request.getUsername())) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Username is already taken"));
        }

        User newUser = createNewUser(request);
        User savedUser = userRepository.save(newUser);
        
        String token = jwtUtil.generateTokenWithRole(
            savedUser.getUsername(), 
            savedUser.getRole().getAuthority()
        );

        return ResponseEntity.ok(createAuthResponse(token, savedUser));
    }

    /**
     * Authenticate user and return JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Invalid username or password"));
        }

        User user = userOptional.get();
        
        if (!isPasswordValid(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Invalid username or password"));
        }

        String token = jwtUtil.generateTokenWithRole(
            user.getUsername(), 
            user.getRole().getAuthority()
        );

        return ResponseEntity.ok(createAuthResponse(token, user));
    }

    private boolean isUsernameAlreadyTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private User createNewUser(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        user.setEnabled(true);
        return user;
    }

    private boolean isPasswordValid(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private Map<String, Object> createAuthResponse(String token, User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", createUserInfo(user));
        return response;
    }

    private Map<String, Object> createUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole().toString());
        return userInfo;
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    // DTOs for request handling
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String name;
        private String email;
        private Integer age;
        private String username;
        private String password;
        private String role;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
