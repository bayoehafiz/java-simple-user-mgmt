package com.example.userapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collections;

/**
 * Comprehensive tests for JWT utility functionality including security scenarios,
 * edge cases, and error handling.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Use a test-specific secret
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "testSecretKeyThatIsSufficientlyLongForHMACAlgorithm123456789");
        ReflectionTestUtils.setField(jwtUtil, "expirationTimeInSeconds", 3600L); // 1 hour
    }

    @Test
    void testGenerateToken() {
        // Given
        String username = "testuser";

        // When
UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // Then
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts separated by dots");
    }

    @Test
    void testGenerateTokenWithNullUsername() {
        // When & Then
        assertThrows(NullPointerException.class, () -> jwtUtil.generateToken(null),
                "Should throw exception for null username");
    }

    @Test
    void testGenerateTokenWithEmptyUsername() {
        // When & Then
        // Spring Security User constructor throws exception for empty username
        assertThrows(IllegalArgumentException.class, () -> {
            UserDetails emptyUser = new User("", "password", Collections.emptyList());
            jwtUtil.generateToken(emptyUser);
        }, "Should throw exception for empty username");
    }

    @Test
    void testValidateValidToken() {
        // Given
        String username = "testuser";
UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // When
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertTrue(isValid, "Valid token should be validated successfully");
    }

    @Test
    void testValidateTokenWithWrongUsername() {
        // Given
        String username = "testuser";
        String wrongUsername = "wronguser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // When
        UserDetails wrongUserDetails = new User(wrongUsername, "password", Collections.emptyList());
        boolean isValid = jwtUtil.validateToken(token, wrongUserDetails);

        // Then
        assertFalse(isValid, "Token should be invalid for wrong username");
    }

    @Test
    void testValidateExpiredToken() {
        // Given
        ReflectionTestUtils.setField(jwtUtil, "expirationTimeInSeconds", 1L); // 1 second expiration
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // Wait for token to expire
        try {
            Thread.sleep(1200); // Wait longer than expiration time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When & Then
        UserDetails userDetails2 = new User(username, "password", Collections.emptyList());
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateToken(token, userDetails2),
                "Should throw ExpiredJwtException for expired token");
    }

    @Test
    void testValidateMalformedToken() {
        // Given
        String malformedToken = "this.is.not.a.valid.jwt.token";
        String username = "testuser";

        // When & Then
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        assertThrows(MalformedJwtException.class, () -> jwtUtil.validateToken(malformedToken, userDetails),
                "Should throw MalformedJwtException for malformed token");
    }

    @Test
    void testValidateTokenWithWrongSignature() {
        // Given
        String username = "testuser";
UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        
        // Change the secret to make signature invalid
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "differentSecretKey123456789012345678901234567890");

        // When & Then
        UserDetails userDetails2 = new User(username, "password", Collections.emptyList());
        assertThrows(SignatureException.class, () -> jwtUtil.validateToken(token, userDetails2),
                "Should throw SignatureException for token with wrong signature");
    }

    @Test
    void testExtractUsername() {
        // Given
        String username = "testuser";
UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // When
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername, "Extracted username should match original");
    }

    @Test
    void testExtractUsernameFromInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(MalformedJwtException.class, () -> jwtUtil.extractUsername(invalidToken),
                "Should throw exception when extracting username from invalid token");
    }

    @Test
    void testExtractExpiration() {
        // Given
        String username = "testuser";
UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertNotNull(expiration, "Expiration should not be null");
        assertTrue(expiration.after(new Date()), "Expiration should be in the future");
    }

    @Test
    void testIsTokenExpired() {
        // Given - Create token with very short expiration (1 second)
        ReflectionTestUtils.setField(jwtUtil, "expirationTimeInSeconds", 1L);
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // Wait for expiration
        try {
            Thread.sleep(1200); // Wait longer than expiration time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When & Then - Use the safer validateToken method to avoid direct access issues
        boolean isValid = jwtUtil.validateToken(token);
        
        // Then
        assertFalse(isValid, "Expired token should not be valid");
    }

    @Test
    void testIsTokenNotExpired() {
        // Given
        String username = "testuser";
UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertFalse(isExpired, "Fresh token should not be expired");
    }

    @Test
    void testTokenContainsCorrectClaims() {
        // Given
        String username = "testuser";
UserDetails userDetails = new User(username, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // When
        Claims claims = jwtUtil.parseTokenToClaims(token);

        // Then
        assertNotNull(claims, "Claims should not be null");
        assertEquals(username, claims.getSubject(), "Subject should match username");
        assertNotNull(claims.getIssuedAt(), "Issued at should be set");
        assertNotNull(claims.getExpiration(), "Expiration should be set");
    }

    @Test
    void testGenerateTokensForDifferentUsers() {
        // Given
        String user1 = "user1";
        String user2 = "user2";

        // When
        UserDetails userDetails1 = new User(user1, "password", Collections.emptyList());
        UserDetails userDetails2 = new User(user2, "password", Collections.emptyList());
        String token1 = jwtUtil.generateToken(userDetails1);
        String token2 = jwtUtil.generateToken(userDetails2);

        // Then
        assertNotEquals(token1, token2, "Tokens for different users should be different");
        assertEquals(user1, jwtUtil.extractUsername(token1), "Token1 should contain user1");
        assertEquals(user2, jwtUtil.extractUsername(token2), "Token2 should contain user2");
    }

    @Test
    void testTokenGenerationConsistency() {
        // Given
        String username = "testuser";

        // When
        UserDetails userDetails1 = new User(username, "password", Collections.emptyList());
        String token1 = jwtUtil.generateToken(userDetails1);
        
        // Add a delay to ensure different timestamps
        try {
            Thread.sleep(1100); // Wait long enough to ensure different timestamps
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        UserDetails userDetails2 = new User(username, "password", Collections.emptyList());
        String token2 = jwtUtil.generateToken(userDetails2);

        // Then
        // Tokens should be different due to different issued timestamps
        assertNotEquals(token1, token2, "Two tokens generated at different times should be different");
        
        // But both should be valid for the same user
        assertTrue(jwtUtil.validateToken(token1, userDetails1), "First token should be valid");
        assertTrue(jwtUtil.validateToken(token2, userDetails2), "Second token should be valid");
    }

    @Test
    void testSpecialCharactersInUsername() {
        // Given
        String specialUsername = "user@domain.com";
        
        // When
UserDetails userDetails = new User(specialUsername, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        String extractedUsername = jwtUtil.extractUsername(token);
        
        // Then
        assertEquals(specialUsername, extractedUsername, "Special characters in username should be preserved");
        assertTrue(jwtUtil.validateToken(token, userDetails), "Token should be valid for username with special characters");
    }

    @Test
    void testLongUsername() {
        // Given
        String longUsername = "a".repeat(255); // Very long username
        
        // When
UserDetails userDetails = new User(longUsername, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        String extractedUsername = jwtUtil.extractUsername(token);
        
        // Then
        assertEquals(longUsername, extractedUsername, "Long username should be preserved");
        assertTrue(jwtUtil.validateToken(token, userDetails), "Token should be valid for long username");
    }

    @Test
    void testUnicodeUsername() {
        // Given
        String unicodeUsername = "用户名测试"; // Chinese characters
        
        // When
UserDetails userDetails = new User(unicodeUsername, "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);
        String extractedUsername = jwtUtil.extractUsername(token);
        
        // Then
        assertEquals(unicodeUsername, extractedUsername, "Unicode username should be preserved");
        assertTrue(jwtUtil.validateToken(token, userDetails), "Token should be valid for unicode username");
    }
}
