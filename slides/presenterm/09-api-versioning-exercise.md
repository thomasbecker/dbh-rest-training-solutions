---
title: Exercise 05 - API Versioning
author: Thomas Becker
theme:
  name: dark

---

# Exercise 05: API Versioning

Implement versioned REST endpoints

⏱️ **Duration**: 25 minutes  
🎯 **Goal**: Create v1 and v2 of your User API

---

## Your Mission

Implement API versioning using URI path strategy:

1. Refactor existing API to v1
2. Create v2 with enhanced User model
3. Support both versions simultaneously
4. Add deprecation headers to v1

<!-- pause -->

💡 **Working from**: Your Exercise 04 solution

<!--
speaker_note: |
  EXERCISE INTRODUCTION (1 minute)
  
  • Prerequisites check:
    - "Who has validation working?"
    - "Anyone still debugging?"
    - Quick help if needed
  
  • Exercise overview:
    - Real-world scenario
    - Breaking change needed
    - Maintain compatibility
    - 25 minutes total
  
  • Pacing guidance:
    - 10 min: Refactor to v1
    - 10 min: Create v2
    - 5 min: Testing both
  
  • Support strategy:
    - Walk around at 5 min
    - Check progress at 15 min
    - Help anyone stuck
-->

<!-- end_slide -->

---

## Task 1: Refactor to V1

📚 **Docs**: JAX-RS @Path annotation

💡 **Hint**: Move existing endpoints to /v1 path

<!-- pause -->

### Current Structure
```java
@Path("/users")
public class UserResource {
    // Your existing implementation
}
```

<!-- pause -->

### Target Structure
```java
@Path("/v1/users")
public class UserResourceV1 {
    // Same implementation, new path
}
```

<!--
speaker_note: |
  REFACTORING TASK (5 minutes)
  
  • Simple rename:
    - UserResource → UserResourceV1
    - Path /users → /v1/users
    - Everything else stays same
  
  • Common issues:
    - Forgetting to update JerseyConfig
    - Tests still hitting old path
    - Location header in POST
  
  • Quick tip:
    - "Just rename and change path"
    - "Tests will fail - that's OK"
    - "We'll fix them next"
  
  • Verification:
    - Server should start
    - /api/v1/users should work
    - /api/users should 404
-->

<!-- end_slide -->

---

## Task 2: Update Configuration

### JerseyConfig.java

```java
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Update registration
        register(UserResourceV1.class);
        // Remove: register(UserResource.class);
        
        // Keep other registrations
        register(ValidationExceptionMapper.class);
    }
}
```

<!-- pause -->

### Update Tests

```java
// Change test paths from:
.get("/users")
// To:
.get("/v1/users")
```

<!--
speaker_note: |
  CONFIGURATION UPDATE (3 minutes)
  
  • Two changes needed:
    - JerseyConfig registration
    - Test base paths
  
  • JerseyConfig:
    - Register V1 class
    - Remove old class
    - Keep mappers
  
  • Test updates:
    - Find/replace /users
    - With /v1/users
    - Should be ~10 places
  
  • Verification:
    - Run tests
    - Should all pass again
    - Same functionality, new path
-->

<!-- end_slide -->

---

## Task 3: Create User V2 Model

📚 **Docs**: Domain model evolution

💡 **Hint**: V2 splits name into firstName/lastName

<!-- pause -->

### UserV2.java

```java
public class UserV2 {
    private Long id;
    private String username;
    private String email;
    
    // V2: Separate name fields (was combined in V1)
    private String firstName;
    private String lastName;
    
    // V2: New field
    private Integer age;
    
    private LocalDateTime createdAt;
    
    // Getters and setters...
}
```

<!--
speaker_note: |
  V2 MODEL (5 minutes)
  
  • What's changing:
    - Split name fields (breaking!)
    - Add age field (non-breaking)
    - Everything else same
  
  • Why this example:
    - Common real scenario
    - Shows breaking change
    - Requires data migration
  
  • Implementation tip:
    - Copy User class
    - Rename to UserV2
    - Modify fields
    - Add all getters/setters
  
  • Note:
    - In real world, might extend
    - Here, keep simple
    - Focus on versioning
-->

<!-- end_slide -->

---

## Task 4: Create UserResourceV2

### Basic Structure

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
    
    // Implement other CRUD operations with UserV2
}
```

<!--
speaker_note: |
  V2 RESOURCE (5 minutes)
  
  • Separate implementation:
    - New path /v2/users
    - Uses UserV2 model
    - Own storage (for demo)
  
  • In production:
    - Share service layer
    - Transform DTOs
    - Same database
  
  • Key differences:
    - Handles firstName/lastName
    - Includes age field
    - Different validation rules
  
  • Copy-paste OK:
    - Start from V1
    - Change to UserV2
    - Adjust for new fields
-->

<!-- end_slide -->

---

## Task 5: Handle Name Fields in V2

### POST Method Changes

```java
@POST
public Response createUser(@Valid UserV2 user) {
    Long id = idGenerator.getAndIncrement();
    user.setId(id);
    user.setCreatedAt(LocalDateTime.now());
    
    // V2 has firstName and lastName separate
    // No fullName field needed
    
    users.put(id, user);
    return created(user, id);
}
```

<!-- pause -->

💡 **Note**: V2 clients send firstName/lastName separately

<!--
speaker_note: |
  V2 FIELD HANDLING (3 minutes)
  
  • Main difference:
    - V1: Single name field
    - V2: firstName + lastName
    - Breaking change!
  
  • POST handling:
    - Accept separate fields
    - Validate both required
    - Store as-is
  
  • GET handling:
    - Return separate fields
    - No computed fullName
    - Clean structure
  
  • This is why versions:
    - Can't change V1
    - V2 gets clean design
    - Both coexist
-->

<!-- end_slide -->

---

## Task 6: Add Validation to V2

### UserV2 Validation

```java
public class UserV2 {
    // ... other fields ...
    
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50)
    private String lastName;
    
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 150, message = "Age cannot exceed 150")
    private Integer age;  // Optional field
}
```

<!--
speaker_note: |
  V2 VALIDATION (3 minutes)
  
  • Different rules:
    - firstName required
    - lastName required
    - age optional but validated
  
  • Validation evolution:
    - V1 might be looser
    - V2 can be stricter
    - Different business rules
  
  • Remember:
    - @Valid in resource methods
    - Same ValidationExceptionMapper
    - Works for both versions
-->

<!-- end_slide -->

---

## Task 7: Register Both Versions

### JerseyConfig.java

```java
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Register BOTH versions
        register(UserResourceV1.class);
        register(UserResourceV2.class);
        
        // Shared components
        register(ValidationExceptionMapper.class);
        register(CorsFilter.class);
        register(LoggingFilter.class);
    }
}
```

<!-- pause -->

Both versions now active simultaneously! 🎉

<!--
speaker_note: |
  DUAL REGISTRATION (2 minutes)
  
  • Key point:
    - Both versions active
    - Same server
    - Different paths
  
  • Jersey handles:
    - Routing by path
    - No conflicts
    - Clean separation
  
  • Shared components:
    - Exception mappers
    - Filters
    - Providers
  
  • Test this:
    - Start server
    - Try both /v1 and /v2
    - Should both work
-->

<!-- end_slide -->

---

## Task 8: Add Deprecation Headers

### Enhance V1 with Sunset Header

```java
@GET
@Path("/v1/users")
public Response getAllUsers() {
    return Response.ok(new ArrayList<>(users.values()))
        .header("Sunset", "31 Dec 2024")
        .header("Deprecation", "true")
        .header("Link", "</api/v2/users>; rel=\"successor-version\"")
        .build();
}
```

<!-- pause -->

Tells clients that V1 is deprecated! ⚠️

<!--
speaker_note: |
  DEPRECATION HEADERS (3 minutes)
  
  • Three headers:
    - Sunset: When it dies
    - Deprecation: Boolean flag
    - Link: Where to go
  
  • Best practice:
    - Add to all V1 endpoints
    - Consistent date
    - Clear successor
  
  • Client benefit:
    - Automated detection
    - Migration planning
    - Clear timeline
  
  • Could use filter:
    - Apply to all V1
    - Centralized logic
    - Easier maintenance
-->

<!-- end_slide -->

---

## ⏱️ Checkpoint - 15 minutes

### You should have:
- ✅ V1 API at `/api/v1/users`
- ✅ V2 API at `/api/v2/users`
- ✅ Both versions working
- ✅ Different models (User vs UserV2)

<!-- pause -->

### Quick Test:
```bash
# Test V1
curl http://localhost:8080/api/v1/users

# Test V2
curl http://localhost:8080/api/v2/users
```

<!--
speaker_note: |
  MIDPOINT CHECK (2 minutes)
  
  • Status check:
    - "Who has both versions?"
    - "Anyone getting 404s?"
    - "Validation working?"
  
  • Common problems:
    - Forgot to register V2
    - Path typos (/v2 not /v2/)
    - Model confusion
  
  • Troubleshooting:
    - Check JerseyConfig
    - Verify paths exactly
    - Restart server
  
  • Time management:
    - Fast group: Add migration
    - Slow group: Focus on basics
    - 10 minutes remaining
-->

<!-- end_slide -->

---

## Task 9: Test Both Versions

### Test V1 (Original Structure)
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username":"johndoe",
    "email":"john@example.com",
    "firstName":"John",
    "lastName":"Doe"
  }'
```

<!-- pause -->

### Test V2 (New Structure)
```bash
curl -X POST http://localhost:8080/api/v2/users \
  -H "Content-Type: application/json" \
  -d '{
    "username":"janedoe",
    "email":"jane@example.com",
    "firstName":"Jane",
    "lastName":"Doe",
    "age":30
  }'
```

<!--
speaker_note: |
  TESTING BOTH (3 minutes)
  
  • V1 testing:
    - Same as before
    - Should still work
    - Check deprecation headers
  
  • V2 testing:
    - New field structure
    - Age field included
    - Separate name fields
  
  • Verify headers:
    - V1 has Sunset header
    - V2 doesn't
    - Both return 201
  
  • Check responses:
    - V1 returns old structure
    - V2 returns new structure
    - IDs independent
-->

<!-- end_slide -->

---

## Bonus Task 1: Migration Endpoint

### Add Data Migration Helper

```java
@POST
@Path("/migrate")
public Response migrateV1ToV2() {
    List<User> v1Users = userResourceV1.getAllUsersInternal();
    
    for (User v1User : v1Users) {
        UserV2 v2User = new UserV2();
        v2User.setUsername(v1User.getUsername());
        v2User.setEmail(v1User.getEmail());
        v2User.setFirstName(v1User.getFirstName());
        v2User.setLastName(v1User.getLastName());
        // age defaults to null
        
        userResourceV2.importUser(v2User);
    }
    
    return ok("Migrated " + v1Users.size() + " users");
}
```

<!--
speaker_note: |
  BONUS: MIGRATION (2 minutes)
  
  • For fast finishers
  • Real-world need
  • Data migration strategy
  
  • Shows:
    - Field mapping
    - Default values
    - Batch processing
  
  • In production:
    - More sophisticated
    - Gradual migration
    - Rollback capability
-->

<!-- end_slide -->

---

## Bonus Task 2: Version Discovery

### Add Version Discovery Endpoint

```java
@Path("/versions")
public class VersionResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersions() {
        Map<String, Object> versions = new HashMap<>();
        
        versions.put("supported", Arrays.asList("v1", "v2"));
        versions.put("current", "v2");
        versions.put("deprecated", Arrays.asList("v1"));
        versions.put("sunset_dates", 
            Map.of("v1", "2024-12-31"));
        
        return Response.ok(versions).build();
    }
}
```

<!--
speaker_note: |
  BONUS: DISCOVERY (2 minutes)
  
  • Version discovery:
    - Clients can query
    - See what's available
    - Plan migration
  
  • Useful information:
    - Supported versions
    - Current/recommended
    - Deprecation info
    - Sunset dates
  
  • Best practice:
    - Machine readable
    - Well documented
    - Stable endpoint
-->

<!-- end_slide -->

---

## ⏱️ Final Checkpoint - 25 minutes

### Core Tasks Completed:
- ✅ Refactored to V1 structure
- ✅ Created V2 with new model
- ✅ Both versions operational
- ✅ Deprecation headers added
- ✅ All tests passing

<!-- pause -->

### Test Commands:
```bash
# List V1 users (with deprecation headers)
curl -i http://localhost:8080/api/v1/users

# List V2 users (no deprecation)
curl -i http://localhost:8080/api/v2/users
```

<!--
speaker_note: |
  FINAL CHECK (2 minutes)
  
  • Success criteria:
    - Both versions respond
    - V1 has headers
    - V2 has new structure
    - No conflicts
  
  • Quick poll:
    - "Who got it working?"
    - "See the headers?"
    - "Anyone do bonus?"
  
  • Key learning:
    - Versioning complexity
    - Maintenance burden
    - Why it's needed
  
  • Wrap up:
    - "Great job!"
    - "You've versioned an API!"
    - "Real-world skill!"
-->

<!-- end_slide -->

---

## Common Issues & Solutions

### Both Versions Not Working?
- Check JerseyConfig registers both
- Verify exact paths (/v1/ vs /v1)
- Restart server after changes

<!-- pause -->

### Validation Errors in V2?
- Different validation rules
- Check required fields
- firstName/lastName both needed

<!-- pause -->

### Headers Not Showing?
- Use `curl -i` to see headers
- Add to all V1 methods
- Check Response.header() calls

<!--
speaker_note: |
  TROUBLESHOOTING (1 minute)
  
  • Quick fixes:
    - Registration issues
    - Path problems
    - Validation differences
  
  • Header visibility:
    - curl -i flag
    - Postman shows them
    - Browser dev tools
  
  • Keep available:
    - Reference during exercise
    - Quick solutions
    - Reduce frustration
-->

<!-- end_slide -->

---

## What You Learned

### ✅ Implemented URI Versioning
- Created parallel versions
- Maintained compatibility
- Clean separation

<!-- pause -->

### ✅ Deprecation Strategy
- Sunset headers
- Link to successor
- Clear communication

<!-- pause -->

### ✅ Real-World Pattern
- Breaking changes handled
- Gradual migration path
- Both versions coexist

<!--
speaker_note: |
  LEARNING SUMMARY (1 minute)
  
  • Key skills:
    - Version strategy implementation
    - Deprecation handling
    - Parallel maintenance
  
  • Real-world applicable:
    - Common pattern
    - Industry standard
    - Portfolio worthy
  
  • Trade-offs experienced:
    - Code duplication
    - Maintenance burden
    - But compatibility maintained
-->

<!-- end_slide -->

---

## Excellent Work! 🎉

You've successfully implemented API versioning!

### Your API now:
- ✅ Supports multiple versions
- ✅ Handles breaking changes gracefully
- ✅ Provides clear migration path
- ✅ Follows REST best practices

<!-- pause -->

### Next Up: Spring Boot Overview

How does Spring Boot change the game?

<!--
speaker_note: |
  CONGRATULATIONS (1 minute)
  
  • Celebrate success:
    - "Complex topic mastered!"
    - "Production-ready pattern!"
    - "Real-world skill!"
  
  • What they achieved:
    - Version management
    - Deprecation strategy
    - Migration path
    - Professional API
  
  • Transition:
    - "Next: Spring Boot"
    - "See the difference"
    - "Jersey with/without"
    - "30 minute overview"
  
  • Break timing:
    - Check schedule
    - Maybe short break?
    - Day 1 almost done!
-->

<!-- end_slide -->