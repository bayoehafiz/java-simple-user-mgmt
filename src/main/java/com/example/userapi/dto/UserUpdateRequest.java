package com.example.userapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;

/**
 * DTO for user update requests with validation.
 * Fields are optional for partial updates.
 */
public class UserUpdateRequest {
    
    @JsonProperty("name")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @JsonProperty("email")
    @Email(message = "Email should be valid")
    private String email;
    
    @JsonProperty("age")
    @Min(value = 0, message = "Age should not be less than 0")
    @Max(value = 150, message = "Age should not be greater than 150")
    private Integer age;

    // Default constructor
    public UserUpdateRequest() {}

    // Constructor
    public UserUpdateRequest(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
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
}
