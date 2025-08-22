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
 * Solution for Exercise 03: Jersey CRUD
 * 
 * Basic CRUD operations with JAX-RS annotations.
 * This is what students should have after completing Exercise 03.
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    @GET
    public Response getAllUsers() {
        return ok(new ArrayList<>(users.values()));
    }
    
    /**
     * GET /users/{id}
     * Return specific user or 404
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        User user = users.get(id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        return ok(user);
    }
    
    /**
     * POST /users
     * Create new user with generated ID
     * Return 201 with Location header
     */
    @POST
    public Response createUser(User user) {
        // Generate ID and set creation timestamp
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        
        // Store the user
        users.put(id, user);
        
        // Return 201 Created with location header and entity
        // Using helper method from AbstractResource for dynamic URI building
        return created(user, id);
    }
    
    /**
     * PUT /users/{id}
     * Update existing user or return 404
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        // Check if user exists
        if (!users.containsKey(id)) {
            return Response.status(404).entity("User not found").build();
        }
        
        // Ensure ID matches
        user.setId(id);
        
        // Update the user
        users.put(id, user);
        
        return ok(user);
    }
    
    /**
     * DELETE /users/{id}
     * Delete user, return 204 or 404
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        User removed = users.remove(id);
        if (removed == null) {
            return Response.status(404).entity("User not found").build();
        }
        // Using helper method from AbstractResource
        return noContent();
    }
}