package com.example.userapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("age")
    private Integer age;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    @JsonProperty("role")
    private Role role;
    
    @JsonProperty("enabled")
    private boolean enabled;

    // Default constructor
    public User() {
        this.enabled = true;
        this.role = Role.USER; // Default role
    }

    // Constructor with parameters
    public User(Long id, String name, String email, Integer age) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }
    
    // Constructor with authentication parameters
    public User(String name, String email, Integer age, String username, String password, Role role) {
        this();
        this.name = name;
        this.email = email;
        this.age = age;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        this.role = role;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
