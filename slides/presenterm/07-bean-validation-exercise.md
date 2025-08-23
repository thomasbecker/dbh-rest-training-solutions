---
title: Exercise 04 - Bean Validation
author: Thomas Becker
theme:
  name: dark

---

# Exercise 04: Bean Validation

Add validation to your REST API

⏱️ **Duration**: 20 minutes  
🎯 **Goal**: Implement input validation using Bean Validation

---

## Your Mission

Enhance the User REST API with proper validation:

1. Add validation annotations to the User model
2. Enable validation in REST endpoints
3. Create custom error responses
4. Test with invalid data

<!-- pause -->

💡 **Working from**: Your Exercise 03 solution

<!--
speaker_note: |
  EXERCISE INTRODUCTION (1 minute)
  
  • Prerequisites check:
    - "Who has all CRUD tests passing?"
    - "Anyone still debugging?"
    - Help strugglers quickly
  
  • Exercise overview:
    - Build on existing code
    - Add validation layer
    - Improve error messages
    - 20 minutes total
  
  • Pacing guidance:
    - 10 min: Basic validation
    - 10 min: Error handling
    - Bonus if time permits
  
  • Support strategy:
    - Walk around at 5 min
    - Check progress at 10 min
    - Help stuck students
-->

<!-- end_slide -->

---

## Task 1: Add Dependencies

📚 **Docs**: Jersey Bean Validation Guide

💡 **Hint**: You need three dependencies

<!-- pause -->

### Update build.gradle

```gradle
dependencies {
    // Existing dependencies...
    
    // Bean Validation
    implementation 'org.glassfish.jersey.ext:jersey-bean-validation:2.35'
    implementation 'org.hibernate.validator:hibernate-validator:6.2.5.Final'
    implementation 'org.glassfish:javax.el:3.0.0'
}
```

<!-- pause -->

Run: `./gradlew build`

<!--
speaker_note: |
  DEPENDENCIES TASK (2 minutes)
  
  • Copy-paste friendly:
    - Have slide visible
    - Let them copy exactly
    - Versions are important
  
  • Common issues:
    - Wrong Hibernate version (7.x)
    - Missing javax.el
    - Typos in group IDs
  
  • Verification:
    - "Everyone run gradle build"
    - "Any errors?"
    - Fix before moving on
  
  • Quick explanation:
    - Jersey extension: integration
    - Hibernate: validation engine
    - EL: error messages
-->

<!-- end_slide -->

---

## Task 2: Add Validation to User Model

📚 **Docs**: `javax.validation.constraints` package

💡 **Hint**: Username, email, and age need validation

<!-- pause -->

### User.java Validations

```java
import javax.validation.constraints.*;

public class User {
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 150, message = "Age cannot exceed 150")
    private Integer age;
}
```

<!-- speaker_note:
5 minutes for this task
Explain each annotation briefly
Show how message attribute works
-->

<!-- end_slide -->

---

## Task 3: Enable Validation in Resources

📚 **Docs**: `@Valid` annotation

💡 **Hint**: Add to POST and PUT methods

<!-- pause -->

### UserResource.java

```java
import javax.validation.Valid;

@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response createUser(@Valid User user) {
    User created = userService.save(user);
    return Response.status(Response.Status.CREATED)
                   .entity(created)
                   .build();
}

@PUT
@Path("/{id}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response updateUser(@PathParam("id") Long id, 
                          @Valid User user) {
    // Update logic
}
```

<!-- speaker_note:
3 minutes for this task
Emphasize @Valid annotation placement
Test with Postman/curl after adding
-->

<!-- end_slide -->

---

## Task 4: Test Basic Validation

### Test with invalid data:

```bash
# Missing username
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","age":25}'
```

<!-- pause -->

Expected: **400 Bad Request**

<!-- pause -->

```bash
# Invalid email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"invalid","age":25}'
```

<!-- pause -->

Expected: **400 Bad Request**

<!-- speaker_note:
Let them test and see default error responses
Point out that errors are not very user-friendly yet
This motivates the next task
-->

<!-- end_slide -->

---

## ⏱️ Checkpoint - 10 minutes

### You should have:
- ✅ Validation dependencies added
- ✅ User model with validation annotations
- ✅ @Valid in POST and PUT methods
- ✅ Basic validation working (400 responses)

<!-- pause -->

### Quick Test:
```bash
# Should fail - username too short
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"ab","email":"test@test.com","age":30}'
```

<!--
speaker_note: |
  MIDPOINT CHECK (2 minutes)
  
  • Status check:
    - "Show of hands - who has 400 errors?"
    - "Who's still getting 201?"
    - "Who's getting 500?"
  
  • Common problems:
    - 201: Forgot @Valid
    - 500: Missing EL dependency
    - 404: Wrong URL
  
  • Troubleshooting:
    - Check imports: javax not jakarta
    - Verify @Valid placement
    - Rebuild project
  
  • Time management:
    - Fast group: Start error handler
    - Slow group: Help individually
    - Consider pairing strugglers
  
  • Encouragement:
    - "Great progress!"
    - "Second half is easier"
    - "Error handling improves UX"
-->

<!-- end_slide -->

---

## Task 5: Custom Validation Error Handler

📚 **Docs**: `ExceptionMapper<ConstraintViolationException>`

💡 **Hint**: Create a provider to format errors nicely

<!-- pause -->

### ValidationExceptionMapper.java

```java
package com.dbh.training.rest.mappers;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.*;

@Provider
public class ValidationExceptionMapper 
    implements ExceptionMapper<ConstraintViolationException> {
    
    @Override
    public Response toResponse(ConstraintViolationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Validation Failed");
        
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        response.put("errors", errors);
        
        return Response.status(Response.Status.BAD_REQUEST)
                      .entity(response)
                      .build();
    }
}
```

<!-- speaker_note:
5 minutes for this task
Explain the ExceptionMapper interface
Show how it transforms validation errors
-->

<!-- end_slide -->

---

## Task 6: Register the Mapper

### JerseyConfig.java

```java
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Existing registrations...
        
        // Register validation mapper
        register(ValidationExceptionMapper.class);
    }
}
```

<!-- pause -->

### Test the improved errors:

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"ab","email":"invalid","age":200}'
```

<!-- pause -->

```json
{
  "status": 400,
  "error": "Validation Failed",
  "errors": [
    "username: Username must be between 3 and 50 characters",
    "email: Email must be valid",
    "age: Age cannot exceed 150"
  ]
}
```

<!-- speaker_note:
Show the difference in error responses
Much more user-friendly
Clients can parse and display specific errors
-->

<!-- end_slide -->

---

## Bonus Task 1: Query Parameter Validation

### Add validation to GET parameters:

```java
@GET
public Response getUsers(
    @QueryParam("page") 
    @DefaultValue("1") 
    @Min(value = 1, message = "Page must be >= 1") 
    Integer page,
    
    @QueryParam("size") 
    @DefaultValue("10") 
    @Min(value = 1, message = "Size must be >= 1")
    @Max(value = 100, message = "Size must be <= 100") 
    Integer size) {
    
    // Pagination logic
    return Response.ok(users).build();
}
```

<!-- pause -->

Test: `GET /api/users?page=0&size=200`

<!-- speaker_note:
For faster students
Shows parameter validation
Common for pagination
-->

<!-- end_slide -->

---

## Bonus Task 2: Custom Validator

### Create a username format validator:

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface ValidUsername {
    String message() default "Username can only contain letters, numbers, and underscores";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

<!-- pause -->

```java
public class UsernameValidator 
    implements ConstraintValidator<ValidUsername, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.matches("^[a-zA-Z0-9_]+$");
    }
}
```

<!-- speaker_note:
Advanced task for fast finishers
Shows custom validation creation
Useful for domain-specific rules
-->

<!-- end_slide -->

---

## ⏱️ Final Checkpoint - 20 minutes

### Core Tasks Completed:
- ✅ Bean Validation dependencies
- ✅ Model validation annotations  
- ✅ @Valid in endpoints
- ✅ Custom error responses
- ✅ All tests passing

<!-- pause -->

### Test Commands:
```bash
# Valid user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","email":"john@example.com","age":30}'

# Invalid user (multiple errors)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"a","email":"notanemail","age":-5}'
```

<!--
speaker_note: |
  FINAL CHECK (2 minutes)
  
  • Success criteria:
    - Valid user: 201 Created
    - Invalid user: 400 with details
    - Multiple errors shown
    - Clean error format
  
  • Quick poll:
    - "Who got it all working?"
    - "Who got stuck on error handler?"
    - "Anyone do bonus tasks?"
  
  • Common final issues:
    - Mapper not registered
    - Wrong Response.status()
    - JSON formatting problems
  
  • Wrap up:
    - "Excellent work!"
    - "You've added professional validation"
    - "This prevents bad data"
    - "Improves API reliability"
  
  • Transition:
    - "Questions before we move on?"
    - "Next: API Versioning"
    - "How to evolve without breaking"
-->

<!-- end_slide -->

---

## Common Issues & Solutions

### Validation Not Working?
- Check @Valid annotation is present
- Verify ValidationExceptionMapper is registered
- Ensure dependencies are correct versions

<!-- pause -->

### Getting 500 Instead of 400?
- Missing EL dependency (`javax.el`)
- Exception mapper not registered
- Check server logs for details

<!-- pause -->

### Custom Messages Not Showing?
- Check message attribute in annotations
- Verify mapper extracts `getMessage()`

<!-- speaker_note:
Address common problems
Have solutions ready
Keep troubleshooting quick
-->

<!-- end_slide -->

---

## What You Learned

### ✅ Implemented Bean Validation
- Added validation annotations
- Enabled automatic validation
- Created user-friendly errors

<!-- pause -->

### ✅ Best Practices
- Declarative validation
- Separation of concerns
- Clear error messages

<!-- pause -->

### ✅ Advanced Features
- Query parameter validation
- Custom validators
- Exception mapping

<!-- end_slide -->

---

## Excellent Work! 🎉

You've successfully added professional validation to your REST API!

### Your API now:
- ✅ Validates all input automatically
- ✅ Returns clear error messages
- ✅ Follows REST best practices
- ✅ Maintains data integrity

<!-- pause -->

### Next Up: API Versioning Strategies

How do we evolve our API without breaking clients?

<!-- speaker_note:
Congratulate the group
Quick recap of what they achieved
Preview next topic
Short break before versioning
-->

<!-- end_slide -->