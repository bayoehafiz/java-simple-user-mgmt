package com.example.userapi.health;

import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserRepositoryHealthIndicatorTest {

    @Mock
    private UserRepository userRepository;

    private UserRepositoryHealthIndicator healthIndicator;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        healthIndicator = new UserRepositoryHealthIndicator();
        ReflectionTestUtils.setField(healthIndicator, "userRepository", userRepository);
    }

    @Test
    void testHealthIndicatorWithHealthyRepository() {
        // Given
        List<User> users = List.of(
            new User(1L, "John Doe", "john@example.com", 30),
            new User(2L, "Jane Smith", "jane@example.com", 25)
        );
        when(userRepository.findAll()).thenReturn(users);

        // When
        Health health = healthIndicator.health();

        // Then
        assertEquals(Status.UP, health.getStatus());
        assertEquals("User repository is healthy", health.getDetails().get("message"));
        assertEquals(2, health.getDetails().get("userCount"));
        assertTrue((Boolean) health.getDetails().get("dataFileAccessible"));
        assertNotNull(health.getDetails().get("dataFilePath"));
    }

    @Test
    void testHealthIndicatorWithEmptyRepository() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        Health health = healthIndicator.health();

        // Then
        assertEquals(Status.UP, health.getStatus());
        assertEquals("User repository is healthy", health.getDetails().get("message"));
        assertEquals(0, health.getDetails().get("userCount"));
        assertTrue((Boolean) health.getDetails().get("dataFileAccessible"));
    }

    @Test
    void testHealthIndicatorWithRepositoryException() {
        // Given
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        // When
        Health health = healthIndicator.health();

        // Then
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("User repository check failed", health.getDetails().get("message"));
        assertEquals("Database connection failed", health.getDetails().get("error"));
    }

    @Test
    void testHealthIndicatorWithNullPointerException() {
        // Given
        when(userRepository.findAll()).thenThrow(new NullPointerException("Null pointer error"));

        // When
        Health health = healthIndicator.health();

        // Then
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("User repository check failed", health.getDetails().get("message"));
        assertEquals("Null pointer error", health.getDetails().get("error"));
    }

    @Test
    void testHealthIndicatorDetailsContainCorrectInformation() {
        // Given
        List<User> users = List.of(new User(1L, "Test User", "test@example.com", 25));
        when(userRepository.findAll()).thenReturn(users);

        // When
        Health health = healthIndicator.health();

        // Then
        assertEquals(Status.UP, health.getStatus());
        
        // Verify all expected details are present
        assertTrue(health.getDetails().containsKey("message"));
        assertTrue(health.getDetails().containsKey("userCount"));
        assertTrue(health.getDetails().containsKey("dataFileAccessible"));
        assertTrue(health.getDetails().containsKey("dataFilePath"));
        
        // Verify detail values
        assertEquals(1, health.getDetails().get("userCount"));
        assertTrue(health.getDetails().get("dataFilePath").toString().endsWith("users.json"));
    }
}
