# Exercise 03: Bean Validation

## Time Allocation: 15-20 minutes

## Objective
Add Bean Validation to your REST API to ensure data integrity and provide
meaningful error messages.

## Prerequisites
- Completed Exercise 02 (Jersey CRUD)
- Working User model and UserResource

## Tasks

### Task 1: Add Validation Annotations (5 minutes)
Update the User model with Bean Validation annotations:

```java
// In User.java
import javax.validation.constraints.*;

public class User {
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    // ... rest of the class
}
```

### Task 2: Enable Validation in Resources (5 minutes)
Add `@Valid` annotation to UserResource methods:

```java
@POST
public Response createUser(@Valid User user) {
    // Your existing implementation
}

@PUT
@Path("/{id}")
public Response updateUser(@PathParam("id") Long id, @Valid User user) {
    // Your existing implementation
}
```

### Task 3: Test Validation (5 minutes)
Test that invalid data returns 400 Bad Request:

```bash
# Test missing username
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'

# Test invalid email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"not-an-email"}'

# Test short username
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"jo","email":"test@example.com"}'
```

### Task 4: Create Custom Error Response (5 minutes)
Create a ValidationExceptionMapper to return structured error messages:

```java
@Provider
public class ValidationExceptionMapper 
    implements ExceptionMapper<ConstraintViolationException> {
    
    @Override
    public Response toResponse(ConstraintViolationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Validation Failed");
        
        List<String> errors = e.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + 
                             ": " + violation.getMessage())
            .collect(Collectors.toList());
        
        response.put("errors", errors);
        
        return Response.status(400)
            .entity(response)
            .build();
    }
}
```

## Validation Rules Summary
- **username**: Required, 3-20 characters
- **email**: Required, valid email format
- **firstName**: Required
- **lastName**: Required

## Expected Response for Validation Errors
```json
{
    "status": 400,
    "error": "Validation Failed",
    "errors": [
        "username: Username must be 3-20 characters",
        "email: Email must be valid"
    ]
}
```

## Bonus Tasks (For Fast Learners)

### Bonus 1: Custom Validation Annotation (10 minutes)
Create a custom `@UniqueUsername` annotation:

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
    String message() default "Username already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class UniqueUsernameValidator 
    implements ConstraintValidator<UniqueUsername, String> {
    // Implement validation logic
}
```

### Bonus 2: Validation Groups (10 minutes)
Implement different validation groups for Create vs Update:

```java
public interface CreateValidation {}
public interface UpdateValidation {}

// In User model
@Null(groups = CreateValidation.class)
@NotNull(groups = UpdateValidation.class)
private Long id;

// In UserResource
@POST
public Response createUser(
    @Validated(CreateValidation.class) User user) {
    // ...
}
```

### Bonus 3: Password Validation (5 minutes)
Add a password field with complex validation:

```java
@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
         message = "Password must be 8+ chars with uppercase, lowercase, digit, and special char")
private String password;
```

### Bonus 4: Cross-Field Validation (10 minutes)
Create a class-level constraint for related fields:

```java
@PasswordConfirmation
public class UserRegistration {
    private String password;
    private String confirmPassword;
    // Validate that both passwords match
}
```

## Common Issues and Solutions

### Issue: Validation not triggered
**Solution**: Ensure you have:
1. `jersey-bean-validation` dependency in build.gradle
2. `@Valid` annotation on method parameters
3. ValidationConfig registered in your application

### Issue: Generic 500 error instead of 400
**Solution**: Register the ValidationExceptionMapper as a Provider

### Issue: No error details in response
**Solution**: Create custom ExceptionMapper for ValidationException

## Testing Tips
1. Use curl or Postman to test various invalid scenarios
2. Check that valid data still works correctly
3. Verify error messages are helpful and specific
4. Test boundary values (min/max lengths)

## Success Criteria
✅ Invalid data returns 400 Bad Request  
✅ Error messages clearly indicate what's wrong  
✅ Valid data continues to work as before  
✅ All validation rules are enforced  

## Next Steps
After completing this exercise:
- Consider how validation affects API usability
- Think about validation performance impact
- Explore more complex validation scenarios
- Move on to Exercise 04: API Versioning