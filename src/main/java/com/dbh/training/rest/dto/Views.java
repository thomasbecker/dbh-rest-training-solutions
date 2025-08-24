package com.dbh.training.rest.dto;

/**
 * JSON View definitions for role-based field visibility.
 * 
 * Exercise 07: Jackson Advanced
 * Defines hierarchical views: Admin > Internal > Public
 */
public class Views {
    /**
     * Public view - minimal fields for unauthenticated users
     */
    public static class Public { }
    
    /**
     * Internal view - extends Public, for authenticated users
     */
    public static class Internal extends Public { }
    
    /**
     * Admin view - extends Internal, for administrators only
     */
    public static class Admin extends Internal { }
}