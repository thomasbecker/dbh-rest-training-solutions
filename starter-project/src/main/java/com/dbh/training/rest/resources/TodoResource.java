package com.dbh.training.rest.resources;

import com.dbh.training.rest.models.Todo;
import com.dbh.training.rest.models.Priority;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * TodoResource - Exercise 09 Comprehensive Implementation
 * 
 * TODO Exercise 09: Implement a complete Todo REST API with:
 * - CRUD operations
 * - Security (user isolation)
 * - Filtering and search
 * - Custom Jackson serialization
 * - Admin capabilities
 */
@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// TODO Task 2.1: Add OpenAPI @Tag annotation
public class TodoResource {
    
    private static final Map<Long, Todo> todos = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    @Context
    private SecurityContext securityContext;
    
    /**
     * TODO Task 3.1: Implement this method to extract userId from JWT token
     * Hint: Cast securityContext.getUserPrincipal() to JwtPrincipal
     */
    private String getCurrentUserId() {
        // TODO: Extract userId from JWT token in SecurityContext
        throw new UnsupportedOperationException("Task 3.1: Implement getCurrentUserId()");
    }
    
    /**
     * GET /todos - List all todos for current user
     * 
     * TODO Task 2.1: Implement basic listing
     * TODO Task 2.2: Add query parameter filtering:
     *   - @QueryParam("completed") Boolean completed
     *   - @QueryParam("priority") Priority priority  
     *   - @QueryParam("dueBefore") String dueBefore
     *   - @QueryParam("dueAfter") String dueAfter
     *   - @QueryParam("search") String search
     * TODO Task 4.2: Apply @JsonView(Views.Summary.class)
     */
    @GET
    public Response getAllTodos() {
        // TODO: Get current user ID
        // TODO: Filter todos by userId
        // TODO: Apply query parameter filters if provided
        // TODO: Return filtered list
        
        return Response.ok(Collections.emptyList()).build();
    }
    
    /**
     * GET /todos/{id} - Get specific todo
     * 
     * TODO Task 2.1: Implement
     * TODO Task 3.1: Ensure user can only access their own todos
     * TODO Task 4.2: Apply @JsonView(Views.Detailed.class)
     */
    @GET
    @Path("/{id}")
    public Response getTodoById(@PathParam("id") Long id) {
        // TODO: Find todo by ID
        // TODO: Check if todo belongs to current user
        // TODO: Return 404 if not found OR belongs to another user
        
        throw new UnsupportedOperationException("Task 2.1: Implement getTodoById");
    }
    
    /**
     * POST /todos - Create new todo
     * 
     * TODO Task 2.1: Implement
     * TODO Task 3.1: Set userId from JWT token
     * TODO Task 5.2: Add @Valid parameter annotation
     */
    @POST
    public Response createTodo(/* TODO: Add @Valid */ Todo todo) {
        // TODO: Set todo ID (use idGenerator)
        // TODO: Set userId from current user
        // TODO: Set createdAt and updatedAt to now
        // TODO: Store in map
        // TODO: Return 201 with Location header
        
        throw new UnsupportedOperationException("Task 2.1: Implement createTodo");
    }
    
    /**
     * PUT /todos/{id} - Update existing todo
     * 
     * TODO Task 2.1: Implement
     * TODO Task 3.1: Ensure user can only update their own todos
     * TODO Task 5.2: Add @Valid parameter annotation
     */
    @PUT
    @Path("/{id}")
    public Response updateTodo(@PathParam("id") Long id, /* TODO: Add @Valid */ Todo todo) {
        // TODO: Find existing todo
        // TODO: Check if todo belongs to current user
        // TODO: Update fields (preserve id, userId, createdAt)
        // TODO: Set updatedAt to now
        // TODO: Return updated todo or 404
        
        throw new UnsupportedOperationException("Task 2.1: Implement updateTodo");
    }
    
    /**
     * DELETE /todos/{id} - Delete todo
     * 
     * TODO Task 2.1: Implement
     * TODO Task 3.1: Ensure user can only delete their own todos
     */
    @DELETE
    @Path("/{id}")
    public Response deleteTodo(@PathParam("id") Long id) {
        // TODO: Find todo
        // TODO: Check if todo belongs to current user
        // TODO: Remove from map
        // TODO: Return 204 or 404
        
        throw new UnsupportedOperationException("Task 2.1: Implement deleteTodo");
    }
    
    /**
     * PATCH /todos/{id}/complete - Toggle completion status
     * 
     * TODO Task 2.1: Implement
     * TODO Task 3.1: Ensure user can only modify their own todos
     */
    @PATCH
    @Path("/{id}/complete")
    public Response toggleComplete(@PathParam("id") Long id) {
        // TODO: Find todo
        // TODO: Check if todo belongs to current user
        // TODO: Toggle completed status
        // TODO: Set updatedAt to now
        // TODO: Return updated todo or 404
        
        throw new UnsupportedOperationException("Task 2.1: Implement toggleComplete");
    }
    
    /**
     * Utility method for filtering todos
     * 
     * TODO Task 2.2: Implement filtering logic
     */
    private List<Todo> filterTodos(List<Todo> todos,
                                   Boolean completed,
                                   Priority priority,
                                   LocalDateTime dueBefore,
                                   LocalDateTime dueAfter,
                                   String search) {
        // TODO: Use Java 8 streams to filter todos based on parameters
        // TODO: Only apply filters if parameters are not null
        // TODO: For search, check if title or description contains the text (case-insensitive)
        
        return todos;
    }
}