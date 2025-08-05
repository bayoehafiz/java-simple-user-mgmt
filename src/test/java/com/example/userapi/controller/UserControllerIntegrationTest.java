package com.example.userapi.controller;

import com.example.userapi.config.TestSecurityConfig;
import com.example.userapi.dto.UserCreateRequest;
import com.example.userapi.dto.UserUpdateRequest;
import com.example.userapi.model.User;
import com.example.userapi.model.Role;
import com.example.userapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
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
    @WithMockUser(roles = {"USER"})
    void testCreateUser() throws Exception {
        UserCreateRequest createRequest = new UserCreateRequest("John Doe", "john@example.com", 30, "john_doe", "Password123", Role.USER);
        User savedUser = new User(1L, "John Doe", "john@example.com", 30);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithInvalidData() throws Exception {
        // Test with empty request body - should fail validation
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        // Test with incomplete data - should fail validation
        UserCreateRequest incompleteRequest = new UserCreateRequest();
        incompleteRequest.setName("Test");
        // Missing required fields

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteRequest)))
                .andExpect(status().isBadRequest());
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
    @WithMockUser(roles = {"ADMIN"})
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
    @WithMockUser(roles = {"USER"})
    void testUpdateUser() throws Exception {
        User existingUser = new User(1L, "John Doe", "john@example.com", 30);
        User updatedUser = new User(1L, "John Updated", "john.updated@example.com", 31);
        UserUpdateRequest updateRequest = new UserUpdateRequest("John Updated", "john.updated@example.com", 31);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
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
        UserUpdateRequest updateRequest = new UserUpdateRequest("Non Existent", "nonexistent@example.com", 25);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
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
        UserCreateRequest createRequest = new UserCreateRequest("José María", "test+special@email.com", 25, "jose_maria", "Password123", Role.USER);
        User savedUser = new User(1L, "José María", "test+special@email.com", 25);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("José María"))
                .andExpect(jsonPath("$.email").value("test+special@email.com"));
    }

    @Test
    void testCreateUserWithLongFields() throws Exception {
        String longName = "A".repeat(1000);
        String longEmail = "test@" + "a".repeat(240) + ".com";
        UserCreateRequest createRequest = new UserCreateRequest(longName, longEmail, 100, "longuser", "Password123", Role.USER);

        // Should fail validation due to field length constraints
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
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
