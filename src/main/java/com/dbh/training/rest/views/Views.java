package com.dbh.training.rest.views;

/**
 * Jackson JSON Views for controlling serialization.
 * 
 * Exercise 09: Used for Todo API to provide summary and detailed views
 */
public class Views {
    
    /**
     * Summary view - includes only essential fields
     * Used for list operations
     */
    public static class Summary {}
    
    /**
     * Detailed view - includes all fields
     * Used for single resource operations
     */
    public static class Detailed extends Summary {}
    
    /**
     * Public view - for external API consumers
     * Used in Exercise 07 for different serialization contexts
     */
    public static class Public {}
    
    /**
     * Internal view - for internal systems
     * Includes additional metadata
     */
    public static class Internal extends Public {}
    
    /**
     * Admin view - for administrative users
     * Includes sensitive information
     */
    public static class Admin extends Internal {}
}