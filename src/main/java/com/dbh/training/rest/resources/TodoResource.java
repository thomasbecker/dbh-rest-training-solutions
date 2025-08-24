package com.dbh.training.rest.resources;

import com.dbh.training.rest.models.Todo;
import com.dbh.training.rest.models.Priority;
import com.dbh.training.rest.views.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * TodoResource - Exercise 09 Complete Solution
 * 
 * A comprehensive Todo REST API demonstrating:
 * - CRUD operations with proper HTTP status codes
 * - JWT-based security with user isolation
 * - Advanced filtering and search
 * - Custom Jackson serialization with views
 * - Admin capabilities
 * - OpenAPI documentation
 */
@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Todo Management", description = "Manage personal todo items")
@SecurityRequirement(name = "bearerAuth")
public class TodoResource extends AbstractResource {
    
    private static final Map<Long, Todo> todos = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Context
    private SecurityContext securityContext;
    
    @Context
    private UriInfo uriInfo;
    
    // Package-private for testing
    static void resetForTesting() {
        todos.clear();
        idGenerator.set(1);
    }
    
    /**
     * Extract userId from JWT token in SecurityContext
     */
    private String getCurrentUserId() {
        Principal principal = securityContext.getUserPrincipal();
        if (principal != null) {
            // For Exercise 09, we'll use the username as userId
            // In a real app, you'd cast to a custom principal with getUserId()
            return principal.getName();
        }
        throw new NotAuthorizedException("No authenticated user");
    }
    
    /**
     * GET /todos - List all todos for current user with optional filtering
     */
    @GET
    @JsonView(Views.Summary.class)
    @Operation(
        summary = "List todos",
        description = "Get all todos for the authenticated user with optional filtering"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of todos",
            content = @Content(schema = @Schema(implementation = Todo.class))
        ),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getAllTodos(
            @Parameter(description = "Filter by completion status")
            @QueryParam("completed") Boolean completed,
            
            @Parameter(description = "Filter by priority")
            @QueryParam("priority") Priority priority,
            
            @Parameter(description = "Filter todos due before this date (ISO format)")
            @QueryParam("dueBefore") String dueBefore,
            
            @Parameter(description = "Filter todos due after this date (ISO format)")
            @QueryParam("dueAfter") String dueAfter,
            
            @Parameter(description = "Search in title and description")
            @QueryParam("search") String search,
            
            @Parameter(description = "Sort field (title, priority, dueDate, createdAt)")
            @QueryParam("sort") @DefaultValue("createdAt") String sortField,
            
            @Parameter(description = "Sort order (asc, desc)")
            @QueryParam("order") @DefaultValue("desc") String sortOrder) {
        
        String userId = getCurrentUserId();
        
        // Filter todos by user
        List<Todo> userTodos = todos.values().stream()
                .filter(todo -> userId.equals(todo.getUserId()))
                .collect(Collectors.toList());
        
        // Apply filters
        userTodos = filterTodos(userTodos, completed, priority, 
                                parseDateOrNull(dueBefore), 
                                parseDateOrNull(dueAfter), 
                                search);
        
        // Sort results
        userTodos = sortTodos(userTodos, sortField, sortOrder);
        
        return ok(userTodos);
    }
    
    /**
     * GET /todos/{id} - Get specific todo
     */
    @GET
    @Path("/{id}")
    @JsonView(Views.Detailed.class)
    @Operation(
        summary = "Get todo by ID",
        description = "Get detailed information about a specific todo"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Todo found",
            content = @Content(schema = @Schema(implementation = Todo.class))
        ),
        @ApiResponse(responseCode = "404", description = "Todo not found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getTodoById(
            @Parameter(description = "Todo ID", required = true)
            @PathParam("id") Long id) {
        
        String userId = getCurrentUserId();
        Todo todo = todos.get(id);
        
        // Return 404 if not found OR belongs to another user (security through obscurity)
        if (todo == null || !userId.equals(todo.getUserId())) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Todo not found")
                    .build();
        }
        
        return ok(todo);
    }
    
    /**
     * POST /todos - Create new todo
     */
    @POST
    @Operation(
        summary = "Create todo",
        description = "Create a new todo item"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Todo created",
            content = @Content(schema = @Schema(implementation = Todo.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response createTodo(
            @Parameter(description = "Todo to create", required = true)
            @Valid Todo todo) {
        
        String userId = getCurrentUserId();
        
        // Set system-managed fields
        Long id = idGenerator.getAndIncrement();
        todo.setId(id);
        todo.setUserId(userId);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        
        // Store todo
        todos.put(id, todo);
        
        // Build location URI
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(id))
                .build();
        
        return Response.created(location)
                .entity(todo)
                .build();
    }
    
    /**
     * PUT /todos/{id} - Update existing todo
     */
    @PUT
    @Path("/{id}")
    @Operation(
        summary = "Update todo",
        description = "Update an existing todo item"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Todo updated",
            content = @Content(schema = @Schema(implementation = Todo.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Todo not found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response updateTodo(
            @Parameter(description = "Todo ID", required = true)
            @PathParam("id") Long id,
            
            @Parameter(description = "Updated todo data", required = true)
            @Valid Todo todo) {
        
        String userId = getCurrentUserId();
        Todo existing = todos.get(id);
        
        // Check if exists and belongs to user
        if (existing == null || !userId.equals(existing.getUserId())) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Todo not found")
                    .build();
        }
        
        // Update fields (preserve system-managed fields)
        todo.setId(id);
        todo.setUserId(userId);
        todo.setCreatedAt(existing.getCreatedAt());
        todo.setUpdatedAt(LocalDateTime.now());
        
        todos.put(id, todo);
        
        return ok(todo);
    }
    
    /**
     * DELETE /todos/{id} - Delete todo
     */
    @DELETE
    @Path("/{id}")
    @Operation(
        summary = "Delete todo",
        description = "Delete a todo item"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Todo deleted"),
        @ApiResponse(responseCode = "404", description = "Todo not found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response deleteTodo(
            @Parameter(description = "Todo ID", required = true)
            @PathParam("id") Long id) {
        
        String userId = getCurrentUserId();
        Todo todo = todos.get(id);
        
        // Check if exists and belongs to user
        if (todo == null || !userId.equals(todo.getUserId())) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Todo not found")
                    .build();
        }
        
        todos.remove(id);
        return noContent();
    }
    
    /**
     * PATCH /todos/{id}/complete - Toggle completion status
     */
    @PATCH
    @Path("/{id}/complete")
    @Operation(
        summary = "Toggle todo completion",
        description = "Toggle the completion status of a todo"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Todo completion toggled",
            content = @Content(schema = @Schema(implementation = Todo.class))
        ),
        @ApiResponse(responseCode = "404", description = "Todo not found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response toggleComplete(
            @Parameter(description = "Todo ID", required = true)
            @PathParam("id") Long id) {
        
        String userId = getCurrentUserId();
        Todo todo = todos.get(id);
        
        // Check if exists and belongs to user
        if (todo == null || !userId.equals(todo.getUserId())) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Todo not found")
                    .build();
        }
        
        // Toggle completion
        todo.setCompleted(!todo.isCompleted());
        todo.setUpdatedAt(LocalDateTime.now());
        
        return ok(todo);
    }
    
    /**
     * GET /admin/todos - Admin only: List ALL todos from all users
     */
    @GET
    @Path("/admin/todos")
    @RolesAllowed("ADMIN")
    @JsonView(Views.Admin.class)
    @Operation(
        summary = "List all todos (Admin)",
        description = "Get all todos from all users (requires ADMIN role)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of all todos",
            content = @Content(schema = @Schema(implementation = Todo.class))
        ),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public Response getAllTodosAdmin() {
        List<Todo> allTodos = new ArrayList<>(todos.values());
        return ok(allTodos);
    }
    
    /**
     * GET /admin/todos/stats - Admin only: Get statistics
     */
    @GET
    @Path("/admin/todos/stats")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Get todo statistics (Admin)",
        description = "Get statistics about todos (requires ADMIN role)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistics retrieved"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public Response getTodoStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Todo> allTodos = new ArrayList<>(todos.values());
        
        stats.put("totalTodos", allTodos.size());
        stats.put("completedTodos", allTodos.stream().filter(Todo::isCompleted).count());
        stats.put("pendingTodos", allTodos.stream().filter(t -> !t.isCompleted()).count());
        
        // Count by priority
        Map<Priority, Long> byPriority = allTodos.stream()
                .collect(Collectors.groupingBy(Todo::getPriority, Collectors.counting()));
        stats.put("byPriority", byPriority);
        
        // Count by user
        Map<String, Long> byUser = allTodos.stream()
                .collect(Collectors.groupingBy(Todo::getUserId, Collectors.counting()));
        stats.put("byUser", byUser);
        
        // Overdue todos
        long overdue = allTodos.stream()
                .filter(t -> !t.isCompleted())
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getDueDate().isBefore(LocalDateTime.now()))
                .count();
        stats.put("overdueTodos", overdue);
        
        return ok(stats);
    }
    
    /**
     * Utility method for filtering todos
     */
    private List<Todo> filterTodos(List<Todo> todos,
                                   Boolean completed,
                                   Priority priority,
                                   LocalDateTime dueBefore,
                                   LocalDateTime dueAfter,
                                   String search) {
        
        return todos.stream()
                .filter(todo -> completed == null || todo.isCompleted() == completed)
                .filter(todo -> priority == null || todo.getPriority() == priority)
                .filter(todo -> dueBefore == null || 
                        (todo.getDueDate() != null && todo.getDueDate().isBefore(dueBefore)))
                .filter(todo -> dueAfter == null || 
                        (todo.getDueDate() != null && todo.getDueDate().isAfter(dueAfter)))
                .filter(todo -> search == null || search.isEmpty() ||
                        (todo.getTitle() != null && 
                         todo.getTitle().toLowerCase().contains(search.toLowerCase())) ||
                        (todo.getDescription() != null && 
                         todo.getDescription().toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());
    }
    
    /**
     * Utility method for sorting todos
     */
    private List<Todo> sortTodos(List<Todo> todos, String sortField, String sortOrder) {
        Comparator<Todo> comparator = null;
        
        switch (sortField.toLowerCase()) {
            case "title":
                comparator = Comparator.comparing(Todo::getTitle, 
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            case "priority":
                comparator = Comparator.comparing(Todo::getPriority);
                break;
            case "duedate":
                comparator = Comparator.comparing(Todo::getDueDate, 
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "createdat":
            default:
                comparator = Comparator.comparing(Todo::getCreatedAt, 
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;
        }
        
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        
        todos.sort(comparator);
        return todos;
    }
    
    /**
     * Utility method to parse date string or return null
     */
    private LocalDateTime parseDateOrNull(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            // Invalid date format, return null to ignore filter
            return null;
        }
    }
}