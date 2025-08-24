package com.dbh.training.rest.resources;

import com.dbh.training.rest.models.Todo;
import com.dbh.training.rest.models.Priority;
import com.dbh.training.rest.test.BaseIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TodoResource - Exercise 09 Solution
 * 
 * Tests comprehensive Todo API functionality including:
 * - CRUD operations
 * - Security (JWT authentication and user isolation)
 * - Filtering and search
 * - Validation
 * - Admin capabilities
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TodoResourceTest extends BaseIntegrationTest {
    
    private static Long createdTodoId;
    private static Long userTodoId;
    private static Long adminTodoId;
    
    @BeforeAll
    public static void setupTodos() {
        // Reset todo storage
        TodoResource.resetForTesting();
    }
    
    @BeforeEach
    public void setUp() {
        RestAssured.port = TEST_PORT;
    }
    
    @Test
    @Order(1)
    @DisplayName("Should require authentication")
    public void testRequiresAuthentication() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/todos")
        .then()
            .statusCode(401);
    }
    
    @Test
    @Order(2)
    @DisplayName("Should create todo with valid data")
    public void testCreateTodo() {
        Map<String, Object> todo = new HashMap<>();
        todo.put("title", "Complete REST training");
        todo.put("description", "Finish all exercises and tests");
        todo.put("priority", "HIGH");
        todo.put("dueDate", "2025-08-30T17:00:00");
        
        Response response = given()
            .header("Authorization", "Bearer " + userToken)
            .contentType(ContentType.JSON)
            .body(todo)
        .when()
            .post("/api/todos")
        .then()
            .statusCode(201)
            .header("Location", notNullValue())
            .body("id", notNullValue())
            .body("title", equalTo("Complete REST training"))
            .body("priority.value", equalTo("HIGH"))
            .body("priority.level", equalTo(3))
            .body("priority.color", equalTo("#FF0000"))
            .body("completed", equalTo(false))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue())
            .extract().response();
        
        createdTodoId = response.jsonPath().getLong("id");
        userTodoId = createdTodoId;
        
        // Verify Location header
        String location = response.getHeader("Location");
        assertTrue(location.endsWith("/api/todos/" + createdTodoId));
    }
    
    @Test
    @Order(3)
    @DisplayName("Should validate required fields")
    public void testValidation() {
        Map<String, Object> invalidTodo = new HashMap<>();
        invalidTodo.put("description", "Missing title");
        
        given()
            .header("Authorization", "Bearer " + userToken)
            .contentType(ContentType.JSON)
            .body(invalidTodo)
        .when()
            .post("/api/todos")
        .then()
            .statusCode(400)
            .body("violations", hasSize(greaterThan(0)))
            .body("violations[0].field", containsString("title"));
    }
    
    @Test
    @Order(4)
    @DisplayName("Should get todo by ID")
    public void testGetTodoById() {
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/api/todos/" + createdTodoId)
        .then()
            .statusCode(200)
            .body("id", equalTo(createdTodoId.intValue()))
            .body("title", equalTo("Complete REST training"))
            .body("description", notNullValue())  // Detailed view includes description
            .body("createdAt", notNullValue());
    }
    
    @Test
    @Order(5)
    @DisplayName("Should list user's todos")
    public void testListTodos() {
        // Create another todo
        Map<String, Object> todo2 = new HashMap<>();
        todo2.put("title", "Review Jackson features");
        todo2.put("priority", "MEDIUM");
        
        given()
            .header("Authorization", "Bearer " + userToken)
            .contentType(ContentType.JSON)
            .body(todo2)
        .when()
            .post("/api/todos")
        .then()
            .statusCode(201);
        
        // List todos
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/api/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("[0].title", notNullValue())
            .body("[0].priority", notNullValue())
            .body("[0].description", nullValue());  // Summary view excludes description
    }
    
    @Test
    @Order(6)
    @DisplayName("Should filter todos by completion status")
    public void testFilterByCompleted() {
        given()
            .header("Authorization", "Bearer " + userToken)
            .queryParam("completed", false)
        .when()
            .get("/api/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("findAll { it.completed == true }", hasSize(0));
    }
    
    @Test
    @Order(7)
    @DisplayName("Should filter todos by priority")
    public void testFilterByPriority() {
        given()
            .header("Authorization", "Bearer " + userToken)
            .queryParam("priority", "HIGH")
        .when()
            .get("/api/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(1))
            .body("[0].priority.value", equalTo("HIGH"));
    }
    
    @Test
    @Order(8)
    @DisplayName("Should search todos by text")
    public void testSearchTodos() {
        given()
            .header("Authorization", "Bearer " + userToken)
            .queryParam("search", "jackson")
        .when()
            .get("/api/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(1))
            .body("[0].title", containsStringIgnoringCase("jackson"));
    }
    
    @Test
    @Order(9)
    @DisplayName("Should update todo")
    public void testUpdateTodo() {
        Map<String, Object> update = new HashMap<>();
        update.put("title", "Complete REST training - UPDATED");
        update.put("description", "Updated description");
        update.put("priority", "MEDIUM");
        update.put("completed", true);
        
        given()
            .header("Authorization", "Bearer " + userToken)
            .contentType(ContentType.JSON)
            .body(update)
        .when()
            .put("/api/todos/" + createdTodoId)
        .then()
            .statusCode(200)
            .body("title", equalTo("Complete REST training - UPDATED"))
            .body("completed", equalTo(true))
            .body("updatedAt", notNullValue());
    }
    
    @Test
    @Order(10)
    @DisplayName("Should toggle completion status")
    public void testToggleComplete() {
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .patch("/api/todos/" + createdTodoId + "/complete")
        .then()
            .statusCode(200)
            .body("completed", equalTo(false));  // Was true, now false
    }
    
    @Test
    @Order(11)
    @DisplayName("Should prevent access to other user's todos")
    public void testUserIsolation() {
        // Create a todo as admin
        Map<String, Object> adminTodo = new HashMap<>();
        adminTodo.put("title", "Admin only task");
        adminTodo.put("priority", "HIGH");
        
        Response response = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(adminTodo)
        .when()
            .post("/api/todos")
        .then()
            .statusCode(201)
            .extract().response();
        
        adminTodoId = response.jsonPath().getLong("id");
        
        // Try to access admin's todo as regular user - should get 404
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/api/todos/" + adminTodoId)
        .then()
            .statusCode(404);
        
        // Try to update admin's todo as regular user - should get 404
        given()
            .header("Authorization", "Bearer " + userToken)
            .contentType(ContentType.JSON)
            .body(adminTodo)
        .when()
            .put("/api/todos/" + adminTodoId)
        .then()
            .statusCode(404);
        
        // Try to delete admin's todo as regular user - should get 404
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .delete("/api/todos/" + adminTodoId)
        .then()
            .statusCode(404);
    }
    
    @Test
    @Order(12)
    @DisplayName("Admin should access all todos")
    public void testAdminAccessAllTodos() {
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/admin/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(3)));  // User's todos + admin's todo
    }
    
    @Test
    @Order(13)
    @DisplayName("Admin should get statistics")
    public void testAdminStats() {
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/admin/todos/stats")
        .then()
            .statusCode(200)
            .body("totalTodos", greaterThanOrEqualTo(3))
            .body("completedTodos", notNullValue())
            .body("pendingTodos", notNullValue())
            .body("byPriority", notNullValue())
            .body("byUser", notNullValue())
            .body("overdueTodos", notNullValue());
    }
    
    @Test
    @Order(14)
    @DisplayName("Regular user cannot access admin endpoints")
    public void testUserCannotAccessAdminEndpoints() {
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/api/admin/todos")
        .then()
            .statusCode(403);
        
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/api/admin/todos/stats")
        .then()
            .statusCode(403);
    }
    
    @Test
    @Order(15)
    @DisplayName("Should delete todo")
    public void testDeleteTodo() {
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .delete("/api/todos/" + userTodoId)
        .then()
            .statusCode(204);
        
        // Verify it's deleted
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/api/todos/" + userTodoId)
        .then()
            .statusCode(404);
    }
    
    @Test
    @Order(16)
    @DisplayName("Should handle date filtering")
    public void testDateFiltering() {
        // Create a todo with a specific due date
        Map<String, Object> futureTodo = new HashMap<>();
        futureTodo.put("title", "Future task");
        futureTodo.put("priority", "LOW");
        futureTodo.put("dueDate", "2025-12-31T23:59:59");
        
        given()
            .header("Authorization", "Bearer " + userToken)
            .contentType(ContentType.JSON)
            .body(futureTodo)
        .when()
            .post("/api/todos")
        .then()
            .statusCode(201);
        
        // Filter by due before
        given()
            .header("Authorization", "Bearer " + userToken)
            .queryParam("dueBefore", "2025-09-01T00:00:00")
        .when()
            .get("/api/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));  // Future task should not appear
        
        // Filter by due after
        given()
            .header("Authorization", "Bearer " + userToken)
            .queryParam("dueAfter", "2025-10-01T00:00:00")
        .when()
            .get("/api/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body("find { it.title == 'Future task' }", notNullValue());
    }
}