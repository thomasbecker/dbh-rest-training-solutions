package com.dbh.training.rest.models;

import com.dbh.training.rest.config.jackson.MoneyDeserializer;
import com.dbh.training.rest.config.jackson.MoneySerializer;
import com.dbh.training.rest.dto.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * User model with Bean Validation and Jackson annotations
 * 
 * Exercise 06: Jackson Basics
 * Exercise 07: Jackson Advanced - Added JSON Views and Money field
 * Domain model with Jackson annotations for JSON processing
 */
public class User {
    
    @JsonView(Views.Public.class)
    @JsonProperty("user_id")
    private Long id;
    
    @JsonView(Views.Public.class)
    @JsonProperty("user_name")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @JsonView(Views.Public.class)
    @JsonProperty("email_address")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @JsonView(Views.Public.class)
    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;
    
    @JsonView(Views.Public.class)
    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;
    
    @JsonIgnore
    private String passwordHash;
    
    @JsonView(Views.Internal.class)
    @JsonProperty("birth_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    @JsonView(Views.Internal.class)
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonView(Views.Internal.class)
    @JsonProperty("account_status")
    private AccountStatus status;
    
    @JsonView(Views.Internal.class)
    @JsonProperty("roles")
    private Set<String> roles = new HashSet<>();
    
    @JsonView(Views.Internal.class)
    @JsonProperty("primary_address")
    @Valid
    private Address primaryAddress;
    
    @JsonView(Views.Internal.class)
    @JsonProperty("billing_address")
    @Valid
    private Address billingAddress;
    
    @JsonView(Views.Admin.class)
    @JsonProperty("last_login")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLogin;
    
    @JsonView(Views.Internal.class)
    @JsonProperty("account_balance")
    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private Money accountBalance;
    
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
    
    public boolean checkPassword(String password) {
        return passwordHash != null && BCrypt.checkpw(password, passwordHash);
    }
    
    public void setPassword(String password) {
        this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
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
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public Money getAccountBalance() {
        return accountBalance;
    }
    
    public void setAccountBalance(Money accountBalance) {
        this.accountBalance = accountBalance;
    }
}