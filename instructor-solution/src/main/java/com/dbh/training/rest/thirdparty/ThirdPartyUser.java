package com.dbh.training.rest.thirdparty;

/**
 * Example third-party class we can't modify.
 * 
 * Exercise 07: Jackson Advanced
 * Simulates a library class that we need to control serialization for
 * but cannot modify directly.
 */
public class ThirdPartyUser {
    private String username;
    private String internalId;
    private String apiKey;
    private String email;
    
    public ThirdPartyUser() {
    }
    
    public ThirdPartyUser(String username, String internalId, String apiKey, String email) {
        this.username = username;
        this.internalId = internalId;
        this.apiKey = apiKey;
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getInternalId() {
        return internalId;
    }
    
    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}