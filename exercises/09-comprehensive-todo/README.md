# Exercise 09: Comprehensive Todo API

**Duration:** 90 minutes  
**Difficulty:** Comprehensive  
**Objectives:**
- Apply all concepts learned in Day 1 and Day 2
- Build a complete REST API from scratch
- Integrate security, validation, and JSON handling
- Practice real-world API development

## Prerequisites

- Completed Exercises 01-08
- Understanding of all concepts covered:
  - REST principles and Jersey
  - Bean Validation
  - API versioning strategies
  - Jackson JSON processing
  - JWT authentication and authorization
  - OpenAPI documentation

## Background

You've learned individual REST API concepts throughout the training. Now it's
time to bring everything together by building a complete Todo List API. This
exercise simulates a real-world scenario where you need to create a
production-ready API with all the features users expect.

## Requirements Overview

Build a Todo List API that allows users to:
- Create, read, update, and delete todos
- Mark todos as complete/incomplete
- Filter todos by various criteria
- Secure their todos from other users
- Export todos in different formats

## Your Tasks

### Part 1: Domain Model & Validation (15 min)

#### Task 1.1: Create Todo Model

Create the `Todo` class in `models` package with:

```java
public class Todo {
    private Long id;
    private String title;         // Required, 1-200 chars
    private String description;    // Optional, max 1000 chars
    private boolean completed;     // Default: false
    private Priority priority;     // Enum: HIGH, MEDIUM, LOW
    private LocalDateTime dueDate; // Optional
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;         // Owner of the todo
}
```

**Requirements:**
- Add Bean Validation annotations
- Create Priority enum
- Add Jackson annotations for date formatting (ISO-8601)
- Include getters/setters

**💡 Hint:** Use `@NotBlank`, `@Size`, `@JsonFormat` annotations

#### Task 1.2: Create DTO Classes

Create request/response DTOs:
- `CreateTodoRequest` - for POST requests (no id, timestamps)
- `UpdateTodoRequest` - for PUT requests (no id, userId, timestamps)
- `TodoResponse` - for responses (all fields)

### Part 2: REST Resource Implementation (30 min)

#### Task 2.1: Basic CRUD Operations

Implement `TodoResource` at `/api/todos`:

| Method | Path | Description | Status Code |
|--------|------|-------------|-------------|
| GET | /todos | List all todos for user | 200 |
| GET | /todos/{id} | Get specific todo | 200/404 |
| POST | /todos | Create new todo | 201 |
| PUT | /todos/{id} | Update todo | 200/404 |
| DELETE | /todos/{id} | Delete todo | 204/404 |
| PATCH | /todos/{id}/complete | Toggle completion | 200/404 |

**Security Requirements:**
- All endpoints require authentication (JWT)
- Users can only access their own todos
- Return 404 (not 403) if todo exists but belongs to another user

**💡 Hint:** Extract userId from SecurityContext

#### Task 2.2: Advanced Filtering

Add query parameters to GET /todos:

```
GET /api/todos?completed=true
GET /api/todos?priority=HIGH
GET /api/todos?dueBefore=2025-08-30
GET /api/todos?dueAfter=2025-08-25
GET /api/todos?search=shopping
```

Support combining filters:
```
GET /api/todos?completed=false&priority=HIGH&dueBefore=2025-08-30
```

### Part 3: Security Integration (15 min)

#### Task 3.1: User Context

Modify TodoResource to:
1. Extract userId from JWT token
2. Automatically set userId when creating todos
3. Filter queries by userId
4. Prevent access to other users' todos

```java
@Context
private SecurityContext securityContext;

private String getCurrentUserId() {
    JwtPrincipal principal = (JwtPrincipal) securityContext.getUserPrincipal();
    return principal.getUserId();
}
```

#### Task 3.2: Admin Capabilities

Add admin-only endpoints:
- `GET /api/admin/todos` - List ALL todos from all users
- `GET /api/admin/todos/stats` - Get statistics (total todos, by priority, etc.)

Use `@RolesAllowed("ADMIN")` annotation.

### Part 4: Jackson Customization (15 min)

#### Task 4.1: Custom Priority Serializer

Create a custom serializer for Priority enum that outputs:
- HIGH → { "value": "HIGH", "level": 3, "color": "#FF0000" }
- MEDIUM → { "value": "MEDIUM", "level": 2, "color": "#FFA500" }
- LOW → { "value": "LOW", "level": 1, "color": "#00FF00" }

#### Task 4.2: JSON Views

Implement two views:
- `Summary` view: id, title, completed, priority
- `Detailed` view: all fields

```java
public class Views {
    public static class Summary {}
    public static class Detailed extends Summary {}
}
```

Use views in endpoints:
- GET /todos - returns Summary view
- GET /todos/{id} - returns Detailed view

### Part 5: Error Handling & Validation (10 min)

#### Task 5.1: Custom Exceptions

Create and handle:
- `TodoNotFoundException` - returns 404
- `InvalidTodoStateException` - returns 400
- `TodoAccessDeniedException` - returns 404 (hide existence)

#### Task 5.2: Validation Error Responses

Ensure validation errors return structured responses:
```json
{
  "error": "Validation failed",
  "violations": [
    {
      "field": "title",
      "message": "must not be blank"
    },
    {
      "field": "priority",
      "message": "must be one of [HIGH, MEDIUM, LOW]"
    }
  ]
}
```

### Part 6: Testing (10 min)

#### Task 6.1: Integration Tests

Write tests for:
1. CRUD operations (happy path)
2. Security (401 without token, 404 for other users' todos)
3. Filtering (verify each filter works)
4. Validation (400 for invalid data)

Use REST Assured with JWT tokens.

### Part 7: Documentation (5 min)

#### Task 7.1: OpenAPI Annotations

Add OpenAPI annotations to TodoResource:
- `@Tag` for the resource
- `@Operation` for each endpoint
- `@ApiResponses` for status codes
- `@Parameter` for query parameters

## Running Your Implementation

### Start the Server
```bash
cd instructor-solution
./gradlew run
```

### Test with curl

Create a todo:
```bash
TOKEN="your-jwt-token"
curl -X POST http://localhost:8080/api/todos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete REST training",
    "description": "Finish all exercises",
    "priority": "HIGH",
    "dueDate": "2025-08-26T17:00:00"
  }'
```

List todos with filters:
```bash
curl -X GET "http://localhost:8080/api/todos?completed=false&priority=HIGH" \
  -H "Authorization: Bearer $TOKEN"
```

### Run Tests
```bash
./gradlew test --tests TodoResourceTest
```

## Expected Test Results

Your implementation should pass all these test scenarios:

✅ **CRUD Operations**
- Create todo returns 201 with Location header
- Get todo returns 200 with todo data
- Update todo returns 200 with updated data
- Delete todo returns 204
- Get non-existent todo returns 404

✅ **Security**
- Requests without token return 401
- User A cannot access User B's todos (404)
- Admin can access all todos

✅ **Filtering**
- Filter by completed status works
- Filter by priority works
- Date range filters work
- Combined filters work

✅ **Validation**
- Missing title returns 400
- Invalid priority returns 400
- Title over 200 chars returns 400

## Bonus Tasks (If Time Permits)

### Bonus 1: Batch Operations (15 min)
Add endpoints for:
- `POST /api/todos/batch` - Create multiple todos
- `DELETE /api/todos/batch` - Delete multiple todos by IDs
- `PATCH /api/todos/batch/complete` - Mark multiple as complete

### Bonus 2: Export Functionality (10 min)
Add export endpoint:
- `GET /api/todos/export?format=csv` - Export as CSV
- `GET /api/todos/export?format=json` - Export as JSON array
- Use `@Produces` with different media types

### Bonus 3: Sorting & Pagination (10 min)
Enhance GET /todos with:
- `?sort=dueDate,asc` or `?sort=priority,desc`
- `?page=0&size=10` for pagination
- Return pagination metadata in headers

## Common Mistakes to Avoid

1. ❌ **Forgetting to filter by userId** - Users see all todos
2. ❌ **Returning 403 instead of 404** - Reveals todo existence
3. ❌ **Not validating enum values** - Causes 500 errors
4. ❌ **Hardcoding dates without timezone** - Use ISO-8601
5. ❌ **Missing @Valid annotation** - Validation doesn't trigger
6. ❌ **Not setting Content-Type header** - Jersey rejects JSON
7. ❌ **Forgetting Location header** - POST should return it
8. ❌ **Not handling null filters** - NullPointerException

## Solution Checkpoint

By the end of this exercise, you should have:

- [ ] Todo model with validation and Jackson annotations
- [ ] TodoResource with all CRUD operations
- [ ] Security integration (JWT + user isolation)
- [ ] Filtering by multiple parameters
- [ ] Custom Priority serializer
- [ ] JSON Views (Summary/Detailed)
- [ ] Error handling with proper status codes
- [ ] At least 10 passing integration tests
- [ ] OpenAPI documentation annotations
- [ ] Admin-only endpoints

## Need Help?

If you're stuck:

1. **Model/Validation Issues**: Check Exercise 04 for validation patterns
2. **Security Problems**: Review Exercise 08 for JWT integration
3. **Jackson Issues**: See Exercises 06-07 for Jackson examples
4. **Filtering Logic**: Use Java 8 streams to filter collections
5. **Test Failures**: Check server logs for detailed errors
6. Ask the instructor for guidance

## Time Management Guide

- **0-15 min**: Create model and DTOs
- **15-45 min**: Implement basic CRUD + filtering
- **45-60 min**: Add security integration
- **60-75 min**: Jackson customization
- **75-85 min**: Error handling and testing
- **85-90 min**: Documentation and final testing

Remember: It's better to have a working basic implementation than a broken
advanced one. Focus on core functionality first!