package com.dbh.training.rest.resources;

import com.dbh.training.rest.dto.ErrorResponse;
import com.dbh.training.rest.dto.LoginRequest;
import com.dbh.training.rest.dto.TokenResponse;
import com.dbh.training.rest.models.User;
import com.dbh.training.rest.security.JwtService;
import com.dbh.training.rest.services.UserService;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication Resource
 * 
 * Exercise 08: Security Implementation
 * Handles login and token generation
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthResource.class);
    
    @Inject
    private UserService userService;
    
    @Inject
    private JwtService jwtService;
    
    /**
     * Login endpoint - authenticates user and returns JWT token
     */
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());
        
        try {
            // Find user by username
            User user = userService.findByUsername(request.getUsername());
            
            if (user != null && user.checkPassword(request.getPassword())) {
                // Generate JWT token
                String token = jwtService.generateToken(user);
                
                logger.info("Successful login for user: {}", request.getUsername());
                
                return Response.ok(new TokenResponse(token))
                              .build();
            }
            
            logger.warn("Failed login attempt for user: {}", request.getUsername());
            
            // Generic error message for security
            return Response.status(Response.Status.UNAUTHORIZED)
                          .entity(new ErrorResponse("Invalid credentials"))
                          .build();
                          
        } catch (Exception e) {
            logger.error("Error during login", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Authentication failed"))
                          .build();
        }
    }
    
    /**
     * Registration endpoint (optional)
     */
    @POST
    @Path("/register")
    public Response register(@Valid User user) {
        logger.info("Registration attempt for username: {}", user.getUsername());
        
        try {
            // Check if username already exists
            if (userService.findByUsername(user.getUsername()) != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity(new ErrorResponse("Username already exists"))
                              .build();
            }
            
            // Create new user
            User created = userService.createUser(user);
            
            // Generate token for immediate login
            String token = jwtService.generateToken(created);
            
            logger.info("Successful registration for user: {}", user.getUsername());
            
            return Response.status(Response.Status.CREATED)
                          .entity(new TokenResponse(token))
                          .build();
                          
        } catch (Exception e) {
            logger.error("Error during registration", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Registration failed"))
                          .build();
        }
    }
    
    /**
     * Health check endpoint (no auth required)
     */
    @GET
    @Path("/health")
    public Response health() {
        return Response.ok("{\"status\":\"healthy\"}")
                      .build();
    }
}