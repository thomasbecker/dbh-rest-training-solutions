package com.dbh.training.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Address model for nested object demonstration.
 * 
 * Exercise 06: Jackson Basics
 * Domain model representing a physical address with Jackson annotations.
 */
public class Address {
    
    @JsonProperty("street_line_1")
    @NotBlank(message = "Street line 1 is required")
    private String streetLine1;
    
    @JsonProperty("street_line_2")
    private String streetLine2;
    
    @JsonProperty("city")
    @NotBlank(message = "City is required")
    private String city;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("postal_code")
    @NotBlank(message = "Postal code is required")
    private String postalCode;
    
    @JsonProperty("country_code")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    private String countryCode;
    
    // Default constructor
    public Address() {
    }
    
    // Constructor with required fields
    public Address(String streetLine1, String city, String postalCode, String countryCode) {
        this.streetLine1 = streetLine1;
        this.city = city;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }
    
    // Full constructor
    public Address(String streetLine1, String streetLine2, String city, 
                   String state, String postalCode, String countryCode) {
        this.streetLine1 = streetLine1;
        this.streetLine2 = streetLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }
    
    // Getters and Setters
    public String getStreetLine1() {
        return streetLine1;
    }
    
    public void setStreetLine1(String streetLine1) {
        this.streetLine1 = streetLine1;
    }
    
    public String getStreetLine2() {
        return streetLine2;
    }
    
    public void setStreetLine2(String streetLine2) {
        this.streetLine2 = streetLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}