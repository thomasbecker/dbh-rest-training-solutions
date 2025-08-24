# DBH REST API Training - Solutions

This repository contains the complete solution code for all exercises in the REST API training.

## ⚠️ Important Note

This repository becomes available progressively during the training:
- **Day 1 End**: Exercises 1-5 solutions
- **Day 2 Morning**: Exercises 6-7 solutions  
- **Day 2 End**: All remaining solutions

Please try to complete exercises on your own before referring to these solutions!

## Repository Structure

### Branches

Each exercise solution is available on a separate branch:

- `main` - Base solution with exercises 1-3 complete
- `solution/04-bean-validation` - Bean Validation implementation
- `solution/05-api-versioning` - API Versioning with V1/V2 resources
- `solution/06-jackson-basics` - Jackson basic annotations
- `solution/07-jackson-advanced` - Advanced Jackson features
- `solution/08-security-implementation` - JWT authentication
- `solution/09-comprehensive-todo` - Complete Todo API
- `solution/showcase-openapi` - OpenAPI/Swagger integration

## Viewing Solutions

To see a specific exercise solution:

```bash
# Clone the repository
git clone https://github.com/thomasbecker/dbh-rest-training-solutions.git
cd dbh-rest-training-solutions

# List all solution branches
git branch -a

# Checkout a specific solution
git checkout solution/04-bean-validation

# To see changes for that exercise
git diff main..solution/04-bean-validation
```

## Running the Solutions

Each branch contains a fully working solution:

```bash
# Build the project
./gradlew build

# Run tests to verify
./gradlew test

# Start the server
./gradlew run
```

## Understanding the Solutions

### Progressive Enhancement

Each branch builds upon the previous:

1. **main**: Basic CRUD operations
2. **04-bean-validation**: Adds validation annotations
3. **05-api-versioning**: Implements versioning strategies
4. **06-jackson-basics**: JSON customization
5. **07-jackson-advanced**: Complex serialization
6. **08-security**: JWT authentication
7. **09-todo**: Complete application

### Key Implementation Patterns

#### Resource Structure
```java
@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResourceV1 {
    // Implementation
}
```

#### Validation
```java
public class User {
    @NotNull(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @Email(message = "Invalid email format")
    private String email;
}
```

#### Jackson Customization
```java
@JsonProperty("user_name")
private String username;

@JsonIgnore
private String password;

@JsonFormat(pattern = "yyyy-MM-dd")
private LocalDate birthDate;
```

#### JWT Security
```java
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    // JWT validation logic
}
```

## Common Patterns Used

### 1. In-Memory Storage
For simplicity, all solutions use in-memory storage:

```java
private static final Map<Long, User> users = new ConcurrentHashMap<>();
private static final AtomicLong idGenerator = new AtomicLong();
```

### 2. Error Handling
Consistent error responses:

```java
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    // Unified error handling
}
```

### 3. Test-Driven Development
All features have corresponding tests:

```java
@Test
public void testCreateUser() {
    given()
        .contentType(ContentType.JSON)
        .body(newUser)
    .when()
        .post("/users")
    .then()
        .statusCode(201)
        .body("username", equalTo("john"));
}
```

## API Endpoints

### User Management (V1 & V2)
- `GET /api/v1/users` - List all users
- `GET /api/v1/users/{id}` - Get specific user
- `POST /api/v1/users` - Create user
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user

### Todo Management
- `GET /api/todos` - List todos (with filtering)
- `GET /api/todos/{id}` - Get specific todo
- `POST /api/todos` - Create todo
- `PUT /api/todos/{id}` - Update todo
- `PATCH /api/todos/{id}` - Partial update
- `DELETE /api/todos/{id}` - Delete todo

### Authentication
- `POST /api/auth/login` - Get JWT token
- `POST /api/auth/refresh` - Refresh token

## Technology Decisions

### Why Jersey over Spring Boot?
- Lighter footprint
- Explicit configuration
- Better understanding of JAX-RS
- Faster startup times

### Why JWT over Sessions?
- Stateless authentication
- Scalable architecture
- Mobile-friendly
- Microservices-ready

### Why In-Memory Storage?
- Focus on REST concepts
- No database setup required
- Easy to reset state
- Perfect for training

## Troubleshooting

### Common Issues

1. **Port already in use**
```bash
# Find process using port 8080
lsof -i :8080
# Kill the process
kill -9 <PID>
```

2. **Java version mismatch**
```bash
# Ensure Java 8
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
java -version
```

3. **Tests failing**
```bash
# Clean and rebuild
./gradlew clean build
```

## Best Practices Demonstrated

1. **RESTful Design**: Proper HTTP methods and status codes
2. **Validation**: Input validation at API boundaries
3. **Error Handling**: Consistent error responses
4. **Security**: JWT implementation with proper validation
5. **Testing**: Comprehensive integration tests
6. **Documentation**: Clear code structure and comments

## Additional Resources

- [Jersey Documentation](https://eclipse-ee4j.github.io/jersey/)
- [Jackson Documentation](https://github.com/FasterXML/jackson)
- [JWT Introduction](https://jwt.io/introduction)
- [REST Best Practices](https://restfulapi.net/)

## License

