package com.example.userapi.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 */
public class DuplicateUserException extends RuntimeException {
    
    public DuplicateUserException(String message) {
        super(message);
    }
    
    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DuplicateUserException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }
}
