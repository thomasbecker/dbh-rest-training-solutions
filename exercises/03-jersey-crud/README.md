# Exercise 03: Jersey CRUD Operations

**Time:** 45-50 minutes  
**Objective:** Implement a complete CRUD REST API using Jersey with TDD approach

## Background

You'll implement a User resource with full CRUD operations. We've provided REST Assured tests that define the expected behavior - your goal is to make all tests pass!

## Prerequisites

- Completed project setup
- Running Jersey server
- REST Assured tests are in `src/test/java/com/dbh/training/rest/resources/UserResourceTest.java`

## Your Tasks

### Task 1: Review the User Model (5 minutes)

The User model is already provided with all fields and getters/setters to save time.
Open `src/main/java/com/dbh/training/rest/models/User.java` and familiarize yourself with it.

### Task 2: Add JAX-RS Annotations & Implement GET Operations (15 minutes)

In `src/main/java/com/dbh/training/rest/resources/UserResource.java`:

**First, add necessary class-level annotations for:**

- Resource path mapping
- Content type handling (JSON)

**Then implement:**

1. **GET /users** - Return all users (200 OK)
2. **GET /users/{id}** - Return specific user (200 OK) or 404 if not found

**Need help?** Check the JAX-RS documentation:
https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html

### Task 3: Implement POST Operation (10 minutes)

**POST /users**

- Accept: User JSON (without ID)
- Generate unique ID for the user
- Set creation timestamp
- Return: 201 Created with Location header
- Response should include the created user

**Tip:** Use the `created(entity, id)` helper method from AbstractResource for proper Location header generation

### Task 4: Implement PUT Operation (10 minutes)

**PUT /users/{id}**

- Accept: Complete User JSON
- Update existing user
- Return: 200 OK with updated user or 404 if not found

### Task 5: Implement DELETE Operation (5 minutes)

**DELETE /users/{id}**

- Remove user from storage
- Return: 204 No Content if deleted, 404 if not found

## Running the Tests

The tests are already written using REST Assured. Run them to verify your implementation:

```bash
./gradlew test --tests UserResourceTest
```

### Test-Driven Development Flow:

1. Run tests - see them fail (RED)
2. Implement the functionality (GREEN)
3. Refactor if needed (REFACTOR)
4. Repeat for each operation

### Important: Test Independence

Each test is completely independent:

- Tests create their own test data using a helper method
- `@BeforeEach` calls `UserResource.resetForTesting()` to clean state
- Tests can run in any order
- No test depends on data from another test

The `resetForTesting()` method is package-private (no access modifier), meaning:

- Only accessible from the same package (tests are in same package)
- Not exposed in the public API
- Common pattern for test utilities
- Safer than exposing test endpoints

This teaches best practices for REST API testing!

## Expected Test Output

When all tests pass, you should see:

```
UserResourceTest
  ✓ testGetAllUsers
  ✓ testGetUserById
  ✓ testGetUserByIdNotFound
  ✓ testCreateUser
  ✓ testUpdateUser
  ✓ testUpdateUserNotFound
  ✓ testDeleteUser
  ✓ testDeleteUserNotFound
```

## Hints

- Use `ConcurrentHashMap<Long, User>` for in-memory storage
- Use `AtomicLong` for ID generation
- Remember to set proper `@Produces` and `@Consumes` annotations
- Return `Response` objects for better control over status codes
- For 404 errors, include a message: `Response.status(404).entity("User not found").build()`
- Use AbstractResource helper methods: `ok()`, `created()`, `noContent()`

## Bonus Tasks (if you finish early)

### Bonus 1: Add Pagination (10 minutes)

- Add query parameters: `?page=1&size=10`
- Return paginated results with headers:
  - `X-Total-Count`: Total number of users
  - `X-Page`: Current page
  - `X-Page-Size`: Items per page

### Bonus 2: Add Filtering (10 minutes)

- Support filtering by username: `/users?username=john`
- Support filtering by email domain: `/users?emailDomain=example.com`

### Bonus 3: Implement PATCH (10 minutes)

- Path: `/users/{id}`
- Accept: Partial User JSON
- Only update provided fields
- Return: Updated user
- Status: 200 OK

### Bonus 4: Add Search (5 minutes)

- Path: `/users/search?q=term`
- Search in username, firstName, and lastName
- Return matching users

## Helpful Resources

**JAX-RS Annotations:**

- [Jersey Resources Documentation](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html)
- [JAX-RS Quick Reference](https://docs.oracle.com/javaee/7/tutorial/jaxrs002.htm)

**Response Building:**

- [Response class JavaDoc](https://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Response.html)
- Common patterns: `Response.ok()`, `Response.created()`, `Response.noContent()`, `Response.status(404)`

**Need a hint?**
Look at `ExampleResource.java` for annotation examples, but try to figure it out yourself first!

## Common Mistakes to Avoid

1. ❌ Forgetting `@Path` annotation on the class
2. ❌ Missing `@Produces`/`@Consumes` annotations
3. ❌ Returning null instead of proper Response
4. ❌ Forgetting `@PathParam` for path parameters
5. ❌ Not handling the 404 case properly

## Solution Checkpoint

After implementing all CRUD operations, you should have:

- [ ] User model with all fields
- [ ] GET /users returning list
- [ ] GET /users/{id} with 404 handling
- [ ] POST /users creating new users with IDs
- [ ] PUT /users/{id} updating existing users
- [ ] DELETE /users/{id} removing users
- [ ] All 8 tests passing

## Need Help?

If you get stuck:

1. Check the test file to understand expected behavior
2. Look at error messages from failing tests
3. Review the hints section
4. Ask the instructor

Remember: The tests define the contract - make them pass!
