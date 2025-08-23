package com.dbh.training.rest.resources;

import com.dbh.training.rest.models.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Exercise 02: Jersey CRUD
 * 
 * TODO: Make the REST Assured tests pass!
 * Run tests with: ./gradlew test --tests UserResourceTest
 * 
 * JAX-RS Documentation:
 * https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html
 * 
 * Required: Class-level annotations for path and content type
 */
// TODO: Add annotations
public class UserResource extends AbstractResource {
    
    // Thread-safe storage for users
    private static final Map<Long, User> users = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    // Package-private method for test cleanup (only accessible from same package)
    // This pattern prevents production code misuse while allowing test access
    static void resetForTesting() {
        users.clear();
        idGenerator.set(1);
    }
    
    /**
     * GET /users
     * Return all users
     */
    public Response getAllUsers() {
        // TODO: Add annotation and implement
        // Hint: Use the ok() helper method from AbstractResource
        return Response.status(501).entity("Not implemented yet").build();
    }
    
    /**
     * GET /users/{id}
     * Return specific user or 404
     */
    public Response getUserById(Long id) {
        // TODO: Add annotations and implement
        // Hint: Return 404 with message "User not found" if user doesn't exist
        // Hint: Use the ok() helper method for successful response
        return Response.status(501).entity("Not implemented yet").build();
    }
    
    /**
     * POST /users
     * Create new user with generated ID
     * Return 201 with Location header
     * TODO Exercise 04: Add @Valid annotation to enable validation
     */
    public Response createUser(User user) {
        // TODO: Add annotation and implement
        // Hint: Use idGenerator.getAndIncrement() for ID
        // Hint: Set user.setCreatedAt(LocalDateTime.now())
        // Hint: Use the created(entity, id) helper method from AbstractResource
        // TODO Exercise 04: Add @Valid before User parameter
        return Response.status(501).entity("Not implemented yet").build();
    }
    
    /**
     * PUT /users/{id}
     * Update existing user or return 404
     * TODO Exercise 04: Add @Valid annotation to enable validation
     */
    public Response updateUser(Long id, User user) {
        // TODO: Add annotations and implement
        // Hint: Return 404 with message "User not found" if user doesn't exist
        // Hint: Use the ok() helper method for successful response
        // TODO Exercise 04: Add @Valid before User parameter
        return Response.status(501).entity("Not implemented yet").build();
    }
    
    /**
     * DELETE /users/{id}
     * Delete user, return 204 or 404
     */
    public Response deleteUser(Long id) {
        // TODO: Add annotations and implement
        // Hint: Return 404 with message "User not found" if user doesn't exist
        // Hint: Use the noContent() helper method for successful deletion
        return Response.status(501).entity("Not implemented yet").build();
    }
}
