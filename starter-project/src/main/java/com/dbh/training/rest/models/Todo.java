package com.dbh.training.rest.models;

import java.time.LocalDateTime;

/**
 * Todo model for Exercise 09.
 * 
 * TODO Exercise 09 Task 1.1: Add the following to this class:
 * 
 * 1. Bean Validation annotations:
 *    - title: @NotBlank, @Size(min = 1, max = 200)
 *    - description: @Size(max = 1000)
 *    - priority: @NotNull
 * 
 * 2. Jackson annotations:
 *    - Date fields: @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
 *    - Consider using @JsonInclude for optional fields
 * 
 * 3. Constructor and builder pattern (optional but recommended)
 */
public class Todo {
    
    private Long id;
    
    // TODO: Add validation - required, 1-200 characters
    private String title;
    
    // TODO: Add validation - optional, max 1000 characters
    private String description;
    
    private boolean completed = false;
    
    // TODO: Add validation - required
    private Priority priority = Priority.MEDIUM;
    
    // TODO: Add Jackson date formatting
    private LocalDateTime dueDate;
    
    // TODO: Add Jackson date formatting
    private LocalDateTime createdAt;
    
    // TODO: Add Jackson date formatting
    private LocalDateTime updatedAt;
    
    // User who owns this todo (set from JWT token)
    private String userId;
    
    // TODO: Generate getters and setters for all fields
    
    // TODO: Consider adding a constructor that sets default values
    
    // TODO: Consider adding a builder pattern for easier testing
}