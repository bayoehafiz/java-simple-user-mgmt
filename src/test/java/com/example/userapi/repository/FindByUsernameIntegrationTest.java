package com.example.userapi.repository;

import com.example.userapi.model.Role;
import com.example.userapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test demonstrating the findByUsername functionality
 * with realistic user scenarios and authentication use cases.
 */
class FindByUsernameIntegrationTest {

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
        
        // Set up test users with realistic authentication data
        setupTestUsers();
    }

    private void setupTestUsers() {
        // Admin user
        User admin = new User("Admin User", "admin@example.com", 35, "admin", "admin123", Role.ADMIN);
        userRepository.save(admin);

        // Regular users
        User john = new User("John Doe", "john@example.com", 30, "johndoe", "password123", Role.USER);
        userRepository.save(john);

        User jane = new User("Jane Smith", "jane@example.com", 28, "janesmith", "securepass", Role.USER);
        userRepository.save(jane);

        User manager = new User("Mike Manager", "mike@example.com", 40, "manager", "mgr2023", Role.MANAGER);
        userRepository.save(manager);

        // User with special characters in username
        User specialUser = new User("Test User", "test@example.com", 25, "test.user@company", "testpass", Role.USER);
        userRepository.save(specialUser);
    }

    @Test
    void testAuthenticationScenario() {
        // Simulate authentication - finding user by username
        String loginUsername = "johndoe";
        String loginPassword = "password123";

        Optional<User> foundUser = userRepository.findByUsername(loginUsername);

        assertTrue(foundUser.isPresent(), "User should be found by username");
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getEmail());
        assertEquals(Role.USER, foundUser.get().getRole());
        assertEquals(loginPassword, foundUser.get().getPassword());
        assertTrue(foundUser.get().isEnabled());
    }

    @Test
    void testAdminLookup() {
        // Find admin user for authorization checks
        Optional<User> admin = userRepository.findByUsername("admin");

        assertTrue(admin.isPresent());
        assertEquals(Role.ADMIN, admin.get().getRole());
        assertEquals("Admin User", admin.get().getName());
    }

    @Test
    void testManagerLookup() {
        // Find manager for role-based operations
        Optional<User> manager = userRepository.findByUsername("manager");

        assertTrue(manager.isPresent());
        assertEquals(Role.MANAGER, manager.get().getRole());
        assertEquals("Mike Manager", manager.get().getName());
    }

    @Test
    void testSpecialCharacterUsername() {
        // Test finding user with special characters in username
        Optional<User> specialUser = userRepository.findByUsername("test.user@company");

        assertTrue(specialUser.isPresent());
        assertEquals("Test User", specialUser.get().getName());
        assertEquals("test@example.com", specialUser.get().getEmail());
    }

    @Test
    void testFailedAuthentication() {
        // Simulate failed authentication - user doesn't exist
        Optional<User> nonExistentUser = userRepository.findByUsername("hacker123");

        assertFalse(nonExistentUser.isPresent(), "Non-existent user should not be found");
    }

    @Test
    void testCaseSensitiveUsername() {
        // Test that username search is case sensitive
        Optional<User> lowercase = userRepository.findByUsername("johndoe");
        Optional<User> uppercase = userRepository.findByUsername("JOHNDOE");
        Optional<User> mixedCase = userRepository.findByUsername("JohnDoe");

        assertTrue(lowercase.isPresent(), "Exact case should match");
        assertFalse(uppercase.isPresent(), "Different case should not match");
        assertFalse(mixedCase.isPresent(), "Different case should not match");
    }

    @Test
    void testUserProfileUpdate() {
        // Simulate user profile update scenario
        String username = "janesmith";
        Optional<User> user = userRepository.findByUsername(username);

        assertTrue(user.isPresent());
        User jane = user.get();
        
        // Update user information
        jane.setName("Jane Smith-Johnson");
        jane.setEmail("jane.johnson@example.com");
        jane.setAge(29);
        
        userRepository.save(jane);

        // Verify the user can still be found by username and has updated info
        Optional<User> updatedUser = userRepository.findByUsername(username);
        assertTrue(updatedUser.isPresent());
        assertEquals("Jane Smith-Johnson", updatedUser.get().getName());
        assertEquals("jane.johnson@example.com", updatedUser.get().getEmail());
        assertEquals(29, updatedUser.get().getAge());
    }

    @Test
    void testUsernameChangeScenario() {
        // Test scenario where user changes username
        String oldUsername = "johndoe";
        String newUsername = "john.doe.updated";

        Optional<User> user = userRepository.findByUsername(oldUsername);
        assertTrue(user.isPresent());

        User john = user.get();
        john.setUsername(newUsername);
        userRepository.save(john);

        // Old username should no longer work
        Optional<User> oldUsernameSearch = userRepository.findByUsername(oldUsername);
        assertFalse(oldUsernameSearch.isPresent());

        // New username should work
        Optional<User> newUsernameSearch = userRepository.findByUsername(newUsername);
        assertTrue(newUsernameSearch.isPresent());
        assertEquals("John Doe", newUsernameSearch.get().getName());
    }

    @Test
    void testAccountDisabling() {
        // Test finding disabled user account
        String username = "janesmith";
        Optional<User> user = userRepository.findByUsername(username);
        assertTrue(user.isPresent());

        // Disable the account
        User jane = user.get();
        jane.setEnabled(false);
        userRepository.save(jane);

        // User should still be findable by username but disabled
        Optional<User> disabledUser = userRepository.findByUsername(username);
        assertTrue(disabledUser.isPresent());
        assertFalse(disabledUser.get().isEnabled());
    }

    @Test
    void testRoleBasedOperations() {
        // Test finding users by username for role-based operations
        String[] adminUsernames = {"admin"};
        String[] managerUsernames = {"manager"};
        String[] regularUsernames = {"johndoe", "janesmith"};

        // Verify admin users
        for (String username : adminUsernames) {
            Optional<User> user = userRepository.findByUsername(username);
            assertTrue(user.isPresent());
            assertEquals(Role.ADMIN, user.get().getRole());
        }

        // Verify manager users
        for (String username : managerUsernames) {
            Optional<User> user = userRepository.findByUsername(username);
            assertTrue(user.isPresent());
            assertEquals(Role.MANAGER, user.get().getRole());
        }

        // Verify regular users
        for (String username : regularUsernames) {
            Optional<User> user = userRepository.findByUsername(username);
            assertTrue(user.isPresent());
            assertEquals(Role.USER, user.get().getRole());
        }
    }
}
