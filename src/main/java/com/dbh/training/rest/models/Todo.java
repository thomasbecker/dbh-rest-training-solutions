package com.dbh.training.rest.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.dbh.training.rest.views.Views;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Todo model for Exercise 09 - Complete Solution
 * 
 * Demonstrates:
 * - Bean Validation
 * - Jackson annotations
 * - JSON Views
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Todo {
    
    @JsonView(Views.Summary.class)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @JsonView(Views.Summary.class)
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @JsonView(Views.Detailed.class)
    private String description;
    
    @JsonView(Views.Summary.class)
    private boolean completed = false;
    
    @NotNull(message = "Priority is required")
    @JsonView(Views.Summary.class)
    private Priority priority = Priority.MEDIUM;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonView(Views.Detailed.class)
    private LocalDateTime dueDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonView(Views.Detailed.class)
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonView(Views.Detailed.class)
    private LocalDateTime updatedAt;
    
    @JsonView(Views.Detailed.class)
    private String userId;
    
    // Constructors
    public Todo() {
    }
    
    public Todo(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
    
    // Builder pattern for testing
    public static class Builder {
        private Todo todo = new Todo();
        
        public Builder title(String title) {
            todo.title = title;
            return this;
        }
        
        public Builder description(String description) {
            todo.description = description;
            return this;
        }
        
        public Builder priority(Priority priority) {
            todo.priority = priority;
            return this;
        }
        
        public Builder dueDate(LocalDateTime dueDate) {
            todo.dueDate = dueDate;
            return this;
        }
        
        public Builder completed(boolean completed) {
            todo.completed = completed;
            return this;
        }
        
        public Builder userId(String userId) {
            todo.userId = userId;
            return this;
        }
        
        public Todo build() {
            return todo;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}