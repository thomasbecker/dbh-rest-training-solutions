# Instructor Notes - Exercise 04: Bean Validation

## Overview
This exercise adds Bean Validation to the REST API created in Exercise 03. Students will learn to use standard validation annotations, create custom error handlers, and test validation rules.

## Time Management
- **Total Time**: 20 minutes
- **Checkpoint at 10 minutes**: Basic validation working
- **Buffer**: 5 minutes for troubleshooting

## Pre-Exercise Checklist
- [ ] Ensure all students completed Exercise 03
- [ ] Verify Java 8 compatibility of validation libraries
- [ ] Have dependency versions ready to share
- [ ] Test validation examples beforehand

## Common Issues and Solutions

### Issue 1: Validation Not Triggering
**Problem**: Students add annotations but validation doesn't run
**Solution**: 
- Check @Valid is on the parameter, not the method
- Verify dependencies are added and project rebuilt
- Check imports (javax.validation, not jakarta)

### Issue 2: ClassNotFoundException for EL
**Problem**: Runtime error about Expression Language
**Solution**:
```gradle
implementation 'org.glassfish:javax.el:3.0.0'
```
This dependency is often missed.

### Issue 3: Generic 500 Error Instead of 400
**Problem**: Validation fails but returns 500 instead of 400
**Solution**:
- ValidationExceptionMapper not registered
- Check @Provider annotation is present
- Verify mapper is registered in JerseyConfig

### Issue 4: Hibernate Validator Version Conflicts
**Problem**: NoSuchMethodError or version conflicts
**Solution**:
Use compatible versions:
```gradle
implementation 'org.hibernate.validator:hibernate-validator:6.2.5.Final'
```
Not version 7.x which requires Jakarta namespace.

## Teaching Points

### When Introducing Validation
- Emphasize declarative vs imperative validation
- Show the mess of manual validation code first
- Demonstrate how annotations clean this up

### Key Concepts to Reinforce
1. **Separation of Concerns**: Validation logic separate from business logic
2. **Fail Fast**: Validate at the boundary (REST layer)
3. **Clear Messages**: Always provide helpful error messages
4. **Standard Annotations**: Use built-in validators when possible

### Live Coding Demonstration
If doing live coding:
1. Start with broken validation (no @Valid)
2. Show the error when @Valid is added
3. Demonstrate custom error mapper improvement
4. Test with curl showing before/after error messages

## Testing Commands for Demonstration

### Valid User Creation
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","email":"john@example.com","age":30}'
```

### Multiple Validation Errors
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"a","email":"notanemail","age":999}'
```

### Edge Cases
```bash
# Empty strings (different from null)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"","email":"","age":25}'

# Whitespace only
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"   ","email":"test@test.com","age":25}'
```

## Discussion Topics

### After Basic Implementation
- Why use @NotBlank vs @NotNull vs @NotEmpty?
- When would you create custom validators?
- How does this compare to database constraints?

### Advanced Topics (If Time Permits)
- Validation groups for different scenarios
- Cross-field validation
- Async validation for expensive checks
- i18n for error messages

## Troubleshooting Guide

### Student Stuck at Dependency Stage
Provide exact gradle snippet:
```gradle
implementation 'org.glassfish.jersey.ext:jersey-bean-validation:2.35'
implementation 'org.hibernate.validator:hibernate-validator:6.2.5.Final'
implementation 'org.glassfish:javax.el:3.0.0'
```

### Validation Working but Ugly Errors
Guide them through exception mapper:
1. Create new package `mappers` if needed
2. Implement ExceptionMapper interface
3. Don't forget @Provider annotation
4. Register in JerseyConfig

### Tests Failing After Adding Validation
Remind them to update test data:
```java
// Before
User user = new User();
user.setUsername("a"); // Too short!

// After
User user = new User();
user.setUsername("valid_user");
user.setEmail("test@example.com");
user.setAge(25);
```

## Success Criteria
Students should be able to:
- [ ] Add validation annotations to model
- [ ] Enable validation with @Valid
- [ ] Create custom error responses
- [ ] Test validation with curl
- [ ] Understand when to use which annotation

## Fast Finishers
Direct them to bonus tasks:
1. Query parameter validation
2. Custom validators
3. Cross-field validation

## Slow Students
Minimum viable outcome:
- At least username validation working
- @Valid on POST method
- One successful validation test

## Post-Exercise Discussion
- Ask who got custom validators working
- Discuss real-world validation scenarios
- Preview how validation integrates with API versioning

## Notes for Next Exercise
The validation added here will be important for API versioning exercise, where v2 might have different validation rules than v1.

## Sample Solution Structure
```
src/main/java/com/dbh/training/rest/
├── models/
│   └── User.java (with validation annotations)
├── resources/
│   └── UserResource.java (with @Valid)
├── mappers/
│   └── ValidationExceptionMapper.java (new)
└── config/
    └── JerseyConfig.java (register mapper)
```

## Validation Annotation Quick Reference
For student questions:
- `@NotNull`: Field cannot be null
- `@NotEmpty`: Not null and not empty (String, Collection)
- `@NotBlank`: Not null, not empty, not whitespace (String only)
- `@Size(min=, max=)`: Length constraints
- `@Min/@Max`: Numeric bounds
- `@Email`: Email format validation
- `@Pattern`: Regex validation

## Time Check Reminders
- **5 minutes**: Dependencies should be added
- **10 minutes**: Basic validation working
- **15 minutes**: Custom error handler complete
- **20 minutes**: Wrap up, preview next topic