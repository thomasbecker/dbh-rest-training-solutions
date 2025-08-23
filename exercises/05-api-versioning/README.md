# Exercise 05: API Versioning

## Overview
**Duration:** 25 minutes  
**Objective:** Implement API versioning using the URI path strategy to support multiple API versions simultaneously

## Prerequisites
- Completed Exercise 04 (Bean Validation)
- Working User REST API with validation
- All tests passing

## Background
Your API has been in production for 6 months and is being used by multiple clients. The business now requires a breaking change: splitting the combined name fields into separate firstName and lastName fields. You need to implement this change without breaking existing clients.

## Your Tasks

### Task 1: Refactor to V1 (5 minutes)
**Objective:** Move your existing API to version 1

1. Rename `UserResource.java` to `UserResourceV1.java`
2. Change the class-level `@Path` annotation:
   ```java
   @Path("/v1/users")  // was "/users"
   ```
3. Update `JerseyConfig.java` to register the renamed class:
   ```java
   register(UserResourceV1.class);
   ```
4. Update all test paths from `/users` to `/v1/users`

**Verification:** Run tests - they should pass with new paths

### Task 2: Create UserV2 Model (5 minutes)
**Objective:** Create an evolved user model for version 2

Create a new `UserV2.java` class with these changes:
- Keep all existing fields (id, username, email, createdAt)
- Remove combined name fields if any
- Add separate `firstName` and `lastName` fields
- Add new optional `age` field (Integer)
- Add appropriate getters and setters

**Key Difference:** V2 uses separate name fields instead of combined

### Task 3: Create UserResourceV2 (10 minutes)
**Objective:** Implement version 2 of the REST API

1. Create `UserResourceV2.java` class
2. Set path to `@Path("/v2/users")`
3. Use `UserV2` model instead of `User`
4. Implement all CRUD operations (GET, POST, PUT, DELETE)
5. Handle the new field structure appropriately

**Tip:** Copy UserResourceV1 and modify for V2 structure

### Task 4: Add V2 Validation (3 minutes)
**Objective:** Add validation to UserV2 model

Add validation annotations to UserV2:
```java
@NotBlank(message = "First name is required")
@Size(min = 1, max = 50)
private String firstName;

@NotBlank(message = "Last name is required")
@Size(min = 1, max = 50)
private String lastName;

@Min(value = 0, message = "Age cannot be negative")
@Max(value = 150, message = "Age cannot exceed 150")
private Integer age;  // Optional field
```

### Task 5: Register Both Versions (2 minutes)
**Objective:** Enable both API versions simultaneously

Update `JerseyConfig.java`:
```java
public JerseyConfig() {
    // Register BOTH versions
    register(UserResourceV1.class);
    register(UserResourceV2.class);
    
    // Keep existing mappers and filters
    register(ValidationExceptionMapper.class);
    // ... other registrations
}
```

### Task 6: Add Deprecation Headers to V1 (Bonus - 5 minutes)
**Objective:** Inform clients that V1 is deprecated

Add headers to V1 responses:
```java
return Response.ok(users)
    .header("Sunset", "31 Dec 2024")
    .header("Deprecation", "true")
    .header("Link", "</api/v2/users>; rel=\"successor-version\"")
    .build();
```

## Running the Tests

Start your server:
```bash
./gradlew run
```

Test V1 (should work as before):
```bash
# GET all users
curl http://localhost:8080/api/v1/users

# POST new user (V1 structure)
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","firstName":"John","lastName":"Doe"}'
```

Test V2 (new structure):
```bash
# GET all users
curl http://localhost:8080/api/v2/users

# POST new user (V2 structure with age)
curl -X POST http://localhost:8080/api/v2/users \
  -H "Content-Type: application/json" \
  -d '{"username":"jane","email":"jane@example.com","firstName":"Jane","lastName":"Doe","age":30}'
```

Check deprecation headers:
```bash
curl -i http://localhost:8080/api/v1/users | grep -E "Sunset|Deprecation|Link"
```

## Expected Test Output

V1 Response (with headers):
```
HTTP/1.1 200 OK
Sunset: 31 Dec 2024
Deprecation: true
Link: </api/v2/users>; rel="successor-version"

[{"id":1,"username":"john","email":"john@example.com","firstName":"John","lastName":"Doe"}]
```

V2 Response:
```
HTTP/1.1 200 OK

[{"id":1,"username":"jane","email":"jane@example.com","firstName":"Jane","lastName":"Doe","age":30}]
```

## Hints

### Package Organization
Consider organizing your code:
```
resources/
├── v1/
│   └── UserResourceV1.java
└── v2/
    └── UserResourceV2.java
```

### Shared Logic
In production, you'd share business logic:
```java
public abstract class BaseUserResource {
    @Inject
    protected UserService userService;
    
    // Shared methods
}
```

### Version Detection
Clients can detect version from URL:
- `/api/v1/users` → Version 1
- `/api/v2/users` → Version 2

## Bonus Tasks (If Time Permits)

### Bonus 1: Migration Endpoint (10 minutes)
Create an admin endpoint to migrate V1 data to V2 format:
```java
@POST
@Path("/admin/migrate")
public Response migrateUsers() {
    // Copy V1 users to V2 format
    // Handle name field transformation
    // Return migration stats
}
```

### Bonus 2: Version Discovery (5 minutes)
Create a versions endpoint:
```java
@GET
@Path("/versions")
public Response getVersions() {
    // Return supported versions
    // Include deprecation info
    // Provide migration guidelines
}
```

### Bonus 3: Header Versioning (15 minutes)
Implement header-based versioning as alternative:
```java
@GET
@Path("/users")
public Response getUsers(@HeaderParam("Api-Version") String version) {
    if ("2".equals(version)) {
        return getUsersV2();
    }
    return getUsersV1();
}
```

## Helpful Resources

- [Jersey Documentation - Resources](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html)
- [REST API Versioning Best Practices](https://www.baeldung.com/rest-versioning)
- [Sunset HTTP Header RFC 8594](https://datatracker.ietf.org/doc/html/rfc8594)

## Common Mistakes to Avoid

1. ❌ **Forgetting to register both versions** - Both classes must be registered in JerseyConfig
2. ❌ **Path confusion** - Ensure paths are `/v1/users` not `/v1/user` or `/users/v1`
3. ❌ **Sharing static storage** - V1 and V2 should have separate storage in this exercise
4. ❌ **Missing validation** - V2 should have its own validation rules
5. ❌ **Not testing both versions** - Verify both work simultaneously

## Solution Checkpoint

By the end of this exercise, you should have:
- ✅ V1 API accessible at `/api/v1/users`
- ✅ V2 API accessible at `/api/v2/users`
- ✅ Both versions working simultaneously
- ✅ Different data models (User vs UserV2)
- ✅ Deprecation headers on V1 (bonus)
- ✅ All tests passing for both versions

## Need Help?

If you're stuck:
1. Check that both resources are registered in JerseyConfig
2. Verify your paths are exactly `/v1/users` and `/v2/users`
3. Ensure models have all required getters/setters
4. Check server logs for registration confirmations
5. Ask the instructor for clarification