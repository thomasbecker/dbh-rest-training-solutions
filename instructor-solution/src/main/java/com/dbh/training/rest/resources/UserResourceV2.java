package com.dbh.training.rest.resources;

import com.dbh.training.rest.models.UserV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UserResource Version 2 - Exercise 05 Solution
 * 
 * API Version 2 with breaking changes:
 * - Separate firstName and lastName fields (was combined in V1)
 * - Added optional age field
 * - No deprecation headers (this is the current version)
 */
@Path("/v2/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User Management V2", description = "Current version of user management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserResourceV2 extends AbstractResource {
    
    // Separate storage for V2 (in production, would share service layer)
    private static final Map<Long, UserV2> users = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    // Package-private method for test cleanup
    static void resetForTesting() {
        users.clear();
        idGenerator.set(1);
    }
    
    /**
     * GET /v2/users
     * Return all users in V2 format
     */
    @GET
    public Response getAllUsers() {
        List<UserV2> allUsers = new ArrayList<>(users.values());
        return ok(allUsers);
    }
    
    /**
     * GET /v2/users/{id}
     * Return specific user or 404
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        UserV2 user = users.get(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }
        return ok(user);
    }
    
    /**
     * POST /v2/users
     * Create new user with V2 structure
     * Now requires separate firstName and lastName
     */
    @POST
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with separate firstName and lastName fields (requires ADMIN role)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                schema = @Schema(implementation = UserV2.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role")
    })
    public Response createUser(@Valid UserV2 user) {
        // Generate ID and timestamp
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        
        // Store user
        users.put(id, user);
        
        // Return 201 with Location header
        return created(user, id);
    }
    
    /**
     * PUT /v2/users/{id}
     * Update existing user with V2 structure
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Update an existing user",
        description = "Updates user information (requires ADMIN role)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserV2.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role")
    })
    public Response updateUser(
        @Parameter(description = "User ID", required = true)
        @PathParam("id") Long id,
        @Parameter(description = "Updated user data", required = true)
        @Valid UserV2 user) {
        if (!users.containsKey(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }
        
        // Ensure ID consistency
        user.setId(id);
        users.put(id, user);
        
        return ok(user);
    }
    
    /**
     * DELETE /v2/users/{id}
     * Delete user, return 204 or 404
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Delete a user",
        description = "Removes a user from the system (requires ADMIN role)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role")
    })
    public Response deleteUser(
        @Parameter(description = "User ID to delete", required = true)
        @PathParam("id") Long id) {
        UserV2 removed = users.remove(id);
        if (removed == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }
        return noContent();
    }
    
    /**
     * Helper method for migration endpoint (bonus task)
     */
    protected List<UserV2> getAllUsersInternal() {
        return new ArrayList<>(users.values());
    }
    
    /**
     * Helper method for migration endpoint (bonus task)
     */
    protected void importUser(UserV2 user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        users.put(user.getId(), user);
    }
}