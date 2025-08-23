---
title: Exercise 08 - Security Implementation
author: Thomas Becker  
theme:
  name: dark
---

# 🔐 Exercise 08: Security Implementation

Duration: **60 minutes**

Implement JWT authentication and role-based authorization for REST APIs

<!--
speaker_note: |
  This exercise puts security theory into practice.
  Focus on understanding rather than memorizing patterns.
  JWT is the modern standard for stateless auth.
  Emphasize proper secret management from the start.
-->

<!-- end_slide -->

---

## Your Mission

Build a secure REST API with:
- JWT token-based authentication
- Role-based access control (RBAC)
- Security filters and headers
- Password hashing

<!--
speaker_note: |
  Students will implement production-grade security.
  This is critical for real-world applications.
  Many developers get this wrong - let's do it right.
-->

<!-- end_slide -->

---

## Task 1: JWT Dependencies (10 min)

📚 JJWT Documentation: github.com/jwtk/jjwt

💡 **Hint**: Use JJWT 0.11.5 for Java 8 compatibility

<!-- pause -->

**Add to build.gradle:**
```gradle
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
implementation 'org.mindrot:jbcrypt:0.4'
```

<!-- pause -->

Expected: Dependencies resolve without errors

<!--
speaker_note: |
  JJWT is the industry standard for Java JWT.
  BCrypt for password hashing is crucial.
  Version 0.11.5 works with Java 8.
-->

<!-- end_slide -->

---

## Task 2: User Model with Credentials (10 min)

📚 BCrypt Documentation: mindrot.org/jbcrypt

💡 **Hint**: Never store plain text passwords!

<!-- pause -->

**Enhance User model:**
```java
@JsonIgnore
private String passwordHash;
private Set<String> roles = new HashSet<>();

public boolean checkPassword(String password) {
    return BCrypt.checkpw(password, passwordHash);
}

public void setPassword(String password) {
    this.passwordHash = BCrypt.hashpw(password, 
                                      BCrypt.gensalt());
}
```

<!--
speaker_note: |
  Password hashing is non-negotiable.
  BCrypt includes salt automatically.
  JsonIgnore prevents password leakage.
-->

<!-- end_slide -->

---

## Task 3: JWT Service (15 min)

📚 JWT Best Practices: cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet

💡 **Hint**: Store secret key in environment variable

<!-- pause -->

**Create JwtService.java:**
```java
public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("roles", user.getRoles())
        .setIssuedAt(new Date())
        .setExpiration(/* 1 hour from now */)
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
}
```

<!-- pause -->

Expected: Token generation with claims

<!--
speaker_note: |
  Secret key management is critical.
  Short expiration times increase security.
  Include minimal claims in token.
-->

<!-- end_slide -->

---

## Task 4: Authentication Filter (15 min)

📚 ContainerRequestFilter: eclipse-ee4j.github.io/jersey.github.io/documentation/latest/filters-and-interceptors.html

💡 **Hint**: Extract token from Authorization header

<!-- pause -->

**AuthenticationFilter.java:**
```java
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter 
    implements ContainerRequestFilter {
    
    public void filter(ContainerRequestContext ctx) {
        String token = extractToken(ctx);
        if (token != null) {
            User user = validateToken(token);
            ctx.setSecurityContext(
                new UserSecurityContext(user));
        }
    }
}
```

<!--
speaker_note: |
  Filter runs before every request.
  Priority ensures correct execution order.
  SecurityContext propagates user info.
-->

<!-- end_slide -->

---

## Task 5: Role-Based Authorization (10 min)

📚 @RolesAllowed: docs.oracle.com/javaee/7/api/javax/annotation/security/RolesAllowed.html

💡 **Hint**: Register RolesAllowedDynamicFeature

<!-- pause -->

**Enable role checking:**
```java
// In JerseyConfig
register(RolesAllowedDynamicFeature.class);

// In UserResource
@GET
@Path("/admin")
@RolesAllowed("ADMIN")
public Response getAdminData() {
    return Response.ok("Admin only data").build();
}
```

<!-- pause -->

Expected: 403 Forbidden without ADMIN role

<!--
speaker_note: |
  Declarative security with annotations.
  Clean separation of concerns.
  Framework handles the authorization logic.
-->

<!-- end_slide -->

---

## Task 6: Login Endpoint (10 min)

📚 JWT Response Pattern: tools.ietf.org/html/rfc7519

💡 **Hint**: Return token in response body

<!-- pause -->

**AuthResource.java:**
```java
@POST
@Path("/login")
public Response login(LoginRequest request) {
    User user = userService.authenticate(
        request.getUsername(), 
        request.getPassword()
    );
    
    if (user != null) {
        String token = jwtService.generateToken(user);
        return Response.ok(new TokenResponse(token))
                      .build();
    }
    return Response.status(401).build();
}
```

<!--
speaker_note: |
  Login is the only unprotected endpoint.
  Always return generic error for failed auth.
  Token should be stored securely on client.
-->

<!-- end_slide -->

---

## Security Headers Reference

| Header | Value | Purpose |
|--------|-------|---------|
| X-Content-Type-Options | nosniff | Prevent MIME sniffing |
| X-Frame-Options | DENY | Prevent clickjacking |
| Strict-Transport-Security | max-age=31536000 | Force HTTPS |
| Content-Security-Policy | default-src 'self' | XSS protection |

<!-- pause -->

💡 Add via ResponseFilter or web.xml

<!--
speaker_note: |
  Security headers are defense in depth.
  Easy to add, significant security benefit.
  Consider using a security headers library.
-->

<!-- end_slide -->

---

## Common Security Mistakes ❌

1. ❌ Storing passwords in plain text
2. ❌ JWT secret in source code
3. ❌ No token expiration
4. ❌ Logging sensitive data
5. ❌ Missing HTTPS in production
6. ❌ Weak password requirements
7. ❌ No rate limiting
8. ❌ Exposing stack traces

<!--
speaker_note: |
  These are real vulnerabilities seen in production.
  Each one can lead to a breach.
  Security is only as strong as weakest link.
-->

<!-- end_slide -->

---

## Testing Your Security

```bash
# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret"}'

# Use token
curl -X GET http://localhost:8080/api/users/admin \
  -H "Authorization: Bearer <token>"

# Test unauthorized
curl -X GET http://localhost:8080/api/users/admin
# Expected: 401 Unauthorized
```

<!--
speaker_note: |
  Manual testing helps understand the flow.
  Automated tests are essential for CI/CD.
  Test both positive and negative cases.
-->

<!-- end_slide -->

---

## ⏱️ Checkpoint: 30 Minutes

You should have:
- ✅ JWT dependencies added
- ✅ User model with password hashing
- ✅ JWT service generating tokens

<!-- pause -->

Need help? Check:
- Token structure at jwt.io
- BCrypt test at bcrypt-generator.com

<!--
speaker_note: |
  Half-way checkpoint for pacing.
  JWT.io is great for debugging tokens.
  If behind, focus on core auth flow.
-->

<!-- end_slide -->

---

## Bonus Tasks 🌟

If you finish early:

1. **Refresh Tokens** (15 min)
   - Implement refresh token flow
   
2. **Brute Force Protection** (10 min)
   - Add login attempt limiting
   
3. **Token Blacklist** (10 min)
   - Implement token revocation
   
4. **Audit Logging** (10 min)
   - Log all authentication events

<!--
speaker_note: |
  These are production considerations.
  Refresh tokens improve UX.
  Rate limiting prevents attacks.
  Audit logs are compliance requirement.
-->

<!-- end_slide -->

---

## OWASP Top 10 API Security

Quick reference for API vulnerabilities:

1. **API1** - Broken Object Level Authorization
2. **API2** - Broken Authentication
3. **API3** - Broken Object Property Level Authorization
4. **API4** - Unrestricted Resource Consumption
5. **API5** - Broken Function Level Authorization

<!-- pause -->

Our implementation addresses API2 and API5 ✅

<!--
speaker_note: |
  OWASP Top 10 is industry standard.
  We're addressing authentication and authorization.
  Consider other items for production.
-->

<!-- end_slide -->

---

## Key Takeaways 🎯

<!-- pause -->

→ **JWT enables stateless authentication**

<!-- pause -->

→ **Always hash passwords with salt**

<!-- pause -->

→ **Use @RolesAllowed for clean authorization**

<!-- pause -->

→ **Security is layered - headers, HTTPS, validation**

<!-- pause -->

→ **Follow OWASP guidelines**

<!--
speaker_note: |
  These are the critical points to remember.
  Security is not optional in modern APIs.
  JWT is the standard for microservices.
  Always stay updated on security best practices.
-->

<!-- end_slide -->

---

## Questions?

Ready for the comprehensive exercise!

Next: **Exercise 09 - Comprehensive API**

<!--
speaker_note: |
  Check understanding of JWT flow.
  Ensure everyone has working authentication.
  The next exercise brings everything together.
-->

<!-- end_slide -->