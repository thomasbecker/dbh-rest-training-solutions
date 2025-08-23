package com.dbh.training.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Error response DTO
 * 
 * Exercise 08: Security Implementation
 */
public class ErrorResponse {
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("status")
    private int status;
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(String error) {
        this.error = error;
        this.message = error;
    }
    
    public ErrorResponse(String error, int status) {
        this.error = error;
        this.message = error;
        this.status = status;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}