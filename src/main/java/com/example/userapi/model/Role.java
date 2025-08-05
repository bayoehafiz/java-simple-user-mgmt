package com.example.userapi.model;

/**
 * Enum representing different user roles in the system.
 * Each role has different levels of access and permissions.
 */
public enum Role {
    /**
     * Admin role - full access to all operations including user management
     */
    ADMIN("ROLE_ADMIN"),
    
    /**
     * Manager role - can read and update users, but cannot delete
     */
    MANAGER("ROLE_MANAGER"),
    
    /**
     * User role - can only read user information
     */
    USER("ROLE_USER");
    
    private final String authority;
    
    Role(String authority) {
        this.authority = authority;
    }
    
    public String getAuthority() {
        return authority;
    }
    
    @Override
    public String toString() {
        return authority;
    }
}
