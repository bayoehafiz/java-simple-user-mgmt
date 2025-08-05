package com.example.userapi.controller;

import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User(null, "John Doe", "john@example.com", 30);
        User savedUser = new User(1L, "John Doe", "john@example.com", 30);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithInvalidData() throws Exception {
        // Test with empty request body
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated()); // Should still work with null values

        // Test with null values
        User userWithNulls = new User(null, null, null, null);
        User savedUserWithNulls = new User(1L, null, null, null);
        when(userRepository.save(any(User.class))).thenReturn(savedUserWithNulls);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithNulls)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User(1L, "John Doe", "john@example.com", 30);
        User user2 = new User(2L, "Jane Smith", "jane@example.com", 25);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsersEmpty() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com", 30);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateUser() throws Exception {
        User existingUser = new User(1L, "John Doe", "john@example.com", 30);
        User updatedUser = new User(1L, "John Updated", "john.updated@example.com", 31);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.age").value(31));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        User updatedUser = new User(999L, "Non Existent", "nonexistent@example.com", 25);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userRepository.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        when(userRepository.deleteById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).deleteById(999L);
    }

    @Test
    void testCreateUserWithSpecialCharacters() throws Exception {
        User user = new User(null, "José María", "test+special@email.com", 25);
        User savedUser = new User(1L, "José María", "test+special@email.com", 25);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("José María"))
                .andExpect(jsonPath("$.email").value("test+special@email.com"));
    }

    @Test
    void testCreateUserWithLongFields() throws Exception {
        String longName = "A".repeat(1000);
        String longEmail = "test@" + "a".repeat(240) + ".com";
        User user = new User(null, longName, longEmail, 100);
        User savedUser = new User(1L, longName, longEmail, 100);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(longName))
                .andExpect(jsonPath("$.email").value(longEmail))
                .andExpect(jsonPath("$.age").value(100));
    }

    @Test
    void testInvalidHttpMethods() throws Exception {
        // Test unsupported methods
        mockMvc.perform(patch("/api/users"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(patch("/api/users/1"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testInvalidContentType() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
