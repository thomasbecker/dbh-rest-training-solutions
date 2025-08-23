package com.dbh.training.rest.resources;

import com.dbh.training.rest.dto.Views;
import com.dbh.training.rest.models.Money;
import com.dbh.training.rest.models.User;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Solution for Exercise 04: Bean Validation
 * 
 * CRUD operations with validation using @Valid annotation.
 * This is what students should have after completing Exercise 04.
 */
@Path("/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResourceV1 extends AbstractResource {
    
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
     * Requires USER or ADMIN role
     */
    @GET
    @RolesAllowed({"USER", "ADMIN"})
    public Response getAllUsers() {
        List<User> allUsers = new ArrayList<>(users.values());
        return Response.ok(allUsers)
            .header("Sunset", "31 Dec 2024")
            .header("Deprecation", "true")
            .header("Link", "</api/v2/users>; rel=\"successor-version\"")
            .build();
    }
    
    /**
     * GET /users/{id}
     * Return specific user or 404
     * Requires USER or ADMIN role
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response getUserById(@PathParam("id") Long id) {
        User user = users.get(id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        return ok(user);
    }
    
    /**
     * POST /users
     * Create new user with generated ID and validation
     * Return 201 with Location header
     * Requires ADMIN role
     */
    @POST
    @RolesAllowed("ADMIN")
    public Response createUser(@Valid User user) {
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
     * Update existing user with validation or return 404
     * Requires ADMIN role
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response updateUser(@PathParam("id") Long id, @Valid User user) {
        // Check if user exists
        if (!users.containsKey(id)) {
            return Response.status(404).entity("User not found").build();
        }
        
        // Preserve the ID and update
        user.setId(id);
        users.put(id, user);
        
        return ok(user);
    }
    
    /**
     * DELETE /users/{id}
     * Delete user, return 204 or 404
     * Requires ADMIN role
     */
    @DELETE
    @RolesAllowed("ADMIN")
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        User removed = users.remove(id);
        if (removed == null) {
            return Response.status(404).entity("User not found").build();
        }
        // Using helper method from AbstractResource
        return noContent();
    }
    
    // ===== Exercise 07: Jackson Advanced - JSON Views =====
    
    /**
     * GET /users/{id}/public
     * Public view - minimal fields for unauthenticated users
     */
    @GET
    @Path("/{id}/public")
    @JsonView(Views.Public.class)
    public Response getPublicUser(@PathParam("id") Long id) {
        User user = users.get(id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        return Response.ok(user).build();
    }
    
    /**
     * GET /users/{id}/details
     * Internal view - more fields for authenticated users
     */
    @GET
    @Path("/{id}/details")
    @JsonView(Views.Internal.class)
    public Response getInternalUser(@PathParam("id") Long id) {
        User user = users.get(id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        // In real app, check auth: @RolesAllowed("USER")
        return Response.ok(user).build();
    }
    
    /**
     * GET /users/{id}/admin
     * Admin view - all fields except password
     */
    @GET
    @Path("/{id}/admin")
    @JsonView(Views.Admin.class)
    @RolesAllowed("ADMIN")
    public Response getAdminUser(@PathParam("id") Long id) {
        User user = users.get(id);
        if (user == null) {
            return Response.status(404).entity("User not found").build();
        }
        return Response.ok(user).build();
    }
    
    // ===== Exercise 07: Jackson Advanced - Streaming =====
    
    /**
     * GET /users/stream
     * Stream all users efficiently for large datasets
     */
    @GET
    @Path("/stream")
    @Produces(MediaType.APPLICATION_JSON)
    public StreamingOutput streamAllUsers() {
        return output -> {
            JsonFactory jsonFactory = new JsonFactory();
            try (JsonGenerator gen = jsonFactory.createGenerator(output)) {
                gen.writeStartArray();
                
                // Stream each user one by one
                for (User user : users.values()) {
                    gen.writeStartObject();
                    gen.writeNumberField("user_id", user.getId());
                    gen.writeStringField("user_name", user.getUsername());
                    gen.writeStringField("email_address", user.getEmail());
                    gen.writeStringField("first_name", user.getFirstName());
                    gen.writeStringField("last_name", user.getLastName());
                    gen.writeEndObject();
                }
                
                gen.writeEndArray();
                gen.flush();
            } catch (IOException e) {
                throw new WebApplicationException("Error streaming users", e);
            }
        };
    }
    
    /**
     * POST /users/test-money
     * Test endpoint for Money serialization
     */
    @POST
    @Path("/test-money")
    public Response createUserWithMoney(@Valid User user) {
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        
        // Add sample money balance for testing
        user.setAccountBalance(new Money(
            new BigDecimal("1250.50"), 
            Currency.getInstance("EUR")
        ));
        
        users.put(id, user);
        return created(user, id);
    }
}