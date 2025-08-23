package com.dbh.training.rest.models;

import java.time.LocalDateTime;

/**
 * User entity representing a system user.
 * 
 * Exercise 02: Complete this model with proper fields and methods
 */
public class User {
    
    // Basic fields needed for Exercise 02
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    
    // Default constructor (required for JSON deserialization)
    public User() {}
    
    // Getters and Setters (provided to save time)
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getFirstName() { 
        return firstName; 
    }
    
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }
    
    public String getLastName() { 
        return lastName; 
    }
    
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    // TODO: Exercise 04 - Add Bean Validation annotations:
    // - username: @NotBlank, @Size(min = 3, max = 50)
    // - email: @NotBlank, @Email
    // - firstName: @NotBlank, @Size(max = 50)
    // - lastName: @NotBlank, @Size(max = 50)
    
    // TODO: Exercise 06 - Add Jackson annotations:
    // - @JsonIgnore for sensitive fields
    // - @JsonProperty for custom naming
    // - @JsonFormat for date formatting
}