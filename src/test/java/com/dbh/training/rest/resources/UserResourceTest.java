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
        UserResourceV1.resetForTesting();
        UserResourceV2.resetForTesting();
    }
    
    @Test
    public void testGetAllUsersEmpty() {
        // When no users exist, should return empty array
        given()
            .header("Authorization", "Bearer " + userToken)
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users")
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
            .header("Authorization", "Bearer " + userToken)
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", equalTo(2))
            .body("user_name", hasItems("alice", "bob"));
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
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(newUser)
            .when()
                .post("/v1/users")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Location", containsString("/users/"))
                .body("user_id", notNullValue())
                .body("user_name", equalTo("johndoe"))
                .body("email_address", equalTo("john@example.com"))
                .body("first_name", equalTo("John"))
                .body("last_name", equalTo("Doe"))
                .extract()
                .path("user_id");
        
        // Then: Verify the user can be retrieved
        given()
            .header("Authorization", "Bearer " + userToken)
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users/{id}", userId)
        .then()
            .statusCode(200)
            .body("user_name", equalTo("johndoe"));
    }
    
    @Test
    public void testGetUserById() {
        // Given: Create a test user
        Integer userId = createTestUser("janesmith", "jane@example.com", "Jane", "Smith");
        
        // When: Get the user by ID
        // Then: Should return the correct user
        given()
            .header("Authorization", "Bearer " + userToken)
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users/{id}", userId)
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("user_id", equalTo(userId))
            .body("user_name", equalTo("janesmith"))
            .body("email_address", equalTo("jane@example.com"))
            .body("first_name", equalTo("Jane"))
            .body("last_name", equalTo("Smith"));
    }
    
    @Test
    public void testGetUserByIdNotFound() {
        // When: Request non-existent user
        // Then: Should return 404
        given()
            .header("Authorization", "Bearer " + userToken)
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users/{id}", 99999)
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
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(updatedUser)
        .when()
            .put("/v1/users/{id}", userId)
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("email_address", equalTo("updated@example.com"))
            .body("first_name", equalTo("Updated"));
        
        // Then: Verify the update persisted
        given()
            .header("Authorization", "Bearer " + userToken)
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users/{id}", userId)
        .then()
            .statusCode(200)
            .body("email_address", equalTo("updated@example.com"))
            .body("first_name", equalTo("Updated"));
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
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(updateUser)
        .when()
            .put("/v1/users/{id}", 99999)
        .then()
            .statusCode(404);
    }
    
    @Test
    public void testDeleteUser() {
        // Given: Create a test user
        Integer userId = createTestUser("deletetest", "delete@example.com", "Delete", "Test");
        
        // When: Delete the user
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/v1/users/{id}", userId)
        .then()
            .statusCode(204);
        
        // Then: Verify the user is gone
        given()
            .header("Authorization", "Bearer " + userToken)
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users/{id}", userId)
        .then()
            .statusCode(404);
    }
    
    @Test
    public void testDeleteUserNotFound() {
        // When: Try to delete non-existent user
        // Then: Should return 404
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/v1/users/{id}", 99999)
        .then()
            .statusCode(404);
    }
    
    // ===== VALIDATION TESTS (Exercise 04) =====
    
    @Test
    public void testCreateUserWithMissingUsername() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("Username is required")));
    }
    
    @Test
    public void testCreateUserWithShortUsername() {
        User user = new User();
        user.setUsername("ab"); // Too short - minimum is 3
        user.setEmail("valid@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("Username must be between 3 and 50 characters")));
    }
    
    @Test
    public void testCreateUserWithInvalidEmail() {
        User user = new User();
        user.setUsername("validuser");
        user.setEmail("not-an-email");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("Email must be valid")));
    }
    
    @Test
    public void testCreateUserWithMissingEmail() {
        User user = new User();
        user.setUsername("validuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("Email is required")));
    }
    
    @Test
    public void testCreateUserWithMissingFirstName() {
        User user = new User();
        user.setUsername("validuser");
        user.setEmail("valid@example.com");
        user.setLastName("Doe");
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("First name is required")));
    }
    
    @Test
    public void testCreateUserWithMultipleValidationErrors() {
        User user = new User();
        user.setUsername("a"); // Too short
        user.setEmail("invalid"); // Invalid format
        // Missing firstName and lastName
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasSize(4)) // All 4 validation errors
            .body("errors", hasItem(containsString("Username must be between 3 and 50 characters")))
            .body("errors", hasItem(containsString("Email must be valid")))
            .body("errors", hasItem(containsString("First name is required")))
            .body("errors", hasItem(containsString("Last name is required")));
    }
    
    @Test
    public void testUpdateUserWithInvalidData() {
        // First create a valid user
        Integer userId = createTestUser("originaluser", "original@example.com", "Original", "User");
        
        // Try to update with invalid data
        User invalidUpdate = new User();
        invalidUpdate.setUsername(""); // Blank username
        invalidUpdate.setEmail("not-valid");
        invalidUpdate.setFirstName("Updated");
        invalidUpdate.setLastName("User");
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(invalidUpdate)
        .when()
            .put("/v1/users/{id}", userId)
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("Username is required")))
            .body("errors", hasItem(containsString("Email must be valid")));
    }
    
    @Test
    public void testCreateUserWithValidDataAfterAddingValidation() {
        // This test ensures that valid data still works after adding validation
        User user = new User();
        user.setUsername("validuser");
        user.setEmail("valid@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(201)
            .header("Location", containsString("/api/v1/users/"))
            .body("user_id", notNullValue())
            .body("user_name", equalTo("validuser"))
            .body("email_address", equalTo("valid@example.com"))
            .body("first_name", equalTo("John"))
            .body("last_name", equalTo("Doe"));
    }
    
    // Helper method to create test users
    private Integer createTestUser(String username, String email, String firstName, String lastName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        return given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(201)
            .extract()
            .path("user_id");
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
            .body("user_name", hasItems("johndoe", "janedoe"));
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
            .body("email_address", equalTo("patched@example.com"))
            .body("user_name", equalTo("patchtest")) // Should not change
            .body("first_name", equalTo("Patch"));    // Should not change
    }
    */
}
