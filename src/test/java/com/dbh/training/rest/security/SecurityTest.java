package com.dbh.training.rest.security;

import com.dbh.training.rest.Application;
import com.dbh.training.rest.dto.LoginRequest;
import com.dbh.training.rest.dto.TokenResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Security Tests
 * 
 * Exercise 08: Security Implementation
 * Tests JWT authentication and role-based authorization
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecurityTest {
    
    private Server server;
    private String adminToken;
    private String userToken;
    
    @BeforeAll
    public void setUp() throws Exception {
        // Start the server
        server = Application.startServer(8089);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8089;
        RestAssured.basePath = "/api";
    }
    
    @AfterAll
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
    
    @Test
    @Order(1)
    public void testLoginWithValidCredentials() {
        LoginRequest request = new LoginRequest("admin", "admin123");
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("token_type", equalTo("Bearer"))
            .body("expires_in", equalTo(3600))
            .extract()
            .response();
        
        adminToken = response.jsonPath().getString("access_token");
        Assertions.assertNotNull(adminToken, "Admin token should not be null");
    }
    
    @Test
    @Order(2)
    public void testLoginWithInvalidCredentials() {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401)
            .body("error", equalTo("Invalid credentials"));
    }
    
    @Test
    @Order(3)
    public void testLoginAsRegularUser() {
        LoginRequest request = new LoginRequest("user", "user123");
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .extract()
            .response();
        
        userToken = response.jsonPath().getString("access_token");
        Assertions.assertNotNull(userToken, "User token should not be null");
    }
    
    @Test
    @Order(4)
    public void testAccessProtectedEndpointWithoutToken() {
        given()
        .when()
            .get("/v1/users")
        .then()
            .statusCode(403); // Forbidden without token
    }
    
    @Test
    @Order(5)
    public void testAccessProtectedEndpointWithAdminToken() {
        // First create a user to ensure we have data
        String testUser = "{\"user_name\":\"testuser1\",\"email_address\":\"testuser1@example.com\"," +
                          "\"first_name\":\"Test\",\"last_name\":\"User1\"}";
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(testUser)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(201);
        
        // Now test getting the list
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/v1/users")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
    }
    
    @Test
    @Order(6)
    public void testAccessProtectedEndpointWithUserToken() {
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/v1/users")
        .then()
            .statusCode(200); // Regular users can view users list
    }
    
    @Test
    @Order(7)
    public void testAdminOnlyEndpointWithAdminToken() {
        // First create a user to ensure user with ID 1 exists
        String testUser = "{\"user_name\":\"testadmin\",\"email_address\":\"testadmin@example.com\"," +
                          "\"first_name\":\"Test\",\"last_name\":\"Admin\"}";
        
        Integer userId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(testUser)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(201)
            .extract()
            .path("user_id");
        
        // Now test the admin endpoint
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/v1/users/" + userId + "/admin")
        .then()
            .statusCode(200);
    }
    
    @Test
    @Order(8)
    public void testAdminOnlyEndpointWithUserToken() {
        // First create a user to ensure a user exists
        String testUser = "{\"user_name\":\"testuser2\",\"email_address\":\"testuser2@example.com\"," +
                          "\"first_name\":\"Test\",\"last_name\":\"User2\"}";
        
        Integer userId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(testUser)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(201)
            .extract()
            .path("user_id");
        
        // Now test that regular user cannot access admin endpoint
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/v1/users/" + userId + "/admin")
        .then()
            .statusCode(403); // Forbidden for regular users
    }
    
    @Test
    @Order(9)
    public void testCreateUserWithAdminToken() {
        String newUser = "{\"user_name\":\"newuser\",\"email_address\":\"new@example.com\"," +
                        "\"first_name\":\"New\",\"last_name\":\"User\",\"password\":\"newpass123\"}";
        
        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(newUser)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(201)
            .header("Location", notNullValue());
    }
    
    @Test
    @Order(10)
    public void testCreateUserWithUserToken() {
        String newUser = "{\"user_name\":\"another\",\"email_address\":\"another@example.com\"," +
                        "\"first_name\":\"Another\",\"last_name\":\"User\",\"password\":\"pass123\"}";
        
        given()
            .header("Authorization", "Bearer " + userToken)
            .contentType(ContentType.JSON)
            .body(newUser)
        .when()
            .post("/v1/users")
        .then()
            .statusCode(403); // Forbidden for regular users
    }
    
    @Test
    @Order(11)
    public void testDeleteUserWithAdminToken() {
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/v1/users/3")
        .then()
            .statusCode(anyOf(equalTo(204), equalTo(404))); // 204 if exists, 404 if not
    }
    
    @Test
    @Order(12)
    public void testDeleteUserWithUserToken() {
        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .delete("/v1/users/2")
        .then()
            .statusCode(403); // Forbidden for regular users
    }
    
    @Test
    @Order(13)
    public void testSecurityHeaders() {
        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/v1/users")
        .then()
            .header("X-Content-Type-Options", "nosniff")
            .header("X-Frame-Options", "DENY")
            .header("X-XSS-Protection", "1; mode=block")
            .header("Content-Security-Policy", notNullValue())
            .header("Referrer-Policy", notNullValue());
    }
    
    @Test
    @Order(14)
    public void testInvalidToken() {
        given()
            .header("Authorization", "Bearer invalid-token-here")
        .when()
            .get("/v1/users")
        .then()
            .statusCode(403); // Invalid token results in forbidden
    }
    
    @Test
    @Order(15)
    public void testMalformedAuthorizationHeader() {
        given()
            .header("Authorization", "NotBearer " + adminToken)
        .when()
            .get("/v1/users")
        .then()
            .statusCode(403); // Malformed header results in forbidden
    }
    
    @Test
    @Order(16)
    public void testHealthEndpointNoAuth() {
        // Health endpoint should not require authentication
        given()
        .when()
            .get("/auth/health")
        .then()
            .statusCode(200)
            .body("status", equalTo("healthy"));
    }
}