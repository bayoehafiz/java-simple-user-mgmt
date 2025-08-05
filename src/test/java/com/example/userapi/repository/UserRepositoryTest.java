package com.example.userapi.repository;

import com.example.userapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        
        // Set up temporary file for testing
        File tempFile = tempDir.resolve("test-users.json").toFile();
        ReflectionTestUtils.setField(userRepository, "DATA_FILE", tempFile.getAbsolutePath());
        
        // Initialize the repository
        userRepository.init();
    }

    @Test
    void testSaveNewUser() {
        // Given
        User user = new User(null, "John Doe", "john@example.com", 30);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals(30, savedUser.getAge());
    }

    @Test
    void testFindAllUsers() {
        // Given
        User user1 = new User(null, "John Doe", "john@example.com", 30);
        User user2 = new User(null, "Jane Smith", "jane@example.com", 25);
        
        userRepository.save(user1);
        userRepository.save(user2);

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> "John Doe".equals(u.getName())));
        assertTrue(users.stream().anyMatch(u -> "Jane Smith".equals(u.getName())));
    }

    @Test
    void testFindById() {
        // Given
        User user = new User(null, "John Doe", "john@example.com", 30);
        User savedUser = userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByIdNotFound() {
        // When
        Optional<User> foundUser = userRepository.findById(999L);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        // Given
        User user = new User(null, "John Doe", "john@example.com", 30);
        User savedUser = userRepository.save(user);

        // When
        savedUser.setName("John Updated");
        savedUser.setEmail("john.updated@example.com");
        savedUser.setAge(31);
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("John Updated", updatedUser.getName());
        assertEquals("john.updated@example.com", updatedUser.getEmail());
        assertEquals(31, updatedUser.getAge());
    }

    @Test
    void testDeleteById() {
        // Given
        User user = new User(null, "John Doe", "john@example.com", 30);
        User savedUser = userRepository.save(user);

        // When
        boolean deleted = userRepository.deleteById(savedUser.getId());

        // Then
        assertTrue(deleted);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testDeleteByIdNotFound() {
        // When
        boolean deleted = userRepository.deleteById(999L);

        // Then
        assertFalse(deleted);
    }

    @Test
    void testIdGeneration() {
        // Given
        User user1 = new User(null, "User 1", "user1@example.com", 25);
        User user2 = new User(null, "User 2", "user2@example.com", 30);

        // When
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        // Then
        assertNotNull(savedUser1.getId());
        assertNotNull(savedUser2.getId());
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
        assertTrue(savedUser2.getId() > savedUser1.getId());
    }

    @Test
    void testSaveUserWithNullValues() {
        // Given
        User user = new User(null, null, null, null);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertNull(savedUser.getName());
        assertNull(savedUser.getEmail());
        assertNull(savedUser.getAge());
    }

    @Test
    void testSaveUserWithEmptyStrings() {
        // Given
        User user = new User(null, "", "", 0);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("", savedUser.getName());
        assertEquals("", savedUser.getEmail());
        assertEquals(0, savedUser.getAge());
    }

    @Test
    void testSaveUserWithSpecialCharacters() {
        // Given
        User user = new User(null, "José María", "test+special@email.com", 25);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("José María", savedUser.getName());
        assertEquals("test+special@email.com", savedUser.getEmail());
        assertEquals(25, savedUser.getAge());
    }

    @Test
    void testSaveUserWithLongStrings() {
        // Given
        String longName = "A".repeat(1000);
        String longEmail = "test@" + "a".repeat(240) + ".com";
        User user = new User(null, longName, longEmail, 100);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals(longName, savedUser.getName());
        assertEquals(longEmail, savedUser.getEmail());
        assertEquals(100, savedUser.getAge());
    }

    @Test
    void testSaveUserWithNegativeAge() {
        // Given
        User user = new User(null, "Negative Age User", "negative@example.com", -5);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("Negative Age User", savedUser.getName());
        assertEquals("negative@example.com", savedUser.getEmail());
        assertEquals(-5, savedUser.getAge());
    }

    @Test
    void testFindAllWhenEmpty() {
        // When
        List<User> users = userRepository.findAll();

        // Then
        assertNotNull(users);
        assertTrue(users.isEmpty());
        assertEquals(0, users.size());
    }

    @Test
    void testMultipleDeleteOperations() {
        // Given
        User user1 = new User(null, "User 1", "user1@example.com", 25);
        User user2 = new User(null, "User 2", "user2@example.com", 30);
        User user3 = new User(null, "User 3", "user3@example.com", 35);
        
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        // When - Delete middle user
        boolean deleted2 = userRepository.deleteById(savedUser2.getId());
        
        // Then
        assertTrue(deleted2);
        assertEquals(2, userRepository.findAll().size());
        assertTrue(userRepository.findById(savedUser1.getId()).isPresent());
        assertFalse(userRepository.findById(savedUser2.getId()).isPresent());
        assertTrue(userRepository.findById(savedUser3.getId()).isPresent());
    }

    @Test
    void testUpdateNonExistentUserCreatesNew() {
        // Given
        User user = new User(999L, "Non Existent", "nonexistent@example.com", 25);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertEquals(999L, savedUser.getId());
        assertEquals("Non Existent", savedUser.getName());
        assertTrue(userRepository.findById(999L).isPresent());
    }

    @Test
    void testDataPersistenceAfterMultipleOperations() {
        // Given
        User user1 = new User(null, "User 1", "user1@example.com", 25);
        User user2 = new User(null, "User 2", "user2@example.com", 30);
        
        // When - Save users
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        
        // Update first user
        savedUser1.setName("Updated User 1");
        userRepository.save(savedUser1);
        
        // Delete second user
        userRepository.deleteById(savedUser2.getId());
        
        // Then - Verify final state
        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());
        assertEquals("Updated User 1", allUsers.get(0).getName());
        assertEquals(savedUser1.getId(), allUsers.get(0).getId());
    }

    @Test
    void testIdCounterContinuityAfterDeletion() {
        // Given
        User user1 = new User(null, "User 1", "user1@example.com", 25);
        User user2 = new User(null, "User 2", "user2@example.com", 30);
        
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        
        // When - Delete first user and add new user
        userRepository.deleteById(savedUser1.getId());
        User user3 = new User(null, "User 3", "user3@example.com", 35);
        User savedUser3 = userRepository.save(user3);
        
        // Then - New ID should be higher than previous max
        assertTrue(savedUser3.getId() > savedUser2.getId());
        assertNotEquals(savedUser1.getId(), savedUser3.getId());
    }

    @Test
    void testConcurrentModification() {
        // Given
        User user1 = new User(null, "User 1", "user1@example.com", 25);
        User user2 = new User(null, "User 2", "user2@example.com", 30);
        
        userRepository.save(user1);
        userRepository.save(user2);
        
        // When - Get all users and modify while iterating
        List<User> users = userRepository.findAll();
        
        // Add new user while we have the list
        User user3 = new User(null, "User 3", "user3@example.com", 35);
        userRepository.save(user3);
        
        // Then - Original list should be unchanged (defensive copy)
        assertEquals(2, users.size());
        assertEquals(3, userRepository.findAll().size());
    }
}
