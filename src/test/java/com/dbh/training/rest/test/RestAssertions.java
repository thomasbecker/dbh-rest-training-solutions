package com.dbh.training.rest.test;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.*;

/**
 * Custom assertions for REST API testing.
 * 
 * Provides fluent assertions for common REST API test scenarios.
 */
public class RestAssertions {
    
    private final ValidatableResponse response;
    
    private RestAssertions(Response response) {
        this.response = response.then();
    }
    
    /**
     * Create assertions for a response.
     * 
     * @param response The response to assert
     * @return RestAssertions instance
     */
    public static RestAssertions assertThat(Response response) {
        return new RestAssertions(response);
    }
    
    // Status code assertions
    
    public RestAssertions isOk() {
        response.statusCode(200);
        return this;
    }
    
    public RestAssertions isCreated() {
        response.statusCode(201);
        return this;
    }
    
    public RestAssertions isNoContent() {
        response.statusCode(204);
        return this;
    }
    
    public RestAssertions isBadRequest() {
        response.statusCode(400);
        return this;
    }
    
    public RestAssertions isUnauthorized() {
        response.statusCode(401);
        return this;
    }
    
    public RestAssertions isForbidden() {
        response.statusCode(403);
        return this;
    }
    
    public RestAssertions isNotFound() {
        response.statusCode(404);
        return this;
    }
    
    public RestAssertions isConflict() {
        response.statusCode(409);
        return this;
    }
    
    public RestAssertions isServerError() {
        response.statusCode(greaterThanOrEqualTo(500));
        return this;
    }
    
    public RestAssertions hasStatus(int statusCode) {
        response.statusCode(statusCode);
        return this;
    }
    
    // Header assertions
    
    public RestAssertions hasContentType(String contentType) {
        response.contentType(contentType);
        return this;
    }
    
    public RestAssertions hasJsonContentType() {
        response.contentType("application/json");
        return this;
    }
    
    public RestAssertions hasHeader(String header, String value) {
        response.header(header, value);
        return this;
    }
    
    public RestAssertions hasLocationHeader() {
        response.header("Location", notNullValue());
        return this;
    }
    
    public RestAssertions hasLocationHeader(String expectedPath) {
        response.header("Location", containsString(expectedPath));
        return this;
    }
    
    // Pagination header assertions
    
    public RestAssertions hasPaginationHeaders() {
        response
            .header("X-Page", notNullValue())
            .header("X-Page-Size", notNullValue())
            .header("X-Total-Count", notNullValue())
            .header("X-Total-Pages", notNullValue());
        return this;
    }
    
    public RestAssertions hasPage(int page) {
        response.header("X-Page", String.valueOf(page));
        return this;
    }
    
    public RestAssertions hasPageSize(int size) {
        response.header("X-Page-Size", String.valueOf(size));
        return this;
    }
    
    public RestAssertions hasTotalCount(int count) {
        response.header("X-Total-Count", String.valueOf(count));
        return this;
    }
    
    // Body assertions
    
    public RestAssertions hasField(String path, Object value) {
        response.body(path, equalTo(value));
        return this;
    }
    
    public RestAssertions hasFieldNotNull(String path) {
        response.body(path, notNullValue());
        return this;
    }
    
    public RestAssertions hasFieldNull(String path) {
        response.body(path, nullValue());
        return this;
    }
    
    public RestAssertions hasSize(int size) {
        response.body("size()", equalTo(size));
        return this;
    }
    
    public RestAssertions hasSize(String path, int size) {
        response.body(path + ".size()", equalTo(size));
        return this;
    }
    
    public RestAssertions isEmpty() {
        response.body("size()", equalTo(0));
        return this;
    }
    
    public RestAssertions isNotEmpty() {
        response.body("size()", greaterThan(0));
        return this;
    }
    
    // Error response assertions
    
    public RestAssertions hasError(String message) {
        response.body("error", equalTo(message));
        return this;
    }
    
    public RestAssertions hasErrorContaining(String text) {
        response.body("error", containsString(text));
        return this;
    }
    
    public RestAssertions hasValidationError(String field, String message) {
        response.body("errors." + field, equalTo(message));
        return this;
    }
    
    public RestAssertions hasValidationErrors() {
        response.body("errors", notNullValue());
        return this;
    }
    
    // Time assertions
    
    public RestAssertions respondsWithin(long milliseconds) {
        response.time(lessThan(milliseconds));
        return this;
    }
    
    // User entity specific assertions
    
    public RestAssertions hasUser(String username, String email) {
        response
            .body("username", equalTo(username))
            .body("email", equalTo(email));
        return this;
    }
    
    public RestAssertions hasUserId() {
        response.body("id", notNullValue());
        return this;
    }
    
    public RestAssertions hasNoPassword() {
        response.body("password", nullValue());
        return this;
    }
    
    // Product entity specific assertions
    
    public RestAssertions hasProduct(String name, String sku) {
        response
            .body("name", equalTo(name))
            .body("sku", equalTo(sku));
        return this;
    }
    
    public RestAssertions hasProductId() {
        response.body("id", notNullValue());
        return this;
    }
    
    public RestAssertions hasPrice(String price) {
        response.body("price", equalTo(Double.parseDouble(price)));
        return this;
    }
    
    // Order entity specific assertions
    
    public RestAssertions hasOrderStatus(String status) {
        response.body("status", equalTo(status));
        return this;
    }
    
    public RestAssertions hasOrderId() {
        response.body("id", notNullValue());
        return this;
    }
    
    public RestAssertions hasOrderItems(int count) {
        response.body("items.size()", equalTo(count));
        return this;
    }
    
    // Get the underlying ValidatableResponse for custom assertions
    
    public ValidatableResponse and() {
        return response;
    }
    
    // Extract values
    
    public <T> T extract(String path, Class<T> type) {
        return response.extract().path(path);
    }
    
    public String extractHeader(String header) {
        return response.extract().header(header);
    }
    
    public <T> T extractAs(Class<T> type) {
        return response.extract().as(type);
    }
}