package com.dbh.training.rest.security;

import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.List;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT Authentication Filter
 * 
 * Exercise 08: Security Implementation
 * Intercepts requests to validate JWT tokens
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Inject
    private JwtService jwtService;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Skip authentication for certain paths
        String path = requestContext.getUriInfo().getPath();
        if (isPublicPath(path)) {
            return;
        }
        
        // Extract token from Authorization header
        String authHeader = requestContext.getHeaderString(AUTHORIZATION_HEADER);
        
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length()).trim();
            
            try {
                // Validate token and extract claims
                Claims claims = jwtService.validateToken(token);
                
                String username = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);
                
                // Create and set security context
                UserSecurityContext securityContext = new UserSecurityContext(
                    username, userId, roles, requestContext.getSecurityContext()
                );
                
                requestContext.setSecurityContext(securityContext);
                
                logger.debug("Authenticated user: {} with roles: {}", username, roles);
                
            } catch (Exception e) {
                logger.warn("Invalid JWT token: {}", e.getMessage());
                // Request continues without authentication
                // @RolesAllowed will handle authorization
            }
        }
    }
    
    /**
     * Check if path should skip authentication
     */
    private boolean isPublicPath(String path) {
        // Allow access to login and registration endpoints
        return path.startsWith("auth/") || 
               path.equals("health") ||
               path.equals("") ||
               path.equals("/");
    }
}