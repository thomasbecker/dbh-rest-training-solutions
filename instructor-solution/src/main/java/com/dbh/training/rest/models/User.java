package com.dbh.training.rest.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * User model with Bean Validation and Jackson annotations
 * 
 * Exercise 06: Jackson Basics
 * Domain model with Jackson annotations for JSON processing
 */
public class User {
    
    @JsonProperty("user_id")
    private Long id;
    
    @JsonProperty("user_name")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @JsonProperty("email_address")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;
    
    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;
    
    @JsonIgnore
    private String password;
    
    @JsonProperty("birth_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("account_status")
    private AccountStatus status;
    
    @JsonProperty("roles")
    private List<String> roles;
    
    @JsonProperty("primary_address")
    @Valid
    private Address primaryAddress;
    
    @JsonProperty("billing_address")
    @Valid
    private Address billingAddress;
    
    // Default constructor (required for Jackson)
    public User() {
    }
    
    // Constructor with all fields except id and timestamp
    public User(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters and Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public AccountStatus getStatus() {
        return status;
    }
    
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public Address getPrimaryAddress() {
        return primaryAddress;
    }
    
    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }
    
    public Address getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }
}