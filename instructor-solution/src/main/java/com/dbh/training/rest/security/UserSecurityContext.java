package com.dbh.training.rest.security;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.SecurityContext;

/**
 * Custom SecurityContext for authenticated users
 * 
 * Exercise 08: Security Implementation
 * Provides user principal and role checking
 */
public class UserSecurityContext implements SecurityContext {
    
    private final String username;
    private final Long userId;
    private final Set<String> roles;
    private final SecurityContext originalContext;
    private final boolean secure;
    
    public UserSecurityContext(String username, Long userId, List<String> roles, 
                               SecurityContext originalContext) {
        this.username = username;
        this.userId = userId;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.originalContext = originalContext;
        this.secure = originalContext != null ? originalContext.isSecure() : false;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return username;
            }
            
            public Long getUserId() {
                return userId;
            }
        };
    }
    
    @Override
    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }
    
    @Override
    public boolean isSecure() {
        return secure;
    }
    
    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
    
    public String getUsername() {
        return username;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Set<String> getRoles() {
        return new HashSet<>(roles);
    }
}