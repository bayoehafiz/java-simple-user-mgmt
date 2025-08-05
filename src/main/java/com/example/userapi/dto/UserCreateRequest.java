package com.example.userapi.dto;

import com.example.userapi.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;

/**
 * DTO for user creation requests with validation.
 */
public class UserCreateRequest {
    
    @JsonProperty("name")
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @JsonProperty("email")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
    
    @JsonProperty("age")
    @NotNull(message = "Age is mandatory")
    @Min(value = 0, message = "Age should not be less than 0")
    @Max(value = 150, message = "Age should not be greater than 150")
    private Integer age;
    
    @JsonProperty("username")
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    @JsonProperty("password")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;
    
    @JsonProperty("role")
    private Role role = Role.USER; // Default role

    // Default constructor
    public UserCreateRequest() {}

    // Constructor
    public UserCreateRequest(String name, String email, Integer age, String username, String password, Role role) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.username = username;
        this.password = password;
        this.role = role != null ? role : Role.USER;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role != null ? role : Role.USER;
    }
}
