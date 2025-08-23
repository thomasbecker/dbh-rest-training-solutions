package com.dbh.training.rest.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account status enumeration
 * 
 * Exercise 06: Jackson Basics
 * Domain model enum with custom JSON representation.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AccountStatus {
    
    @JsonProperty("active")
    ACTIVE,
    
    @JsonProperty("suspended")
    SUSPENDED,
    
    @JsonProperty("pending")
    PENDING_VERIFICATION,
    
    @JsonProperty("deleted")
    DELETED;
    
    /**
     * Get a user-friendly display name for the status
     */
    public String getDisplayName() {
        switch (this) {
            case ACTIVE:
                return "Active";
            case SUSPENDED:
                return "Suspended";
            case PENDING_VERIFICATION:
                return "Pending Verification";
            case DELETED:
                return "Deleted";
            default:
                return this.name();
        }
    }
    
    /**
     * Parse a string value to AccountStatus
     * Handles both enum names and JSON property values
     */
    public static AccountStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        
        // Try direct enum name first
        try {
            return AccountStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try JSON property values
            switch (value.toLowerCase()) {
                case "active":
                    return ACTIVE;
                case "suspended":
                    return SUSPENDED;
                case "pending":
                    return PENDING_VERIFICATION;
                case "deleted":
                    return DELETED;
                default:
                    throw new IllegalArgumentException("Unknown AccountStatus: " + value);
            }
        }
    }
}