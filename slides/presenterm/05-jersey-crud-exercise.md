---
title: "Exercise 02: Jersey CRUD"
author: Thomas Becker
---

# Exercise 03

## Jersey CRUD Operations

**Duration:** 45-50 minutes  
**Goal:** Implement complete CRUD REST API

<!--
speaker_note: |
  EXERCISE INTRODUCTION (3 minutes)

  • Set the stage:
    - "Now we code!"
    - "Test-driven approach"
    - "I'll guide you through"

  • Logistics:
    - 45-50 minutes total
    - Tests are pre-written
    - Make them pass
    - Pair programming encouraged

  • Success criteria:
    - 9 tests passing
    - Server runs
    - Postman/curl works

  • My role:
    - Circulate and help
    - Answer questions
    - Keep pace
-->

<!-- end_slide -->

## Your Mission

Transform skeleton `UserResource` into working REST API

<!--
speaker_note: |
  MISSION BRIEFING (2 minutes)

  • The challenge:
    - Skeleton code exists
    - Tests are written
    - Make them pass

  • TDD approach:
    - Red: Tests fail now
    - Green: Make them pass
    - No refactor needed yet

  • Start command:
    - Open UserResource.java
    - Run tests first
    - See failures
    - Fix one by one
-->

<!-- pause -->

```bash
./gradlew test --tests UserResourceTest
```

<!-- pause -->

✅ Make all 9 tests pass!

<!-- end_slide -->

## Task 1: Review User Model

📚 Already provided to save time!

💡 **Hint:** Familiarize yourself with the fields

<!--
speaker_note: |
  USER MODEL REVIEW (2 minutes)

  • Why provided:
    - Save typing time
    - Focus on REST logic
    - Standard POJO

  • Important fields:
    - id: Long (generated)
    - username: unique identifier
    - email: for validation later
    - createdAt: timestamp

  • All ready:
    - Getters/setters done
    - Constructors done
    - Just use it

  • Move quickly:
    - "Don't modify User"
    - "Focus on Resource"
-->

<!-- pause -->

```java
public class User {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
}
```

<!-- pause -->

✅ All getters/setters ready to use

<!-- end_slide -->

## Task 2: JAX-RS Class Setup

📚 [Jersey Docs: @Path](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html#d0e2040)

💡 **Hint:** Start with class-level annotations

<!--
speaker_note: |
  CLASS SETUP (3 minutes)

  • Three annotations needed:
    - @Path("/users") - base URL
    - @Produces - what we send
    - @Consumes - what we accept

  • MediaType.APPLICATION_JSON:
    - Standard constant
    - "application/json"
    - Most common today

  • Inheritance:
    - extends AbstractResource
    - Provides helpers
    - ok(), created(), etc.

  • Let them type:
    - Don't show immediately
    - Let them try first
    - Help if stuck
-->

<!-- pause -->

```java
@Path("/users")
```

<!-- pause -->

```java
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
```

<!-- pause -->

```java
public class UserResource extends AbstractResource {
```

<!-- end_slide -->

## Task 2a: GET All Users

📚 [Jersey Docs: @GET](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html#d0e2183)

💡 **Hint:** Return all users from the map

<!--
speaker_note: |
  GET ALL USERS (4 minutes)

  • Simplest operation:
    - Just @GET annotation
    - No parameters
    - Return collection

  • Map to List:
    - users.values() gives Collection
    - Wrap in ArrayList
    - Jackson serializes to JSON

  • Helper method:
    - ok() from AbstractResource
    - Sets 200 status
    - Sets entity

  • Common mistake:
    - Returning Map directly
    - Want array in JSON
    - Not object

  • Test this first:
    - Easy win
    - Builds confidence
-->

<!-- pause -->

```java
@GET
public Response getAllUsers() {
```

<!-- pause -->

```java
    return ok(new ArrayList<>(users.values()));
}
```

<!-- pause -->

**Returns:** `200 OK` with JSON array

<!-- end_slide -->

## Task 2b: GET User by ID

📚 [Jersey Docs: @PathParam](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html#d0e2271)

💡 **Hint:** Check if user exists, return 404 with message

<!--
speaker_note: |
  GET BY ID (5 minutes)

  • Path parameter:
    - @Path("/{id}")
    - @PathParam("id") Long id
    - Jersey converts String to Long

  • Null checking:
    - ALWAYS check if exists
    - Return 404 if not
    - Include message why

  • Response building:
    - status(404) for not found
    - entity() for body
    - build() to create

  • Success case:
    - Just ok(user)
    - 200 with JSON

  • Let them struggle:
    - PathParam syntax tricky
    - Help after 2 minutes
-->

<!-- pause -->

```java
@GET
@Path("/{id}")
public Response getUserById(@PathParam("id") Long id) {
```

<!-- pause -->

```java
    User user = users.get(id);
    if (user == null) {
        return Response.status(404)
                   .entity("User not found").build();
    }
```

<!-- pause -->

```java
    return ok(user);  // Using AbstractResource helper
}
```

<!-- end_slide -->

## Task 3: POST Create User (Part 1)

📚 [Jersey Docs: @POST](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html#d0e2183)

💡 **Hint:** Generate ID, set timestamp, store user

<!--
speaker_note: |
  POST PART 1 (5 minutes)

  • ID generation:
    - AtomicLong for thread safety
    - getAndIncrement()
    - Set on user object

  • Timestamp:
    - LocalDateTime.now()
    - Server-side generation
    - Don't trust client

  • Storage:
    - ConcurrentHashMap
    - Thread-safe
    - put(id, user)

  • Common issues:
    - Forgetting to set ID
    - Not setting timestamp
    - Using user-provided ID

  • Pause here:
    - Let them code
    - Check understanding
-->

<!-- pause -->

```java
@POST
public Response createUser(User user) {
```

<!-- pause -->

```java
    // Generate ID
    Long id = idGenerator.getAndIncrement();
    user.setId(id);
```

<!-- pause -->

```java
    // Set timestamp
    user.setCreatedAt(LocalDateTime.now());
```

<!-- end_slide -->

## Task 3: POST Create User (Part 2)

📚 [Jersey Docs: AbstractResource Helper Methods]

💡 **Hint:** Use the created() helper method!

<!--
speaker_note: |
  POST PART 2 (3 minutes)

  • Location header:
    - Required for 201
    - Points to new resource
    - /api/users/123

  • Helper method:
    - created(user, id)
    - Builds URI dynamically
    - Sets Location header
    - Returns 201

  • Why 201 not 200:
    - 201 = Created
    - 200 = OK (but not specific)
    - REST convention

  • Test verification:
    - Checks status code
    - Checks Location header
    - Checks response body
-->

<!-- pause -->

```java
    // Store user
    users.put(id, user);
```

<!-- pause -->

```java
    // Return 201 with Location header
    // AbstractResource helper builds dynamic URI
    return created(user, id);
}
```

<!-- end_slide -->

## Task 4: PUT Update User

📚 [Jersey Docs: @PUT](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html#d0e2183)

💡 **Hint:** Check existence first, maintain ID consistency

<!--
speaker_note: |
  PUT UPDATE (5 minutes)

  • Two parameters:
    - Path ID
    - Request body User
    - Must match!

  • Existence check:
    - containsKey(id)
    - Return 404 if missing
    - Same as GET pattern

  • ID consistency:
    - ALWAYS use path ID
    - Ignore body ID
    - Security best practice

  • Replace entirely:
    - PUT = full replacement
    - Not partial update
    - That's PATCH

  • Return updated:
    - 200 OK
    - Full object
    - Confirm changes
-->

<!-- pause -->

```java
@PUT
@Path("/{id}")
public Response updateUser(
    @PathParam("id") Long id, User user) {
```

<!-- pause -->

```java
    if (!users.containsKey(id)) {
        return Response.status(404)
                   .entity("User not found").build();
    }
```

<!-- pause -->

```java
    user.setId(id);  // Ensure ID consistency
    users.put(id, user);
    return ok(user);
}
```

<!-- end_slide -->

## Task 5: DELETE User

📚 [Jersey Docs: @DELETE](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/jaxrs-resources.html#d0e2183)

💡 **Hint:** Return 204 on success, 404 if not found

<!--
speaker_note: |
  DELETE USER (4 minutes)

  • Remove operation:
    - remove(id) returns old value
    - null if not found
    - Use for checking

  • Status codes:
    - 204 = No Content (success)
    - 404 = Not Found
    - Never 200 for DELETE

  • No response body:
    - 204 has no body
    - That's the spec
    - noContent() helper

  • Idempotency note:
    - Second DELETE = 404
    - That's OK
    - Idempotent operation

  • Almost done:
    - Last operation
    - Run all tests!
-->

<!-- pause -->

```java
@DELETE
@Path("/{id}")
public Response deleteUser(@PathParam("id") Long id) {
```

<!-- pause -->

```java
    User removed = users.remove(id);
```

<!-- pause -->

```java
    if (removed == null) {
        return Response.status(404)
                   .entity("User not found").build();
    }
    return noContent();  // 204 - Using helper
}
```

<!-- end_slide -->

## HTTP Status Codes

📚 [HTTP Status Codes Reference](https://httpstatuses.com/)

<!--
speaker_note: |
  STATUS CODES REFERENCE (2 minutes)

  • Keep visible:
    - Reference during coding
    - Common mistakes here

  • Success codes:
    - 200: General success
    - 201: Created (POST only)
    - 204: No content (DELETE)

  • Why different codes:
    - Semantic meaning
    - Client behavior
    - Caching decisions

  • Industry standard:
    - Not Jersey specific
    - All REST APIs
    - Learn once, use everywhere
-->

**Success Cases:**

| Operation | Status | Meaning    |
| --------- | ------ | ---------- |
| GET all   | 200    | OK         |
| GET one   | 200    | OK         |
| POST      | 201    | Created    |
| PUT       | 200    | OK         |
| DELETE    | 204    | No Content |

<!-- end_slide -->

## HTTP Status Codes - Errors

**Error Cases:**

| Operation | Status | Meaning   |
| --------- | ------ | --------- |
| GET one   | 404    | Not Found |
| PUT       | 404    | Not Found |
| DELETE    | 404    | Not Found |

<!-- pause -->

⚠️ Never return 500 for "not found"!

<!-- end_slide -->

## Common Mistakes

❌ Returning 500 instead of 404

<!-- pause -->

❌ Missing Location header in POST

<!-- pause -->

❌ Forgetting `/api` prefix in Location

<!-- pause -->

❌ Not setting generated ID

<!--
speaker_note: |
  COMMON MISTAKES (2 minutes)

  • Error 1: 500 vs 404
    - "Server error vs Not Found"
    - 500 = your code broke
    - 404 = resource missing
    - Big difference!

  • Error 2: Location header
    - POST must return Location
    - Points to created resource
    - Tests check this!

  • Error 3: /api prefix
    - Location needs full path
    - /api/users/123 not /users/123
    - AbstractResource handles this

  • Error 4: ID not set
    - Generate before storing
    - Return same ID in response
    - Consistency critical

  • Prevention:
    - Run tests often
    - Read error messages
    - Ask if stuck!
-->

<!-- end_slide -->

## Testing Your Code

<!--
speaker_note: |
  TESTING GUIDANCE (3 minutes)

  • Test-driven approach:
    - Run tests first
    - See red
    - Make green
    - Repeat

  • Gradle command:
    - --tests flag
    - Run specific test
    - Faster feedback

  • Manual testing:
    - Server must run
    - Different terminal
    - curl or Postman

  • Both important:
    - Tests = contract
    - Manual = confidence
    - Do both!
-->

**Run tests frequently:**

```bash
./gradlew test --tests UserResourceTest
```

<!-- pause -->

**Manual testing:**

```bash
curl http://localhost:8080/api/users
```

<!-- pause -->

**Server must be running:**

```bash
./gradlew run
```

<!-- end_slide -->

## Checkpoint: 20 Minutes

**Should have completed:**

<!-- pause -->

✅ GET all users

<!-- pause -->

✅ GET user by ID

<!-- pause -->

✅ POST create user

<!--
speaker_note: |
  20-MINUTE CHECKPOINT (2 minutes)

  • Stop and check:
    - "How many have GET working?"
    - "Anyone stuck on POST?"
    - "Need help with IDs?"

  • Common issues at 20min:
    - Annotations wrong
    - ID generation missing
    - Response builder confusion

  • If many behind:
    - Slow pace slightly
    - Pair fast with slow
    - Show solution snippet

  • If most done:
    - "Great progress!"
    - "Keep going"
    - "PUT/DELETE similar"

  • Encourage:
    - "You're doing great"
    - "Normal to struggle"
    - "Tests help guide you"
-->

<!-- end_slide -->

## Checkpoint: 40 Minutes

<!--
speaker_note: |
  40-MINUTE CHECKPOINT (2 minutes)

  • Final check:
    - "Who's done?"
    - "Who needs 5 more minutes?"
    - "Anyone stuck?"

  • If most done:
    - Show bonus tasks
    - Challenge fast finishers
    - Help others finish

  • If many struggling:
    - Show solution
    - Walk through together
    - Explain patterns

  • Key accomplishment:
    - "You built a REST API!"
    - "From scratch!"
    - "Industry patterns!"

  • Time management:
    - 5-10 minutes left
    - Wrap up soon
    - Next exercise ready?
-->

**All operations working:**

<!-- pause -->

✅ All CRUD operations

<!-- pause -->

✅ Proper status codes (200, 201, 204, 404)

<!-- pause -->

✅ Error handling for missing resources

<!-- end_slide -->

## Bonus Tasks

**For fast finishers:**

<!--
speaker_note: |
  BONUS TASKS (2 minutes)

  • Only if time:
    - Some finish in 30 min
    - Need challenges
    - Keep engaged

  • Pagination:
    - @QueryParam
    - Slice the list
    - Real-world need

  • Filtering:
    - Stream API
    - filter() method
    - Practical skill

  • PATCH:
    - Partial updates
    - More complex
    - Optional in REST

  • Encourage exploration:
    - "Try one!"
    - "Tests provided"
    - "Great learning"
-->

<!-- pause -->

1. **Pagination**: Add `?page=1&size=10` support

<!-- pause -->

2. **Filtering**: Add `?username=john` filter

<!-- pause -->

3. **PATCH**: Implement partial updates

<!-- pause -->

Uncomment bonus tests in `UserResourceTest`!

<!-- end_slide -->

## Key Takeaways

<!--
speaker_note: |
  KEY TAKEAWAYS (2 minutes)

  • Reinforce learning:

  • Annotations matter:
    - @GET, @POST, @PUT, @DELETE
    - @Path, @PathParam
    - Define your API

  • Status codes critical:
    - Not random numbers
    - Have meaning
    - Affect client behavior

  • Error handling:
    - Always check existence
    - Return appropriate errors
    - Include messages

  • Location header:
    - POST requirement
    - Full URI needed
    - REST convention

  • Tests as documentation:
    - Define behavior
    - Ensure correctness
    - Enable refactoring

  • You did it:
    - "Real REST API!"
    - "Industry patterns!"
    - "Without Spring Boot!"
-->

✅ JAX-RS annotations define your REST API

<!-- pause -->

✅ HTTP status codes matter (200, 201, 204, 404)

<!-- pause -->

✅ Always handle "not found" case

<!-- pause -->

✅ Location header required for POST

<!-- pause -->

✅ Tests are your contract

<!-- end_slide -->

## Questions?

**Next:** Exercise 03 - Bean Validation

<!--
speaker_note: |
  EXERCISE WRAP-UP (5 minutes)

  • Check completion:
    - "Who got all tests passing?"
    - "Anyone close?"
    - "What was hardest part?"

  • Quick review:
    - Show solution if needed
    - Highlight key patterns
    - Explain design choices

  • Common struggles:
    - Response builder syntax
    - PathParam confusion
    - Status code choices

  • Validation preview:
    - "Next: Bean Validation"
    - "Add constraints to User"
    - "@NotNull, @Size, @Email"
    - "30 minutes"

  • Break timing:
    - Should be ~14:45
    - 15-minute break?
    - Or continue?

  • Encouragement:
    - "You built a REST API!"
    - "Without Spring Boot!"
    - "Production patterns!"
-->

<!-- end_slide -->