package com.dbh.training.rest.resources;

import com.dbh.training.rest.models.User;
import com.dbh.training.rest.test.BaseIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * REST Assured tests for UserResource CRUD operations.
 * 
 * These tests define the expected behavior of your REST API.
 * Your task: Make all these tests pass by implementing UserResource!
 * 
 * Run with: ./gradlew test --tests UserResourceTest
 * 
 * Note: Each test is independent and creates its own test data.
 */
public class UserResourceTest extends BaseIntegrationTest {
    
    @Override
    @BeforeEach
    public void setupTest() {
        super.setupTest();
        // The base path is already set to /api by BaseIntegrationTest
        // Tests will use paths relative to /api (e.g., "/users", "/users/1")
        
        // Clear all users before each test to ensure test independence
        // Using package-private method for clean test isolation
        UserResource.resetForTesting();
    }
    
    @Test
    public void testGetAllUsersEmpty() {
        // When no users exist, should return empty array
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", is(notNullValue()))
            .body("size()", equalTo(0));
    }
    
    @Test
    public void testGetAllUsersWithData() {
        // Given: Create some test users
        createTestUser("alice", "alice@example.com", "Alice", "Smith");
        createTestUser("bob", "bob@example.com", "Bob", "Jones");
        
        // When: Get all users
        // Then: Should return both users
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", equalTo(2))
            .body("username", hasItems("alice", "bob"));
    }
    
    @Test
    public void testCreateUser() {
        // Given: A new user
        User newUser = new User();
        newUser.setUsername("johndoe");
        newUser.setEmail("john@example.com");
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        
        // When: Create the user
        Integer userId = 
            given()
                .contentType(ContentType.JSON)
                .body(newUser)
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Location", containsString("/users/"))
                .body("id", notNullValue())
                .body("username", equalTo("johndoe"))
                .body("email", equalTo("john@example.com"))
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .extract()
                .path("id");
        
        // Then: Verify the user can be retrieved
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users/{id}", userId)
        .then()
            .statusCode(200)
            .body("username", equalTo("johndoe"));
    }
    
    @Test
    public void testGetUserById() {
        // Given: Create a test user
        Integer userId = createTestUser("janesmith", "jane@example.com", "Jane", "Smith");
        
        // When: Get the user by ID
        // Then: Should return the correct user
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users/{id}", userId)
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(userId))
            .body("username", equalTo("janesmith"))
            .body("email", equalTo("jane@example.com"))
            .body("firstName", equalTo("Jane"))
            .body("lastName", equalTo("Smith"));
    }
    
    @Test
    public void testGetUserByIdNotFound() {
        // When: Request non-existent user
        // Then: Should return 404
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users/{id}", 99999)
        .then()
            .statusCode(404);
    }
    
    @Test
    public void testUpdateUser() {
        // Given: Create a test user
        Integer userId = createTestUser("updatetest", "update@example.com", "Update", "Test");
        
        // When: Update the user
        User updatedUser = new User();
        updatedUser.setId(userId.longValue());
        updatedUser.setUsername("updatetest"); // Keep same username
        updatedUser.setEmail("updated@example.com"); // Change email
        updatedUser.setFirstName("Updated"); // Change first name
        updatedUser.setLastName("Test"); // Keep same last name
        
        given()
            .contentType(ContentType.JSON)
            .body(updatedUser)
        .when()
            .put("/users/{id}", userId)
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("email", equalTo("updated@example.com"))
            .body("firstName", equalTo("Updated"));
        
        // Then: Verify the update persisted
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users/{id}", userId)
        .then()
            .statusCode(200)
            .body("email", equalTo("updated@example.com"))
            .body("firstName", equalTo("Updated"));
    }
    
    @Test
    public void testUpdateUserNotFound() {
        // Given: A user that doesn't exist (with valid data for validation)
        User updateUser = new User();
        updateUser.setId(99999L);
        updateUser.setUsername("ghost");
        updateUser.setEmail("ghost@example.com");
        updateUser.setFirstName("Ghost");
        updateUser.setLastName("User");
        
        // When: Try to update non-existent user
        // Then: Should return 404
        given()
            .contentType(ContentType.JSON)
            .body(updateUser)
        .when()
            .put("/users/{id}", 99999)
        .then()
            .statusCode(404);
    }
    
    @Test
    public void testDeleteUser() {
        // Given: Create a test user
        Integer userId = createTestUser("deletetest", "delete@example.com", "Delete", "Test");
        
        // When: Delete the user
        given()
        .when()
            .delete("/users/{id}", userId)
        .then()
            .statusCode(204);
        
        // Then: Verify the user is gone
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/users/{id}", userId)
        .then()
            .statusCode(404);
    }
    
    @Test
    public void testDeleteUserNotFound() {
        // When: Try to delete non-existent user
        // Then: Should return 404
        given()
        .when()
            .delete("/users/{id}", 99999)
        .then()
            .statusCode(404);
    }
    
    // Helper method to create test users
    private Integer createTestUser(String username, String email, String firstName, String lastName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        return given()
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .extract()
            .path("id");
    }
    
    // ===== BONUS TESTS (Uncomment when implementing bonus tasks) =====
    
    /*
    @Test
    public void testPagination() {
        // Given: Create multiple users
        for (int i = 1; i <= 15; i++) {
            createTestUser("user" + i, "user" + i + "@example.com", "User", "Number" + i);
        }
        
        // When: Request with pagination
        // Then: Should return paginated results
        given()
            .queryParam("page", 1)
            .queryParam("size", 5)
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(200)
            .header("X-Total-Count", notNullValue())
            .header("X-Page", equalTo("1"))
            .header("X-Page-Size", equalTo("5"))
            .body("size()", equalTo(5));
    }
    
    @Test
    public void testFilterByUsername() {
        // Given: Create test users
        createTestUser("johndoe", "john@example.com", "John", "Doe");
        createTestUser("janedoe", "jane@example.com", "Jane", "Doe");
        createTestUser("alice", "alice@example.com", "Alice", "Smith");
        
        // When: Filter by username containing "doe"
        // Then: Should return only matching users
        given()
            .queryParam("username", "doe")
            .accept(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("username", hasItems("johndoe", "janedoe"));
    }
    
    @Test
    public void testPatchUser() {
        // Given: Create a test user
        Integer userId = createTestUser("patchtest", "patch@example.com", "Patch", "Test");
        
        // When: Patch only the email
        String partialUpdate = "{\"email\": \"patched@example.com\"}";
        
        given()
            .contentType(ContentType.JSON)
            .body(partialUpdate)
        .when()
            .patch("/{id}", userId)
        .then()
            .statusCode(200)
            .body("email", equalTo("patched@example.com"))
            .body("username", equalTo("patchtest")) // Should not change
            .body("firstName", equalTo("Patch"));    // Should not change
    }
    */
}
