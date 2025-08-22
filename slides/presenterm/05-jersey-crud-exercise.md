---
title: "Exercise 02: Jersey CRUD"
author: Thomas Becker
---

# Exercise 02

## Jersey CRUD Operations

**Duration:** 45-50 minutes  
**Goal:** Implement complete CRUD REST API

<!-- end_slide -->

## Your Mission

Transform skeleton `UserResource` into working REST API

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

<!-- speaker_note: Most common student errors -->

<!-- end_slide -->

## Testing Your Code

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

<!-- speaker_note: Check on slower students, help them catch up -->

<!-- end_slide -->

## Checkpoint: 40 Minutes

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

<!-- speaker_note: |
  - Answer questions
  - Ensure everyone has working code
  - Preview next exercise briefly
-->

<!-- end_slide -->