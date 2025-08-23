# Instructor Notes - Exercise 08: Security Implementation

## Overview

This exercise covers critical security concepts for REST APIs. Students 
implement JWT authentication and role-based authorization, essential for 
production applications.

## Learning Objectives

1. Understand stateless authentication with JWT
2. Implement secure password storage
3. Apply role-based access control
4. Use security filters in Jersey
5. Follow security best practices

## Time Management

- **Total:** 60 minutes
- Setup & Dependencies: 10 min
- JWT Implementation: 25 min
- Authorization: 15 min
- Testing: 10 min

## Common Challenges

### 1. JWT Secret Management
**Problem:** Students hardcode secrets
**Solution:** Emphasize environment variables early
```bash
export JWT_SECRET="your-256-bit-secret-here"
```

### 2. Filter Registration
**Problem:** Filter not executing
**Solution:** Check two things:
```java
// 1. @Provider annotation on filter
@Provider
@Priority(Priorities.AUTHENTICATION)

// 2. Register in JerseyConfig
register(AuthenticationFilter.class);
register(RolesAllowedDynamicFeature.class);
```

### 3. Token Validation Errors
**Problem:** "JWT signature does not match"
**Solution:** Ensure same secret for signing and validation

### 4. Role Checking Not Working
**Problem:** @RolesAllowed ignored
**Solution:** Must register RolesAllowedDynamicFeature

## Setup Instructions

### Pre-Exercise Setup
```bash
# Set environment variable
export JWT_SECRET="training-secret-key-minimum-256-bits"

# Verify Java 8
java -version

# Clean build
./gradlew clean build
```

### Sample Users
Create these in UserService:
```java
User admin = new User();
admin.setUsername("admin");
admin.setPassword("admin123"); // Gets hashed
admin.setRoles(Set.of("USER", "ADMIN"));

User user = new User();
user.setUsername("user");
user.setPassword("user123");
user.setRoles(Set.of("USER"));
```

## Key Teaching Points

### JWT Structure
Draw on whiteboard:
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.HMACSHA256(...)
[    Header    ].[    Payload    ].[  Signature  ]
```

### Authentication Flow
1. Client sends credentials to /auth/login
2. Server validates and returns JWT
3. Client includes JWT in Authorization header
4. Server validates JWT on each request
5. Server checks roles for authorization

### Security Layers
Explain defense in depth:
1. HTTPS (transport security)
2. Authentication (who are you?)
3. Authorization (what can you do?)
4. Input validation (prevent injection)
5. Security headers (browser protection)
6. Rate limiting (prevent abuse)

## Demonstration Script

### 1. Show Insecure Version
```java
// BAD - Never do this!
public class User {
    private String password; // Plain text!
}
```

### 2. BCrypt in Action
```java
String password = "secret123";
String hash = BCrypt.hashpw(password, BCrypt.gensalt());
System.out.println(hash); // $2a$10$...

// Different hash each time (salt)
String hash2 = BCrypt.hashpw(password, BCrypt.gensalt());
System.out.println(hash.equals(hash2)); // false!

// But both validate
BCrypt.checkpw(password, hash); // true
BCrypt.checkpw(password, hash2); // true
```

### 3. JWT Debugging
Show jwt.io to decode tokens and explain claims

### 4. Test the Flow
```bash
# Failed login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrong"}'
# 401 Unauthorized

# Successful login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
# {"token":"eyJ..."}

# Use token
TOKEN="eyJ..."
curl -X GET http://localhost:8080/api/users/admin \
  -H "Authorization: Bearer $TOKEN"
# Success!

# Without token
curl -X GET http://localhost:8080/api/users/admin
# 401 Unauthorized

# Wrong role
# Login as regular user, try admin endpoint
# 403 Forbidden
```

## Troubleshooting Guide

### Issue: "No suitable constructor found for JwtService"
**Fix:** Add @Inject or make constructor public no-args

### Issue: "SignatureException: JWT signature does not match"
**Fix:** 
1. Check SECRET is same for sign and verify
2. Ensure no extra spaces in token
3. Verify Bearer prefix is removed

### Issue: "ExpiredJwtException"
**Fix:** Token expired, generate new one or increase expiration time

### Issue: SecurityContext is null
**Fix:** Filter not running, check @Provider and registration

### Issue: @RolesAllowed not working
**Fix:**
```java
// In JerseyConfig
register(RolesAllowedDynamicFeature.class);
register(AuthenticationFilter.class);
// Order matters!
```

## Advanced Topics (If Time Permits)

### Refresh Tokens
Explain why:
- Short-lived access tokens (15 min)
- Long-lived refresh tokens (7 days)
- Better security and UX

### Token Storage
- Web: httpOnly cookies or memory
- Mobile: Secure keychain/keystore
- Never: localStorage (XSS vulnerable)

### Rate Limiting
```java
// Simple in-memory approach
Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
// Block after 5 attempts
```

## Assessment Checklist

Students should demonstrate:
- [ ] JWT token generation with claims
- [ ] Token validation and parsing
- [ ] Password hashing with BCrypt
- [ ] Role-based authorization working
- [ ] Proper error handling (generic messages)
- [ ] Security headers present
- [ ] No secrets in code

## Additional Notes

- Emphasize this is simplified for training
- Production needs: key rotation, token refresh, audit logs
- Mention OAuth2 for third-party integration
- Discuss microservices authentication patterns

## Links for Students

- jwt.io - Visual token debugger
- bcrypt-generator.com - Test BCrypt hashes
- OWASP Top 10 - Security risks
- 12factor.net - Config best practices