# Instructor Notes - Exercise 05: API Versioning

## Overview
This exercise teaches API versioning through hands-on implementation of the URI path strategy. Students will maintain two versions of their API simultaneously, experiencing the complexity and trade-offs firsthand.

## Time Management (25 minutes)
- **Minutes 0-5:** Introduction and Task 1 (Refactor to V1)
- **Minutes 5-10:** Task 2-3 (Create V2 model and resource)
- **Minutes 10-15:** Task 4-5 (Validation and registration)
- **Minutes 15-20:** Testing both versions
- **Minutes 20-25:** Deprecation headers and wrap-up

## Key Teaching Points

### Why This Exercise Matters
- Real-world scenario every developer faces
- Shows the complexity of maintaining multiple versions
- Demonstrates why careful API design matters upfront
- Illustrates the cost of breaking changes

### Concepts to Emphasize
1. **Breaking Changes:** firstName/lastName split is intentionally breaking
2. **Parallel Versions:** Both must work simultaneously
3. **Deprecation Strategy:** Professional way to sunset versions
4. **Migration Path:** How clients transition between versions

## Common Issues and Solutions

### Issue 1: Tests Failing After Refactor
**Problem:** Students forget to update test paths
**Solution:** 
```bash
# Quick fix - find and replace in test file
/users → /v1/users
```

### Issue 2: 404 Errors on V2
**Problem:** Forgot to register UserResourceV2 in JerseyConfig
**Solution:**
```java
register(UserResourceV1.class);
register(UserResourceV2.class);  // Don't forget this!
```

### Issue 3: Validation Not Working in V2
**Problem:** Forgot @Valid annotation in V2 resource methods
**Solution:** Ensure @Valid is present:
```java
public Response createUser(@Valid UserV2 user)
```

### Issue 4: Both Versions Using Same Storage
**Problem:** Static map shared between versions
**Solution:** Each version should have its own map (for this exercise)

### Issue 5: Deprecation Headers Not Visible
**Problem:** Using curl without -i flag
**Solution:** 
```bash
curl -i http://localhost:8080/api/v1/users
```

## Quick Solutions

### Minimum Working V2 Resource
```java
@Path("/v2/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResourceV2 extends AbstractResource {
    private static final Map<Long, UserV2> users = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    @GET
    public Response getAllUsers() {
        return ok(new ArrayList<>(users.values()));
    }
    
    @POST
    public Response createUser(@Valid UserV2 user) {
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        users.put(id, user);
        return created(user, id);
    }
}
```

## Discussion Points

### After Exercise Completion
1. **Ask:** "What was the biggest challenge?"
   - Expected: Code duplication, maintaining consistency

2. **Ask:** "How would you handle 5 versions?"
   - Lead to discussion about version limits

3. **Ask:** "What about database migrations?"
   - Real-world complexity beyond this exercise

4. **Ask:** "Which versioning strategy would you choose?"
   - Relate back to lecture content

## Extension for Fast Finishers

### Advanced Challenge: Adapter Pattern
```java
public class UserAdapter {
    public static UserV2 fromV1(User v1) {
        UserV2 v2 = new UserV2();
        v2.setFirstName(v1.getFirstName());
        v2.setLastName(v1.getLastName());
        // ... map other fields
        return v2;
    }
}
```

### Advanced Challenge: Shared Service Layer
```java
@Path("/v2/users")
public class UserResourceV2 {
    @Inject
    private UserService userService;  // Shared
    
    @GET
    public Response getAllUsers() {
        return ok(userService.findAll()
            .stream()
            .map(UserV2DTO::fromDomain)
            .collect(Collectors.toList()));
    }
}
```

## Key Takeaways to Reinforce

1. **Versioning is Complex**
   - Not just code, but process and communication
   - Affects entire organization

2. **URI Versioning Trade-offs**
   - Simple but duplicative
   - Clear but rigid

3. **Deprecation is Critical**
   - Can't support versions forever
   - Must communicate clearly

4. **Design Carefully Upfront**
   - Easier to add fields than change/remove
   - Consider evolution from day one

## Troubleshooting Guide

| Symptom | Likely Cause | Quick Fix |
|---------|--------------|-----------|
| 404 on /v1/users | Path typo | Check exact path in @Path |
| 404 on /v2/users | Not registered | Add to JerseyConfig |
| 500 on POST | Missing field | Check all required fields |
| No headers visible | curl missing -i | Use curl -i |
| V2 validation fails | Missing @Valid | Add to method parameter |

## Success Criteria

Students have succeeded when:
- Both /api/v1/users and /api/v2/users respond
- Can POST different structures to each version
- Deprecation headers appear on V1 (bonus)
- Understand the maintenance burden
- Can articulate versioning trade-offs

## Notes for Next Time

- Consider adding database layer to show migration complexity
- Could demonstrate header versioning as comparison
- Might show GraphQL as alternative approach
- Consider adding metrics to show version usage