# Exercise 04: Bean Validation with Jersey

**Duration**: 20 minutes  
**Difficulty**: Intermediate  
**Objectives**:
- Add Bean Validation to your REST API
- Implement validation annotations on the User model
- Create custom error responses for validation failures
- Test validation with various invalid inputs

## Prerequisites

✅ Completed Exercise 03 (Jersey CRUD Operations)  
✅ Working User REST API with all CRUD operations  
✅ Gradle build system configured

## Background

Input validation is crucial for any REST API. Without proper validation, your API
could accept invalid data leading to data corruption, security issues, or
application crashes. Bean Validation (JSR-303/JSR-380) provides a standardized
way to validate data using annotations.

Jersey integrates seamlessly with Bean Validation, automatically validating
request bodies when you add the `@Valid` annotation to your resource methods.

## Your Tasks

### Task 1: Add Bean Validation Dependencies (3 minutes)

Add the required dependencies to your `build.gradle`:

```gradle
dependencies {
    // Your existing dependencies...
    
    // Add these for Bean Validation:
    implementation 'org.glassfish.jersey.ext:jersey-bean-validation:2.35'
    implementation 'org.hibernate.validator:hibernate-validator:6.2.5.Final'
    implementation 'org.glassfish:javax.el:3.0.0'
}
```

After adding, run:
```bash
./gradlew build
```

💡 **Tip**: We need all three dependencies:
- `jersey-bean-validation`: Jersey's validation integration
- `hibernate-validator`: The validation implementation
- `javax.el`: Expression Language for validation messages

### Task 2: Add Validation Annotations to User Model (5 minutes)

Update your `User.java` model with validation annotations:

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
    
    // Keep your existing constructors, getters, and setters
}
```

**Validation Rules**:
- Username: Required, 3-50 characters
- Email: Required, valid email format
- Age: Required, between 0 and 150

### Task 3: Enable Validation in REST Endpoints (3 minutes)

Add the `@Valid` annotation to your POST and PUT methods in `UserResource.java`:

```java
import javax.validation.Valid;

@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response createUser(@Valid User user) {
    // Your existing create logic
}

@PUT
@Path("/{id}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response updateUser(@PathParam("id") Long id, @Valid User user) {
    // Your existing update logic
}
```

💡 **Note**: The `@Valid` annotation tells Jersey to validate the request body
before calling your method.

### Task 4: Test Basic Validation (2 minutes)

Test your validation with invalid data:

```bash
# Test 1: Missing username (should return 400)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","age":25}'

# Test 2: Invalid email (should return 400)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"not-an-email","age":25}'

# Test 3: Username too short (should return 400)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"ab","email":"test@example.com","age":25}'

# Test 4: Age out of range (should return 400)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"test@example.com","age":200}'
```

All tests should return `400 Bad Request`, but the error messages might not be
very user-friendly yet.

### Task 5: Create Custom Validation Error Handler (5 minutes)

Create a new class `ValidationExceptionMapper.java` in the `mappers` package:

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
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Validation Failed");
        
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.add(field + ": " + message);
        }
        response.put("errors", errors);
        
        return Response.status(Response.Status.BAD_REQUEST)
                      .entity(response)
                      .build();
    }
}
```

### Task 6: Register the Exception Mapper (2 minutes)

Register your ValidationExceptionMapper in `JerseyConfig.java`:

```java
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Your existing registrations...
        
        // Add this line:
        register(ValidationExceptionMapper.class);
    }
}
```

Now test again with invalid data to see the improved error messages:

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"a","email":"invalid","age":-5}'
```

Expected response:
```json
{
  "status": 400,
  "error": "Validation Failed",
  "errors": [
    "username: Username must be between 3 and 50 characters",
    "email: Email must be valid",
    "age: Age cannot be negative"
  ]
}
```

## Running the Tests

Your existing tests might fail after adding validation. Update them to provide
valid data:

```bash
./gradlew test
```

Example of a valid user for testing:
```java
User validUser = new User();
validUser.setUsername("john_doe");
validUser.setEmail("john@example.com");
validUser.setAge(30);
```

## Expected Test Output

After implementing validation, you should see:
- POST with invalid data returns 400
- POST with valid data returns 201
- PUT with invalid data returns 400
- PUT with valid data returns 200
- Error responses include field-specific messages

## Hints

### Data Structure Tips
- Use `@NotBlank` for strings that shouldn't be empty or whitespace
- Use `@NotNull` for required fields that aren't strings
- Use `@Size` for string length validation
- Use `@Min/@Max` for numeric ranges
- Use `@Email` for email validation

### Common Patterns
- Always provide custom messages in annotations
- Handle null values appropriately in custom validators
- Use groups for different validation scenarios (create vs update)

## Bonus Tasks (If Time Permits)

### Bonus 1: Query Parameter Validation (5 minutes)

Add validation to your GET method for pagination:

```java
@GET
public Response getUsers(
    @QueryParam("page") 
    @DefaultValue("1") 
    @Min(value = 1, message = "Page must be at least 1") 
    Integer page,
    
    @QueryParam("size") 
    @DefaultValue("10") 
    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100") 
    Integer size) {
    
    // Implementation
}
```

### Bonus 2: Custom Username Validator (10 minutes)

Create a custom annotation to validate username format:

1. Create the annotation:
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

2. Create the validator:
```java
public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // Let @NotNull handle this
        return value.matches("^[a-zA-Z0-9_]+$");
    }
}
```

3. Use it in your User model:
```java
@ValidUsername
@NotBlank(message = "Username is required")
@Size(min = 3, max = 50)
private String username;
```

### Bonus 3: Cross-Field Validation (10 minutes)

If you add a `passwordConfirm` field, validate that it matches `password`:

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {
    String message() default "Passwords do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

## Helpful Resources

- [Jersey Bean Validation Documentation](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/bean-validation.html)
- [Bean Validation Specification](https://beanvalidation.org/2.0/spec/)
- [Hibernate Validator Documentation](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/)
- [Common Validation Annotations Reference](https://docs.oracle.com/javaee/7/api/javax/validation/constraints/package-summary.html)

## Common Mistakes to Avoid

1. ❌ **Forgetting @Valid**: Without `@Valid`, validation won't run
2. ❌ **Wrong null handling**: Custom validators should handle null gracefully
3. ❌ **Missing EL dependency**: Causes runtime errors
4. ❌ **Not registering the exception mapper**: Results in generic error messages
5. ❌ **Validating IDs on creation**: IDs should be null when creating new entities
6. ❌ **Over-validation**: Don't validate business rules, only data integrity
7. ❌ **Unclear messages**: Always provide helpful error messages

## Solution Checkpoint

After completing this exercise, you should have:

- [ ] Bean Validation dependencies in `build.gradle`
- [ ] Validation annotations on all User fields
- [ ] `@Valid` annotation on POST and PUT methods
- [ ] Custom ValidationExceptionMapper created
- [ ] Exception mapper registered in JerseyConfig
- [ ] All validation tests passing
- [ ] Clear error messages for validation failures

## Need Help?

If you're stuck:

1. Check that all three dependencies are added correctly
2. Verify @Valid is on the method parameter, not the method itself
3. Check server logs for detailed error messages
4. Ensure ValidationExceptionMapper is annotated with @Provider
5. Make sure JerseyConfig registers the ValidationExceptionMapper
6. Ask the instructor for help - validation setup can be tricky!

## What's Next?

In the next exercise, we'll learn about API versioning strategies and how to
evolve your API without breaking existing clients.