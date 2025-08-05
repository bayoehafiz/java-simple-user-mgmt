package com.example.userapi.integration;

import com.example.userapi.config.TestSecurityConfig;
import com.example.userapi.dto.UserCreateRequest;
import com.example.userapi.dto.UserUpdateRequest;
import com.example.userapi.model.User;
import com.example.userapi.model.Role;
import com.example.userapi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class UserApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
        // Clear any existing data to ensure test isolation
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        UserCreateRequest createRequest = new UserCreateRequest("Integration User", "integration@example.com", 40, "integration_user_" + System.currentTimeMillis(), "Password123", Role.USER);
        ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, createRequest, User.class);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Integration User", response.getBody().getName());
    }

    @Test
    void testGetAllUsers() {
        userRepository.save(new User(null, "User One", "one@example.com", 25));
        userRepository.save(new User(null, "User Two", "two@example.com", 30));

        ResponseEntity<User[]> response = restTemplate.getForEntity(baseUrl, User[].class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);
    }

    @Test
    void testGetUserById() {
        User savedUser = userRepository.save(new User(null, "Specific User", "specific@example.com", 30));
        ResponseEntity<User> response = restTemplate.getForEntity(baseUrl + "/" + savedUser.getId(), User.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Specific User", response.getBody().getName());
    }

    @Test
    void testUpdateUser() {
        User savedUser = userRepository.save(new User(null, "Old User", "old@example.com", 30));
        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated User", "old@example.com", 30);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<UserUpdateRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<User> response = restTemplate.exchange(baseUrl + "/" + savedUser.getId(), HttpMethod.PUT, entity, User.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Updated User", response.getBody().getName());
    }

    @Test
    void testDeleteUser() {
        User savedUser = userRepository.save(new User(null, "Delete User", "delete@example.com", 30));
        restTemplate.delete(baseUrl + "/" + savedUser.getId());
        ResponseEntity<User> response = restTemplate.getForEntity(baseUrl + "/" + savedUser.getId(), User.class);
        assertEquals(404, response.getStatusCodeValue());
    }
}

