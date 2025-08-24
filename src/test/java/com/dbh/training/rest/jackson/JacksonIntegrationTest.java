package com.dbh.training.rest.jackson;

import com.dbh.training.rest.models.AccountStatus;
import com.dbh.training.rest.models.Address;
import com.dbh.training.rest.models.User;
import com.dbh.training.rest.test.BaseIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Jackson JSON processing
 * 
 * Exercise 06: Jackson Basics
 * Tests custom field naming, date formatting, nested objects, and security
 */
public class JacksonIntegrationTest extends BaseIntegrationTest {
    
    @Test
    public void testUserSerializationWithCustomFieldNames() {
        // Create a user with test data
        String userJson = "{"
            + "\"user_name\": \"john_doe\","
            + "\"email_address\": \"john@example.com\","
            + "\"first_name\": \"John\","
            + "\"last_name\": \"Doe\","
            + "\"birth_date\": \"1990-01-15\","
            + "\"account_status\": \"active\""
            + "}";
        
        // POST to create user
        Response response = given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(201)
            .extract()
            .response();
        
        // Verify response has custom field names
        response.then()
            .body("user_id", notNullValue())
            .body("user_name", equalTo("john_doe"))
            .body("email_address", equalTo("john@example.com"))
            .body("first_name", equalTo("John"))
            .body("last_name", equalTo("Doe"))
            .body("birth_date", equalTo("1990-01-15"))
            .body("account_status", equalTo("active"))
            .body("created_at", notNullValue());
        
        // Verify password is not in response (should be @JsonIgnore)
        String responseBody = response.getBody().asString();
        assertFalse(responseBody.contains("password"), "Password should not be in JSON response");
    }
    
    @Test
    public void testPasswordExclusionFromJson() {
        // Create user with password
        String userJson = "{"
            + "\"user_name\": \"secure_user\","
            + "\"email_address\": \"secure@example.com\","
            + "\"first_name\": \"Secure\","
            + "\"last_name\": \"User\","
            + "\"password\": \"secret123\""  // Should be ignored
            + "}";
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(201)
            .extract()
            .response();
        
        // Get the created user
        Long userId = response.jsonPath().getLong("user_id");
        
        given()
            .when()
            .get("/v2/users/" + userId)
            .then()
            .statusCode(200)
            .body("$", not(hasKey("password")))  // Password should never appear
            .body("user_name", equalTo("secure_user"));
    }
    
    @Test
    public void testDateFormatting() {
        String userJson = "{"
            + "\"user_name\": \"date_user\","
            + "\"email_address\": \"date@example.com\","
            + "\"first_name\": \"Date\","
            + "\"last_name\": \"User\","
            + "\"birth_date\": \"1985-06-20\""
            + "}";
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(201)
            .extract()
            .response();
        
        // Verify date formats
        response.then()
            .body("birth_date", equalTo("1985-06-20"))  // LocalDate format
            .body("created_at", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"));  // LocalDateTime format
    }
    
    @Test
    public void testEnumSerialization() {
        // Test different enum values
        String[] statuses = {"active", "suspended", "pending", "deleted"};
        
        for (String status : statuses) {
            String userJson = String.format("{"
                + "\"user_name\": \"enum_user_%s\","
                + "\"email_address\": \"enum_%s@example.com\","
                + "\"first_name\": \"Enum\","
                + "\"last_name\": \"User\","
                + "\"account_status\": \"%s\""
                + "}", status, status, status);
            
            given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/v2/users")
                .then()
                .statusCode(201)
                .body("account_status", equalTo(status));
        }
    }
    
    @Test
    public void testNestedAddressObject() {
        String userJson = "{"
            + "\"user_name\": \"address_user\","
            + "\"email_address\": \"address@example.com\","
            + "\"first_name\": \"Address\","
            + "\"last_name\": \"User\","
            + "\"primary_address\": {"
            + "  \"street_line_1\": \"123 Main St\","
            + "  \"street_line_2\": \"Apt 4B\","
            + "  \"city\": \"Berlin\","
            + "  \"state\": \"BE\","
            + "  \"postal_code\": \"10115\","
            + "  \"country_code\": \"DE\""
            + "},"
            + "\"billing_address\": {"
            + "  \"street_line_1\": \"456 Commerce Ave\","
            + "  \"city\": \"Hamburg\","
            + "  \"postal_code\": \"20095\","
            + "  \"country_code\": \"DE\""
            + "}"
            + "}";
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(201)
            .extract()
            .response();
        
        // Verify nested address objects
        response.then()
            .body("primary_address", notNullValue())
            .body("primary_address.street_line_1", equalTo("123 Main St"))
            .body("primary_address.street_line_2", equalTo("Apt 4B"))
            .body("primary_address.city", equalTo("Berlin"))
            .body("primary_address.postal_code", equalTo("10115"))
            .body("primary_address.country_code", equalTo("DE"))
            .body("billing_address", notNullValue())
            .body("billing_address.street_line_1", equalTo("456 Commerce Ave"))
            .body("billing_address.city", equalTo("Hamburg"));
    }
    
    @Test
    public void testRolesListSerialization() {
        String userJson = "{"
            + "\"user_name\": \"admin_user\","
            + "\"email_address\": \"admin@example.com\","
            + "\"first_name\": \"Admin\","
            + "\"last_name\": \"User\","
            + "\"roles\": [\"ADMIN\", \"USER\", \"MODERATOR\"]"
            + "}";
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(201)
            .extract()
            .response();
        
        // Verify roles array
        response.then()
            .body("roles", notNullValue())
            .body("roles", hasSize(3))
            .body("roles", hasItems("ADMIN", "USER", "MODERATOR"));
    }
    
    @Test
    public void testNullFieldExclusion() {
        // Create user with minimal fields (others will be null)
        String userJson = "{"
            + "\"user_name\": \"minimal_user\","
            + "\"email_address\": \"minimal@example.com\","
            + "\"first_name\": \"Minimal\","
            + "\"last_name\": \"User\""
            + "}";
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(201)
            .extract()
            .response();
        
        // Get the created user
        Long userId = response.jsonPath().getLong("user_id");
        
        Response getResponse = given()
            .when()
            .get("/v2/users/" + userId)
            .then()
            .statusCode(200)
            .extract()
            .response();
        
        // Verify null fields are not in response (due to NON_NULL inclusion)
        String responseBody = getResponse.getBody().asString();
        assertFalse(responseBody.contains("\"birth_date\":null"), "Null fields should be excluded");
        assertFalse(responseBody.contains("\"primary_address\":null"), "Null fields should be excluded");
        assertFalse(responseBody.contains("\"roles\":null"), "Null fields should be excluded");
    }
    
    @Test
    public void testInvalidDateFormatRejection() {
        String userJson = "{"
            + "\"user_name\": \"invalid_date\","
            + "\"email_address\": \"invalid@example.com\","
            + "\"first_name\": \"Invalid\","
            + "\"last_name\": \"Date\","
            + "\"birth_date\": \"20-06-1985\""  // Wrong format
            + "}";
        
        given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(400);  // Should fail due to invalid date format
    }
    
    @Test
    public void testAddressValidation() {
        // Test address with invalid country code
        String userJson = "{"
            + "\"user_name\": \"invalid_address\","
            + "\"email_address\": \"invalid@example.com\","
            + "\"first_name\": \"Invalid\","
            + "\"last_name\": \"Address\","
            + "\"primary_address\": {"
            + "  \"street_line_1\": \"123 Main St\","
            + "  \"city\": \"Berlin\","
            + "  \"postal_code\": \"10115\","
            + "  \"country_code\": \"GERMANY\""  // Should be 2 chars
            + "}"
            + "}";
        
        given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(400)
            .body("errors", hasItem(containsString("Country code must be exactly 2 characters")));
    }
    
    @Test
    public void testCompleteUserRoundTrip() {
        // Create a complete user with all fields
        String createJson = "{"
            + "\"user_name\": \"complete_user\","
            + "\"email_address\": \"complete@example.com\","
            + "\"first_name\": \"Complete\","
            + "\"last_name\": \"User\","
            + "\"password\": \"hidden123\","
            + "\"birth_date\": \"1992-03-25\","
            + "\"account_status\": \"active\","
            + "\"roles\": [\"USER\"],"
            + "\"primary_address\": {"
            + "  \"street_line_1\": \"789 Test St\","
            + "  \"city\": \"Munich\","
            + "  \"postal_code\": \"80331\","
            + "  \"country_code\": \"DE\""
            + "}"
            + "}";
        
        // Create user
        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(createJson)
            .when()
            .post("/v2/users")
            .then()
            .statusCode(201)
            .extract()
            .response();
        
        Long userId = createResponse.jsonPath().getLong("user_id");
        
        // Get user and verify all fields
        given()
            .when()
            .get("/v2/users/" + userId)
            .then()
            .statusCode(200)
            .body("user_id", equalTo(userId.intValue()))
            .body("user_name", equalTo("complete_user"))
            .body("email_address", equalTo("complete@example.com"))
            .body("first_name", equalTo("Complete"))
            .body("last_name", equalTo("User"))
            .body("birth_date", equalTo("1992-03-25"))
            .body("account_status", equalTo("active"))
            .body("roles", hasItem("USER"))
            .body("primary_address.city", equalTo("Munich"))
            .body("$", not(hasKey("password")));  // Password should never be returned
    }
}