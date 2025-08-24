---
title: Bean Validation with Jersey
author: Thomas Becker
theme:
  name: dark
---

# Bean Validation with Jersey

JSR-303/Bean Validation Integration

⏱️ **Duration**: 30 minutes → **20 min optimal**
⏱️ **Checkpoint**: End of Day 1 PM Session 1

---

## What is Bean Validation?

**Declarative validation using annotations**

- JSR-303/JSR-380 specification
- Declarative validation using annotations
- Automatic validation in REST endpoints
- Clear separation of validation logic

<!--
speaker_note: |
  INTRODUCTION (2 minutes)

  • Context setting:
    - "We've built CRUD operations"
    - "But accepting any data is dangerous"
    - "Need validation layer"

  • JSR-303 explanation:
    - Java Specification Request
    - Industry standard since 2009
    - JSR-380 is version 2.0
    - Hibernate Validator is implementation

  • Key benefit:
    - Declarative = describe what, not how
    - Annotations = clean, readable
    - Automatic = Jersey does the work
-->

<!-- pause -->

```java
public class User {
    @NotNull(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @Email(message = "Invalid email format")
    private String email;
}
```

<!-- end_slide -->

---

## Why Bean Validation?

### Without Validation

```java
@POST
public Response createUser(User user) {
    // Manual validation everywhere
    if (user.getUsername() == null) {
        return Response.status(400).build();
    }
    if (user.getUsername().length() < 3) {
        return Response.status(400).build();
    }
    // ... more checks
}
```

<!--
speaker_note: |
  MOTIVATION (3 minutes)

  • Problem with manual validation:
    - Repetitive boilerplate
    - Error prone
    - Mixed with business logic
    - Hard to maintain
    - No standard error format

  • Real-world impact:
    - "I've seen 200+ line validation methods"
    - "Copy-paste errors common"
    - "Inconsistent error messages"
-->

<!-- pause -->

### With Bean Validation

```java
@POST
public Response createUser(@Valid User user) {
    // Validation happens automatically!
    return Response.ok(user).build();
}
```

<!--
speaker_note: |
  • The magic of @Valid:
    - Single annotation
    - Framework handles everything
    - Consistent error format
    - Separation of concerns

  • Emphasize:
    - "Look how clean this is!"
    - "Business logic only"
    - "Validation is declarative"
-->

<!-- end_slide -->

---

## Common Validation Annotations

### Basic Constraints

| Annotation  | Purpose                             | Example                       |
| ----------- | ----------------------------------- | ----------------------------- |
| `@NotNull`  | Field cannot be null                | `@NotNull String name`        |
| `@NotEmpty` | Not null and not empty              | `@NotEmpty List<String> tags` |
| `@NotBlank` | Not null, not empty, not whitespace | `@NotBlank String username`   |

<!--
speaker_note: |
  BASIC ANNOTATIONS (3 minutes)

  • The big three confusion:
    - @NotNull: just null check
    - @NotEmpty: null + empty string/collection
    - @NotBlank: null + empty + whitespace

  • Rule of thumb:
    - Strings: use @NotBlank
    - Collections: use @NotEmpty
    - Other types: use @NotNull

  • Common mistake:
    - Using @NotNull for strings
    - Allows empty strings through!
-->

<!-- pause -->

### Size Constraints

| Annotation        | Purpose                | Example                |
| ----------------- | ---------------------- | ---------------------- |
| `@Size`           | String/Collection size | `@Size(min=3, max=50)` |
| `@Min/@Max`       | Numeric bounds         | `@Min(0) @Max(100)`    |
| `@DecimalMin/Max` | Decimal bounds         | `@DecimalMin("0.0")`   |

<!--
speaker_note: |
  • Size vs Min/Max:
    - @Size: strings, collections
    - @Min/@Max: numbers only
    - Don't mix them up!

  • Inclusive by default:
    - @Min(0) includes 0
    - @Max(100) includes 100
    - @DecimalMin has inclusive flag
-->

<!-- end_slide -->

---

## String and Pattern Validation

```java
public class User {
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
             message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @Email(message = "Please provide a valid email")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
             message = "Invalid phone number")
    private String phone;
}
```

<!-- pause -->

💡 **Tip**: Use `@Email` for email validation instead of complex regex patterns

<!-- end_slide -->

---

## Date and Time Validation

```java
public class Event {
    @NotNull
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @PastOrPresent(message = "Birth date cannot be in the future")
    private LocalDate birthDate;

    @FutureOrPresent
    private Instant startTime;
}
```

<!-- pause -->

Available annotations:

- `@Past` / `@PastOrPresent`
- `@Future` / `@FutureOrPresent`

<!-- end_slide -->

---

## Nested Validation

Validate nested objects with `@Valid`:

```java
public class Order {
    @NotNull
    private String orderId;

    @Valid  // Triggers validation of nested object
    @NotNull
    private Customer customer;

    @Valid  // Validates each item in the list
    @NotEmpty
    private List<OrderItem> items;
}
```

<!-- pause -->

```java
public class Customer {
    @NotBlank
    private String name;

    @Email
    private String email;
}
```

<!-- end_slide -->

---

## Custom Validation Messages

### Using message attribute

```java
@NotNull(message = "Username is required")
@Size(min = 3, max = 50,
      message = "Username must be between {min} and {max} characters")
private String username;

@Min(value = 18, message = "You must be at least {value} years old")
private Integer age;
```

<!-- pause -->

### Using ValidationMessages.properties

```properties
# src/main/resources/ValidationMessages.properties
user.username.required=Username is required
user.username.size=Username must be between {min} and {max} characters
user.age.minimum=You must be at least {value} years old
```

<!-- end_slide -->

---

## Jersey Integration

### 1. Add Dependencies (build.gradle)

```gradle
dependencies {
    implementation 'org.glassfish.jersey.ext:jersey-bean-validation:2.35'
    implementation 'org.hibernate.validator:hibernate-validator:6.2.5.Final'
    implementation 'org.glassfish:javax.el:3.0.0'
}
```

<!--
speaker_note: |
  SETUP (3 minutes)

  • Three dependencies explained:
    - jersey-bean-validation: Integration layer
    - hibernate-validator: Actual validation engine
    - javax.el: Expression Language for messages

  • Common issue:
    - Missing EL dependency
    - Causes ClassNotFoundException
    - Always need all three!

  • Version compatibility:
    - Jersey 2.35 for Java 8
    - Hibernate Validator 6.x (not 7.x)
    - 7.x needs Jakarta namespace
-->

<!-- pause -->

### 2. Register in JerseyConfig

```java
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(ValidationFeature.class);
        // Other registrations...
    }
}
```

<!--
speaker_note: |
  • Registration options:
    - ValidationFeature.class: Auto-discovery
    - Or explicit: register(new ValidationFeature())
    - Package scanning finds validators

  • That's it!
    - No complex configuration
    - Works out of the box
    - Convention over configuration
-->

<!-- end_slide -->

---

## Using @Valid in Resources

```java
@Path("/users")
public class UserResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid User user) {
        // If validation fails, Jersey returns 400 automatically
        userService.save(user);
        return Response.status(201).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id,
                              @Valid User user) {
        // Validation applied to request body
        return Response.ok(user).build();
    }
}
```

<!-- end_slide -->

---

## Validation Groups

Control which validations run when:

```java
public interface CreateGroup {}
public interface UpdateGroup {}

public class User {
    @Null(groups = CreateGroup.class)
    @NotNull(groups = UpdateGroup.class)
    private Long id;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String username;

    @NotBlank(groups = CreateGroup.class)
    private String password;  // Only required on create
}
```

<!-- pause -->

```java
@POST
public Response create(@Valid @ConvertGroup(to = CreateGroup.class) User user) {
    // Only CreateGroup validations run
}
```

<!-- end_slide -->

---

## Handling Validation Errors

### Default Behavior

Jersey returns 400 Bad Request with validation errors

<!-- pause -->

### Custom Exception Mapper

```java
@Provider
public class ValidationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        Map<String, List<String>> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            errors.computeIfAbsent(field, k -> new ArrayList<>())
                  .add(violation.getMessage());
        }

        return Response.status(400)
                      .entity(new ErrorResponse("Validation failed", errors))
                      .build();
    }
}
```

<!--
speaker_note: |
  ERROR HANDLING (4 minutes)

  • Default behavior problems:
    - Generic error format
    - Hard to parse
    - Not user friendly
    - Inconsistent structure

  • Exception mapper benefits:
    - Consistent format
    - Field-level errors
    - Multiple errors per field
    - Easy for clients to parse

  • Implementation details:
    - @Provider annotation crucial
    - Must implement ExceptionMapper
    - ConstraintViolationException type
    - Transform to your format

  • Best practices:
    - Group by field name
    - Include field path
    - Clear messages
    - Consistent status codes
-->

<!-- end_slide -->

---

## Method Parameter Validation

Validate individual parameters:

```java
@Path("/users")
public class UserResource {

    @GET
    public Response getUsers(
        @QueryParam("page") @Min(1) Integer page,
        @QueryParam("size") @Min(1) @Max(100) Integer size) {
        // Parameters are validated
        return Response.ok().build();
    }

    @GET
    @Path("/search")
    public Response search(
        @QueryParam("query")
        @NotBlank(message = "Search query cannot be empty")
        @Size(min = 3, message = "Search query must be at least 3 characters")
        String query) {
        // Search logic
        return Response.ok().build();
    }
}
```

<!-- end_slide -->

---

## Creating Custom Validators

### 1. Define the Annotation

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface ValidUsername {
    String message() default "Invalid username format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

<!-- pause -->

### 2. Implement the Validator

```java
public class UsernameValidator
    implements ConstraintValidator<ValidUsername, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // Let @NotNull handle this
        return value.matches("^[a-zA-Z0-9_]{3,20}$");
    }
}
```

<!-- end_slide -->

---

## Cross-Field Validation

Validate multiple fields together:

```java
@ValidPasswordConfirmation  // Custom class-level validation
public class RegistrationDto {
    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    // getters/setters
}
```

<!-- pause -->

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConfirmationValidator.class)
public @interface ValidPasswordConfirmation {
    String message() default "Passwords do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

<!-- end_slide -->

---

## Best Practices

### ✅ DO

- Use standard annotations when possible
- Provide clear error messages
- Validate at the boundary (REST layer)
- Use groups for different scenarios
- Test validation logic

<!--
speaker_note: |
  BEST PRACTICES - DO's (2 minutes)

  • Standard annotations:
    - Don't reinvent the wheel
    - @Email better than regex
    - Well-tested implementations

  • Clear messages:
    - User-facing text
    - Actionable feedback
    - Internationalization ready

  • Boundary validation:
    - REST layer = first line
    - Fail fast principle
    - Don't trust client
-->

<!-- pause -->

### ❌ DON'T

- Over-validate (be pragmatic)
- Duplicate validation logic
- Use validation for business rules
- Forget null checks with custom validators
- Mix validation with business logic

<!--
speaker_note: |
  BEST PRACTICES - DON'Ts (2 minutes)

  • Over-validation:
    - Email regex too strict
    - Phone numbers vary globally
    - Be liberal in what you accept

  • Business rules:
    - "User must be premium" = business
    - "Age must be number" = validation
    - Keep them separate

  • Common mistakes:
    - Validating generated fields
    - Too restrictive patterns
    - Not handling null in validators
-->

<!-- end_slide -->

---

## Common Validation Patterns

### Email with specific domain

```java
@Pattern(regexp = "^[A-Za-z0-9+_.-]+@dbh\\.com$",
         message = "Email must be a DBH company email")
private String corporateEmail;
```

<!-- pause -->

### Phone number

```java
@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
         message = "Please provide a valid E.164 phone number")
private String phoneNumber;
```

<!-- pause -->

### Strong password

```java
@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
         message = "Password must be at least 8 characters with uppercase, lowercase, digit, and special character")
private String password;
```

<!-- end_slide -->

---

## Testing Validation

```java
@Test
public void testUserValidation() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    User user = new User();
    user.setUsername("ab");  // Too short
    user.setEmail("invalid");  // Invalid email

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertEquals(2, violations.size());

    // Check specific violations
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
}
```

<!-- end_slide -->

---

## Integration Testing

```java
@Test
public void testCreateUserWithInvalidData() {
    User invalidUser = new User();
    // Missing required fields

    Response response = target("/users")
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.json(invalidUser));

    assertEquals(400, response.getStatus());

    ErrorResponse error = response.readEntity(ErrorResponse.class);
    assertNotNull(error.getErrors());
    assertTrue(error.getErrors().containsKey("username"));
}
```

<!-- end_slide -->

---

## Performance Considerations

### Validation is Fast

- Annotations are processed once at startup
- Validation logic is compiled
- Minimal overhead per request

<!-- pause -->

### Tips for Performance

- Avoid complex regex patterns
- Cache validator instances
- Use fail-fast validation
- Consider async validation for heavy checks

<!-- pause -->

```java
// Fail fast - stops at first error
@Valid(failFast = true)
```

<!-- end_slide -->

---

## Common Pitfalls

### 1. Forgetting @Valid

```java
// ❌ Validation won't run!
public Response create(User user) { }

// ✅ Correct
public Response create(@Valid User user) { }
```

<!-- pause -->

### 2. Null handling in custom validators

```java
// Always handle null in custom validators
public boolean isValid(String value, ConstraintValidatorContext ctx) {
    if (value == null) return true;  // Let @NotNull handle nulls
    // Your validation logic
}
```

<!-- end_slide -->

---

## Summary

### What We Covered

- JSR-303/Bean Validation basics
- Common validation annotations
- Jersey integration with @Valid
- Custom validators
- Error handling
- Best practices

<!-- pause -->

### Key Benefits

- ✅ Declarative validation
- ✅ Automatic error responses
- ✅ Reusable validation logic
- ✅ Clear separation of concerns

<!--
speaker_note: |
  SUMMARY AND TRANSITION (1 minute)

  • Quick recap:
    - Validation is declarative
    - Jersey integration simple
    - Error handling important
    - Best practices matter

  • Key takeaway:
    - "Validation prevents bugs"
    - "First line of defense"
    - "Professional API quality"

  • Exercise preview:
    - "20 minutes hands-on"
    - "Apply what we learned"
    - "I'll help if stuck"

  • Energy check:
    - "Ready to code?"
    - "Questions before we start?"
    - "Let's do this!"
-->

<!-- pause -->

### Next: Hands-on Exercise

Let's add validation to our User REST API!

<!-- end_slide -->

---

## Questions?

### Common Questions

**Q: Can I validate path parameters?**
A: Yes! Use annotations directly on `@PathParam`

<!-- pause -->

**Q: How do I validate collections?**
A: Use `@Valid` with `@NotEmpty` for the collection

<!-- pause -->

**Q: Can I disable validation for specific endpoints?**
A: Simply don't use `@Valid` on that method

<!-- pause -->

**Q: What about database constraints?**
A: Bean Validation complements but doesn't replace DB constraints

<!--
speaker_note: |
  Q&A AND WRAP UP (2 minutes)

  • Common questions to expect:
    - Path parameters: Yes, works same way
    - Collections: @Valid on List + @NotEmpty
    - Disable per endpoint: Just omit @Valid
    - DB constraints: Still needed as backup

  • Key takeaways:
    - Validation at REST layer = fail fast
    - DB constraints = data integrity
    - Both are important
    - Defense in depth principle

  • Transition to exercise:
    - "Now let's implement this"
    - "20 minute hands-on"
    - "I'll help if you get stuck"
-->

<!-- end_slide -->
