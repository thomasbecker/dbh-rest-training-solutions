# Exercise 08: Security Implementation

**Duration:** 60 minutes  
**Difficulty:** Advanced  
**Objectives:**
- Implement JWT-based authentication
- Add role-based authorization
- Secure REST endpoints
- Apply security best practices

## Prerequisites

- Completed Exercise 07 (Jackson Advanced)
- Understanding of authentication vs authorization
- Basic knowledge of JWT tokens
- Familiarity with HTTP headers

## Background

Security is crucial for REST APIs. In this exercise, you'll implement a 
production-grade security layer using:

- **JWT (JSON Web Tokens)** for stateless authentication
- **BCrypt** for secure password hashing
- **Role-Based Access Control (RBAC)** for authorization
- **Security filters** for request interception
- **Security headers** for defense in depth

## Your Tasks

### Task 1: Add Security Dependencies (10 min)

Add the following dependencies to your `build.gradle`:

```gradle
dependencies {
    // JWT support
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    
    // Password hashing
    implementation 'org.mindrot:jbcrypt:0.4'
    
    // Security annotations
    implementation 'javax.annotation:javax.annotation-api:1.3.2'
}
```

**💡 Tip:** JJWT 0.11.5 is the latest version compatible with Java 8.

### Task 2: Enhance User Model (10 min)

Update the `User` model to support authentication:

1. Add password hash field (never store plain text!)
2. Add roles collection
3. Implement password checking and setting methods

```java
public class User {
    // Existing fields...
    
    @JsonIgnore
    private String passwordHash;
    
    private Set<String> roles = new HashSet<>();
    
    public boolean checkPassword(String password) {
        return BCrypt.checkpw(password, passwordHash);
    }
    
    public void setPassword(String password) {
        this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    // Getters and setters...
}
```

### Task 3: Create JWT Service (15 min)

Implement `JwtService` to handle token operations:

```java
@Singleton
public class JwtService {
    private static final String SECRET = System.getenv("JWT_SECRET");
    private static final long EXPIRATION_TIME = 3600000; // 1 hour
    
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("roles", user.getRoles())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
    }
    
    public Claims validateToken(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody();
    }
}
```

**💡 Tip:** Use environment variables for secrets. Never commit them!

### Task 4: Implement Authentication Filter (15 min)

Create `AuthenticationFilter` to validate JWT tokens:

```java
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
    @Inject
    private JwtService jwtService;
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                Claims claims = jwtService.validateToken(token);
                String username = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);
                
                SecurityContext securityContext = new UserSecurityContext(
                    username, roles, requestContext.getSecurityContext());
                    
                requestContext.setSecurityContext(securityContext);
            } catch (Exception e) {
                // Invalid token - request continues without auth
            }
        }
    }
}
```

### Task 5: Create Security Context (10 min)

Implement custom `SecurityContext`:

```java
public class UserSecurityContext implements SecurityContext {
    private final String username;
    private final Set<String> roles;
    private final SecurityContext originalContext;
    
    public UserSecurityContext(String username, List<String> roles, 
                               SecurityContext originalContext) {
        this.username = username;
        this.roles = new HashSet<>(roles);
        this.originalContext = originalContext;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return () -> username;
    }
    
    @Override
    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }
    
    @Override
    public boolean isSecure() {
        return originalContext.isSecure();
    }
    
    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
```

### Task 6: Create Login Endpoint (10 min)

Implement authentication resource:

```java
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    @Inject
    private UserService userService;
    
    @Inject
    private JwtService jwtService;
    
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());
        
        if (user != null && user.checkPassword(request.getPassword())) {
            String token = jwtService.generateToken(user);
            
            return Response.ok(new TokenResponse(token))
                          .build();
        }
        
        return Response.status(Status.UNAUTHORIZED)
                      .entity(new ErrorResponse("Invalid credentials"))
                      .build();
    }
}
```

### Task 7: Secure Endpoints with Roles

Update `UserResource` with role-based security:

```java
@Path("/users")
public class UserResource {
    
    @GET
    @RolesAllowed({"USER", "ADMIN"})
    public Response getUsers() {
        // Any authenticated user can access
    }
    
    @GET
    @Path("/admin")
    @RolesAllowed("ADMIN")
    public Response getAdminData() {
        // Only ADMIN role can access
    }
    
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteUser(@PathParam("id") Long id) {
        // Only ADMIN can delete
    }
}
```

Don't forget to register `RolesAllowedDynamicFeature` in your Jersey config!

## Running the Tests

```bash
# Run security tests
./gradlew test --tests "*SecurityTest"

# Test manually
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Use the token
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <your-token>"
```

## Expected Test Output

```
SecurityTest > testLoginSuccess PASSED
SecurityTest > testLoginFailure PASSED
SecurityTest > testTokenValidation PASSED
SecurityTest > testRoleBasedAccess PASSED
SecurityTest > testUnauthorizedAccess PASSED
SecurityTest > testTokenExpiration PASSED
```

## Hints

### JWT Structure
- Header: Algorithm and token type
- Payload: Claims (user data)
- Signature: Verification

### Security Headers
Add these in a `ResponseFilter`:
```java
response.getHeaders().add("X-Content-Type-Options", "nosniff");
response.getHeaders().add("X-Frame-Options", "DENY");
response.getHeaders().add("Strict-Transport-Security", "max-age=31536000");
```

### Token Storage
- Client should store in memory or secure storage
- Never in localStorage (XSS vulnerable)
- Consider httpOnly cookies for web apps

## Bonus Tasks

### 1. Refresh Tokens (15 min)
Implement long-lived refresh tokens:
- Generate refresh token on login
- Create `/auth/refresh` endpoint
- Validate and issue new access token

### 2. Rate Limiting (10 min)
Prevent brute force attacks:
- Track login attempts per IP
- Block after 5 failed attempts
- Reset after 15 minutes

### 3. Token Revocation (10 min)
Implement token blacklist:
- Store revoked tokens in cache
- Check blacklist in filter
- Clear expired tokens periodically

### 4. Audit Logging (10 min)
Log security events:
- Successful/failed logins
- Token generation
- Authorization failures
- Include timestamp, user, IP

## Helpful Resources

- [OWASP REST Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/REST_Security_Cheat_Sheet.html)
- [JWT.io - JWT Debugger](https://jwt.io/)
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [BCrypt Calculator](https://bcrypt-generator.com/)
- [OWASP Top 10 API Security](https://owasp.org/www-project-api-security/)

## Common Mistakes to Avoid

1. ❌ **Storing passwords in plain text** - Always use BCrypt or similar
2. ❌ **Hardcoding secrets** - Use environment variables
3. ❌ **No token expiration** - Tokens should expire
4. ❌ **Logging sensitive data** - Never log passwords or tokens
5. ❌ **Weak secrets** - Use strong, random secrets (min 256 bits)
6. ❌ **Missing HTTPS** - Always use HTTPS in production
7. ❌ **Client-side authorization** - Never trust the client
8. ❌ **Exposing stack traces** - Hide internal errors from users

## Solution Checkpoint

By the end of this exercise, you should have:

- [ ] JWT token generation and validation
- [ ] Password hashing with BCrypt
- [ ] Authentication filter intercepting requests
- [ ] Role-based authorization with @RolesAllowed
- [ ] Login endpoint returning JWT token
- [ ] Secured endpoints requiring authentication
- [ ] Security headers in responses
- [ ] All security tests passing

## Need Help?

1. Check JWT token structure at jwt.io
2. Verify BCrypt hashes are being generated
3. Check filter is registered in Jersey config
4. Ensure RolesAllowedDynamicFeature is registered
5. Use debugger to trace authentication flow
6. Ask instructor about specific security patterns