package com.dbh.training.rest.test;

import com.dbh.training.rest.Application;
import com.dbh.training.rest.dto.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all integration tests.
 * 
 * Manages the server lifecycle and provides common test setup:
 * - Starts embedded Jetty server before all tests
 * - Configures RestAssured for testing
 * - Provides helper methods for common test operations
 * - Cleans up after tests complete
 */
public abstract class BaseIntegrationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);
    
    protected static final int TEST_PORT = 8081;
    protected static final String BASE_URI = "http://localhost";
    protected static final String BASE_PATH = "/api";
    
    private static Server server;
    protected static RequestSpecification requestSpec;
    protected static String adminToken;
    protected static String userToken;
    
    @BeforeAll
    public static void startServer() throws Exception {
        logger.info("Starting test server on port {}", TEST_PORT);
        
        // Start the server
        server = Application.createServer(TEST_PORT);
        server.start();
        
        // Wait for server to be ready
        Thread.sleep(1000);
        
        // Configure RestAssured
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = TEST_PORT;
        RestAssured.basePath = BASE_PATH;
        
        // Create reusable request specification
        requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setPort(TEST_PORT)
            .setBasePath(BASE_PATH)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
        
        // Get authentication tokens for tests
        loginTestUsers();
        
        logger.info("Test server started successfully");
    }
    
    /**
     * Login test users and get tokens
     */
    private static void loginTestUsers() {
        try {
            // Login as admin
            LoginRequest adminLogin = new LoginRequest("admin", "admin123");
            Response adminResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(adminLogin)
                .when()
                .post(BASE_URI + ":" + TEST_PORT + BASE_PATH + "/auth/login");
            
            if (adminResponse.getStatusCode() == 200) {
                adminToken = adminResponse.jsonPath().getString("access_token");
                logger.info("Admin token obtained for tests: {}", adminToken != null ? "success" : "failed");
            }
            
            // Login as regular user
            LoginRequest userLogin = new LoginRequest("user", "user123");
            Response userResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userLogin)
                .when()
                .post(BASE_URI + ":" + TEST_PORT + BASE_PATH + "/auth/login");
            
            if (userResponse.getStatusCode() == 200) {
                userToken = userResponse.jsonPath().getString("access_token");
                logger.info("User token obtained for tests: {}", userToken != null ? "success" : "failed");
            }
        } catch (Exception e) {
            logger.warn("Could not obtain test tokens - tests may need to handle auth independently", e);
        }
    }
    
    @AfterAll
    public static void stopServer() throws Exception {
        logger.info("Stopping test server");
        
        if (server != null) {
            server.stop();
            server.destroy();
        }
        
        // Reset RestAssured
        RestAssured.reset();
        
        logger.info("Test server stopped");
    }
    
    @BeforeEach
    public void setupTest() {
        // Override in subclasses for test-specific setup
        // For example: clearing data, resetting state, etc.
    }
    
    /**
     * Helper method to build a request specification with common settings.
     * 
     * @return RequestSpecification with default settings
     */
    protected RequestSpecification given() {
        return RestAssured.given()
            .spec(requestSpec)
            .when();
    }
    
    /**
     * Helper method to build an authenticated request.
     * 
     * @param token Authentication token
     * @return RequestSpecification with auth header
     */
    protected RequestSpecification givenAuth(String token) {
        return given()
            .header("Authorization", "Bearer " + token);
    }
    
    /**
     * Helper method to build request with custom headers.
     * 
     * @param headers Headers to add
     * @return RequestSpecification with headers
     */
    protected RequestSpecification givenWithHeaders(String... headers) {
        RequestSpecification spec = given();
        
        if (headers.length % 2 != 0) {
            throw new IllegalArgumentException("Headers must be provided in pairs");
        }
        
        for (int i = 0; i < headers.length; i += 2) {
            spec = spec.header(headers[i], headers[i + 1]);
        }
        
        return spec;
    }
    
    /**
     * Get the full URL for a given path.
     * 
     * @param path The API path
     * @return Full URL
     */
    protected String url(String path) {
        return BASE_URI + ":" + TEST_PORT + BASE_PATH + path;
    }
}