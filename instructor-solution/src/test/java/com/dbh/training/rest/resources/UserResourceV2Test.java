package com.dbh.training.rest.resources;

import com.dbh.training.rest.models.UserV2;
import com.dbh.training.rest.test.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests for UserResource V2 API.
 * 
 * Tests the new V2 structure with separate firstName/lastName fields
 * and optional age field.
 */
public class UserResourceV2Test extends BaseIntegrationTest {
    
    @Override
    @BeforeEach
    public void setupTest() {
        super.setupTest();
        UserResourceV1.resetForTesting();
        UserResourceV2.resetForTesting();
    }
    
    @Test
    public void testGetAllUsersV2Empty() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v2/users")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", is(notNullValue()))
            .body("size()", equalTo(0));
    }
    
    @Test
    public void testCreateUserV2WithSeparateNameFields() {
        UserV2 newUser = new UserV2();
        newUser.setUsername("johndoe");
        newUser.setEmail("john@example.com");
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        
        Integer userId = 
            given()
                .contentType(ContentType.JSON)
                .body(newUser)
            .when()
                .post("/v2/users")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Location", containsString("/v2/users/"))
                .body("user_id", notNullValue())
                .body("user_name", equalTo("johndoe"))
                .body("email_address", equalTo("john@example.com"))
                .body("first_name", equalTo("John"))
                .body("last_name", equalTo("Doe"))
                .body("age", nullValue())
                .extract()
                .path("user_id");
        
        // Verify the user can be retrieved
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v2/users/{id}", userId)
        .then()
            .statusCode(200)
            .body("first_name", equalTo("John"))
            .body("last_name", equalTo("Doe"));
    }
    
    @Test
    public void testCreateUserV2WithAge() {
        UserV2 newUser = new UserV2();
        newUser.setUsername("janedoe");
        newUser.setEmail("jane@example.com");
        newUser.setFirstName("Jane");
        newUser.setLastName("Doe");
        newUser.setAge(30);
        
        given()
            .contentType(ContentType.JSON)
            .body(newUser)
        .when()
            .post("/v2/users")
        .then()
            .statusCode(201)
            .body("age", equalTo(30));
    }
    
    @Test
    public void testV2ValidationMissingFirstName() {
        UserV2 user = new UserV2();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        // Missing firstName
        user.setLastName("Doe");
        
        given()
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v2/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("First name is required")));
    }
    
    @Test
    public void testV2ValidationMissingLastName() {
        UserV2 user = new UserV2();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        // Missing lastName
        
        given()
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v2/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("Last name is required")));
    }
    
    @Test
    public void testV2ValidationInvalidAge() {
        UserV2 user = new UserV2();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAge(-5); // Invalid negative age
        
        given()
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v2/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("Age cannot be negative")));
    }
    
    @Test
    public void testV2ValidationAgeExceedsMax() {
        UserV2 user = new UserV2();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAge(200); // Exceeds max
        
        given()
            .contentType(ContentType.JSON)
            .body(user)
        .when()
            .post("/v2/users")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("errors", hasItem(containsString("Age cannot exceed 150")));
    }
    
    @Test
    public void testUpdateUserV2() {
        // First create a user
        UserV2 newUser = new UserV2();
        newUser.setUsername("updatetest");
        newUser.setEmail("update@example.com");
        newUser.setFirstName("Update");
        newUser.setLastName("Test");
        
        Integer userId = given()
            .contentType(ContentType.JSON)
            .body(newUser)
        .when()
            .post("/v2/users")
        .then()
            .statusCode(201)
            .extract()
            .path("user_id");
        
        // Update the user
        UserV2 updatedUser = new UserV2();
        updatedUser.setId(userId.longValue());
        updatedUser.setUsername("updatetest");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Test");
        updatedUser.setAge(25);
        
        given()
            .contentType(ContentType.JSON)
            .body(updatedUser)
        .when()
            .put("/v2/users/{id}", userId)
        .then()
            .statusCode(200)
            .body("email_address", equalTo("updated@example.com"))
            .body("first_name", equalTo("Updated"))
            .body("age", equalTo(25));
    }
    
    @Test
    public void testDeleteUserV2() {
        // Create a test user
        UserV2 newUser = new UserV2();
        newUser.setUsername("deletetest");
        newUser.setEmail("delete@example.com");
        newUser.setFirstName("Delete");
        newUser.setLastName("Test");
        
        Integer userId = given()
            .contentType(ContentType.JSON)
            .body(newUser)
        .when()
            .post("/v2/users")
        .then()
            .statusCode(201)
            .extract()
            .path("user_id");
        
        // Delete the user
        given()
        .when()
            .delete("/v2/users/{id}", userId)
        .then()
            .statusCode(204);
        
        // Verify the user is gone
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v2/users/{id}", userId)
        .then()
            .statusCode(404);
    }
    
    @Test
    public void testV1AndV2Isolation() {
        // Create user in V1
        com.dbh.training.rest.models.User v1User = new com.dbh.training.rest.models.User();
        v1User.setUsername("v1user");
        v1User.setEmail("v1@example.com");
        v1User.setFirstName("V1");
        v1User.setLastName("User");
        
        given()
            .contentType(ContentType.JSON)
            .body(v1User)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(201);
        
        // Create user in V2
        UserV2 v2User = new UserV2();
        v2User.setUsername("v2user");
        v2User.setEmail("v2@example.com");
        v2User.setFirstName("V2");
        v2User.setLastName("User");
        
        given()
            .contentType(ContentType.JSON)
            .body(v2User)
        .when()
            .post("/v2/users")
        .then()
            .statusCode(201);
        
        // V1 should have 1 user
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].user_name", equalTo("v1user"));
        
        // V2 should have 1 different user
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v2/users")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].user_name", equalTo("v2user"));
    }
    
    @Test
    public void testV1HasDeprecationHeaders() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v1/users")
        .then()
            .statusCode(200)
            .header("Sunset", notNullValue())
            .header("Deprecation", equalTo("true"))
            .header("Link", containsString("/api/v2/users"));
    }
    
    @Test
    public void testV2NoDeprecationHeaders() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v2/users")
        .then()
            .statusCode(200)
            .header("Sunset", nullValue())
            .header("Deprecation", nullValue());
    }
}