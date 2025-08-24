package com.dbh.training.rest.security;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Security Headers Filter
 * 
 * Exercise 08: Security Implementation
 * Adds security headers to all responses
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class SecurityHeadersFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext, 
                      ContainerResponseContext responseContext) throws IOException {
        
        // Prevent MIME type sniffing
        responseContext.getHeaders().add("X-Content-Type-Options", "nosniff");
        
        // Prevent clickjacking
        responseContext.getHeaders().add("X-Frame-Options", "DENY");
        
        // Enable XSS protection
        responseContext.getHeaders().add("X-XSS-Protection", "1; mode=block");
        
        // Force HTTPS (in production)
        // Uncomment for production with HTTPS
        // responseContext.getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        
        // Content Security Policy
        responseContext.getHeaders().add("Content-Security-Policy", "default-src 'self'");
        
        // Referrer Policy
        responseContext.getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Cache control for sensitive data
        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("users") || path.startsWith("auth")) {
            responseContext.getHeaders().add("Cache-Control", "no-store, no-cache, must-revalidate");
            responseContext.getHeaders().add("Pragma", "no-cache");
            responseContext.getHeaders().add("Expires", "0");
        }
    }
}