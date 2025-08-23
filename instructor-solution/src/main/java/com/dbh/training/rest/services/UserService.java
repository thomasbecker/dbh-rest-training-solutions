package com.dbh.training.rest.services;

import com.dbh.training.rest.models.AccountStatus;
import com.dbh.training.rest.models.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User Service with in-memory storage
 * 
 * Exercise 08: Security Implementation
 * Manages users with authentication support
 */
@Singleton
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public UserService() {
        initializeDefaultUsers();
    }
    
    /**
     * Initialize with default users for testing
     */
    private void initializeDefaultUsers() {
        // Admin user
        User admin = new User();
        admin.setId(idGenerator.getAndIncrement());
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setPassword("admin123"); // Will be hashed
        admin.setRoles(new HashSet<>(Arrays.asList("USER", "ADMIN")));
        admin.setStatus(AccountStatus.ACTIVE);
        admin.setCreatedAt(LocalDateTime.now());
        
        users.put(admin.getId(), admin);
        usersByUsername.put(admin.getUsername(), admin);
        
        // Regular user
        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setUsername("user");
        user.setEmail("user@example.com");
        user.setFirstName("Regular");
        user.setLastName("User");
        user.setPassword("user123"); // Will be hashed
        user.setRoles(new HashSet<>(Collections.singletonList("USER")));
        user.setStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        
        users.put(user.getId(), user);
        usersByUsername.put(user.getUsername(), user);
        
        // Test user
        User test = new User();
        test.setId(idGenerator.getAndIncrement());
        test.setUsername("test");
        test.setEmail("test@example.com");
        test.setFirstName("Test");
        test.setLastName("User");
        test.setPassword("test123"); // Will be hashed
        test.setRoles(new HashSet<>(Collections.singletonList("USER")));
        test.setStatus(AccountStatus.ACTIVE);
        test.setCreatedAt(LocalDateTime.now());
        
        users.put(test.getId(), test);
        usersByUsername.put(test.getUsername(), test);
        
        logger.info("Initialized {} default users", users.size());
    }
    
    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        return usersByUsername.get(username);
    }
    
    /**
     * Find user by ID
     */
    public User findById(Long id) {
        return users.get(id);
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    /**
     * Create new user
     */
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        
        if (user.getStatus() == null) {
            user.setStatus(AccountStatus.ACTIVE);
        }
        
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(new HashSet<>(Collections.singletonList("USER")));
        }
        
        users.put(user.getId(), user);
        usersByUsername.put(user.getUsername(), user);
        
        logger.info("Created user: {}", user.getUsername());
        
        return user;
    }
    
    /**
     * Update existing user
     */
    public User updateUser(Long id, User user) {
        User existing = users.get(id);
        if (existing == null) {
            return null;
        }
        
        // Update fields
        existing.setEmail(user.getEmail());
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        
        if (user.getRoles() != null) {
            existing.setRoles(user.getRoles());
        }
        
        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }
        
        logger.info("Updated user: {}", existing.getUsername());
        
        return existing;
    }
    
    /**
     * Delete user
     */
    public boolean deleteUser(Long id) {
        User user = users.remove(id);
        if (user != null) {
            usersByUsername.remove(user.getUsername());
            logger.info("Deleted user: {}", user.getUsername());
            return true;
        }
        return false;
    }
    
    /**
     * Authenticate user with username and password
     */
    public User authenticate(String username, String password) {
        User user = findByUsername(username);
        if (user != null && user.checkPassword(password)) {
            user.setLastLogin(LocalDateTime.now());
            return user;
        }
        return null;
    }
}