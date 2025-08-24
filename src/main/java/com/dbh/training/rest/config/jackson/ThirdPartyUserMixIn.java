package com.dbh.training.rest.config.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mix-in class to control serialization of ThirdPartyUser.
 * 
 * Exercise 07: Jackson Advanced
 * Demonstrates how to modify JSON serialization of classes we don't own
 * using Jackson Mix-ins.
 */
public abstract class ThirdPartyUserMixIn {
    
    @JsonProperty("user_name")
    abstract String getUsername();
    
    @JsonIgnore  // Don't expose internal ID
    abstract String getInternalId();
    
    @JsonIgnore  // Never expose API key!
    abstract String getApiKey();
    
    @JsonProperty("email_address")
    abstract String getEmail();
}