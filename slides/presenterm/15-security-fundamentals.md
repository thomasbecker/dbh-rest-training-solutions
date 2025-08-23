---
title: REST API Security Fundamentals
author: Thomas Becker
theme:
  name: dark
---

# REST API Security Fundamentals

Securing your APIs in production

⏱️ **Duration**: 30 minutes  
🎯 **Goal**: Understand and implement core security patterns

<!-- end_slide -->

---

## Why API Security Matters

### The Stakes Are High

**Data breaches cost an average of $4.45M in 2023** (IBM Report)

<!-- pause -->

Common attack vectors:
- 🔓 Unauthorized access (43%)
- 💉 Injection attacks (19%)
- 🔑 Broken authentication (15%)
- 📡 Man-in-the-middle (12%)

<!-- pause -->

**Your API is the front door to your data** 🚪

<!--
speaker_note: |
  INTRODUCTION (2 minutes)
  
  • Start with impact:
    - Real costs of breaches
    - Reputation damage
    - Legal consequences
  
  • Common mistakes:
    - Trusting client input
    - Weak authentication
    - No rate limiting
    - Exposed sensitive data
  
  • Set serious tone:
    - This is critical
    - Not optional
    - Career-defining skill
-->

<!-- end_slide -->

---

## Security Principles for REST APIs

### Defense in Depth

```
┌─────────────────────────────────┐
│     1. Network Security         │  ← Firewall, TLS
├─────────────────────────────────┤
│   2. Authentication Layer       │  ← Who are you?
├─────────────────────────────────┤
│    3. Authorization Layer       │  ← What can you do?
├─────────────────────────────────┤
│   4. Input Validation Layer     │  ← Trust nothing
├─────────────────────────────────┤
│    5. Business Logic Layer      │  ← Core application
└─────────────────────────────────┘
```

<!-- pause -->

**Never rely on a single security layer!** 🛡️

<!--
speaker_note: |
  DEFENSE IN DEPTH (3 minutes)
  
  • Explain layers:
    1. Network: HTTPS mandatory
    2. Authentication: Identity
    3. Authorization: Permissions
    4. Validation: Never trust input
    5. Business: Final checks
  
  • Real example:
    - User authenticates (layer 2)
    - Has role (layer 3)
    - Input validated (layer 4)
    - Business rules check (layer 5)
  
  • Key point:
    - Each layer independent
    - Failure at one ≠ breach
-->

<!-- end_slide -->

---

## Authentication vs Authorization

### Two Different Concepts

**Authentication (AuthN)**: Who are you? 🔐
- Verifying identity
- Username/password, tokens, certificates

<!-- pause -->

**Authorization (AuthZ)**: What can you do? 🎫
- Checking permissions
- Roles, scopes, ACLs

<!-- pause -->

### In Practice

```java
// Authentication: Is this a valid user?
User user = authenticate(credentials);

// Authorization: Can this user delete?
if (!authorize(user, "DELETE", "/api/users/123")) {
    return Response.status(403).build();
}
```

<!--
speaker_note: |
  AUTH VS AUTH (2 minutes)
  
  • Common confusion:
    - Often mixed up
    - Both needed
    - Different purposes
  
  • Authentication first:
    - Establish identity
    - Create session/token
    - Track user
  
  • Authorization second:
    - Check permissions
    - Resource-specific
    - Action-specific
  
  • 401 vs 403:
    - 401: Who are you?
    - 403: You can't do that
-->

<!-- end_slide -->

---

## HTTPS/TLS: The Foundation

### Why HTTPS is Mandatory

Without HTTPS:
```
Client ──[plaintext]──> Internet ──[plaintext]──> Server
         "password123"            "password123"
         
         🦹 Attacker can see everything!
```

<!-- pause -->

With HTTPS:
```
Client ──[encrypted]──> Internet ──[encrypted]──> Server
         "a3f9x2..."              "a3f9x2..."
         
         🦹 Attacker sees gibberish
```

<!-- pause -->

### In Jersey

```java
// Enforce HTTPS in production
@Provider
public class HttpsEnforcementFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext request) {
        if (!request.getSecurityContext().isSecure()) {
            throw new ForbiddenException("HTTPS required");
        }
    }
}
```

<!--
speaker_note: |
  HTTPS/TLS (3 minutes)
  
  • Non-negotiable:
    - Always in production
    - Even internal APIs
    - No exceptions
  
  • What it prevents:
    - Eavesdropping
    - Tampering
    - Impersonation
  
  • Common mistakes:
    - HTTP in development
    - Mixed content
    - Weak ciphers
  
  • Certificate management:
    - Let's Encrypt for public
    - Internal CA for private
    - Rotation critical
-->

<!-- end_slide -->

---

## Basic Authentication

### Simple but Limited

How it works:
```http
GET /api/users
Authorization: Basic dXNlcjpwYXNzd29yZA==
                     └── base64(user:password)
```

<!-- pause -->

### Jersey Implementation

```java
@Provider
@Priority(Priorities.AUTHENTICATION)
public class BasicAuthFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            abort(requestContext);
            return;
        }
        
        String credentials = new String(
            Base64.getDecoder().decode(
                authHeader.substring(6)
            )
        );
        
        String[] parts = credentials.split(":");
        if (!authenticate(parts[0], parts[1])) {
            abort(requestContext);
        }
    }
}
```

<!--
speaker_note: |
  BASIC AUTH (4 minutes)
  
  • How it works:
    - Every request has credentials
    - Base64 encoded (NOT encrypted)
    - Server validates each time
  
  • Pros:
    - Simple to implement
    - Wide support
    - Stateless
  
  • Cons:
    - Credentials sent every time
    - No logout mechanism
    - Requires HTTPS
    - Poor user experience
  
  • When to use:
    - Internal APIs
    - Service-to-service
    - Simple tools
    - NOT for public APIs
-->

<!-- end_slide -->

---

## Basic Auth: Security Considerations

### ⚠️ Critical Limitations

1. **Credentials sent with every request**
   - Higher exposure risk
   - Can't revoke easily

<!-- pause -->

2. **No built-in expiration**
   - Sessions last forever
   - No forced re-authentication

<!-- pause -->

3. **Base64 is NOT encryption**
   - Anyone can decode: `base64 -d`
   - Example: `dXNlcjpwYXNzd29yZA==` → `user:password`

<!-- pause -->

### ✅ When Basic Auth is OK

- Internal microservices (with mutual TLS)
- Development/testing environments
- Simple admin tools (with IP restrictions)
- Git repositories (with personal access tokens)

<!--
speaker_note: |
  BASIC AUTH SECURITY (3 minutes)
  
  • Emphasize risks:
    - Not for public APIs
    - Always needs HTTPS
    - Consider alternatives
  
  • Mitigation strategies:
    - Rate limiting essential
    - IP whitelisting
    - Strong passwords
    - Regular rotation
  
  • Better alternatives:
    - OAuth 2.0
    - JWT tokens
    - API keys
  
  • Real-world usage:
    - GitHub (with PATs)
    - Docker Registry
    - Elasticsearch
-->

<!-- end_slide -->

---

## JWT (JSON Web Tokens)

### Modern Token-Based Authentication

Structure: `header.payload.signature`

```javascript
// Decoded JWT
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user123",
    "name": "John Doe",
    "roles": ["USER", "ADMIN"],
    "exp": 1716239022
  },
  "signature": "HMACSHA256(...)"
}
```

<!-- pause -->

### Key Benefits

✅ **Stateless** - No server sessions  
✅ **Self-contained** - Includes user info  
✅ **Expirable** - Built-in expiration  
✅ **Secure** - Cryptographically signed  

<!--
speaker_note: |
  JWT INTRODUCTION (3 minutes)
  
  • Structure explanation:
    - Header: Algorithm info
    - Payload: Claims/data
    - Signature: Verification
  
  • How it works:
    1. User logs in
    2. Server creates JWT
    3. Client stores JWT
    4. Sends with requests
    5. Server validates
  
  • Key advantages:
    - No database lookups
    - Horizontal scaling
    - Mobile-friendly
    - Microservices ready
-->

<!-- end_slide -->

---

## JWT Implementation in Jersey

### Creating JWTs

```java
public class JwtService {
    private static final String SECRET = System.getenv("JWT_SECRET");
    private static final long EXPIRATION = 3600000; // 1 hour
    
    public String createToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
    }
}
```

<!-- pause -->

### Login Endpoint

```java
@POST
@Path("/login")
public Response login(@Valid LoginRequest request) {
    User user = authenticate(request.getUsername(), request.getPassword());
    if (user == null) {
        return Response.status(401).build();
    }
    
    String token = jwtService.createToken(user);
    return Response.ok(new TokenResponse(token)).build();
}
```

<!--
speaker_note: |
  JWT CREATION (3 minutes)
  
  • Token creation:
    - Include user identity
    - Add roles/permissions
    - Set expiration
    - Sign with secret
  
  • Security notes:
    - Strong secret key
    - Environment variable
    - Never hardcode
    - Rotate regularly
  
  • Token lifetime:
    - Short for sensitive
    - 15 min - 1 hour typical
    - Refresh tokens for long
-->

<!-- end_slide -->

---

## JWT Validation Filter

### Verify Every Request

```java
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abort(requestContext);
            return;
        }
        
        String token = authHeader.substring(7);
        
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
            
            // Set security context with user info
            requestContext.setSecurityContext(
                new JwtSecurityContext(claims)
            );
            
        } catch (JwtException e) {
            abort(requestContext);
        }
    }
}
```

<!--
speaker_note: |
  JWT VALIDATION (3 minutes)
  
  • Validation steps:
    1. Extract token
    2. Verify signature
    3. Check expiration
    4. Extract claims
    5. Set context
  
  • Common errors:
    - Expired tokens
    - Invalid signature
    - Malformed JWT
  
  • Security context:
    - User principal
    - Roles
    - Request metadata
-->

<!-- end_slide -->

---

## JWT Security Best Practices

### 🔐 Critical Guidelines

1. **Never store sensitive data in JWT**
   - Tokens are readable (base64)
   - Only store IDs and roles

<!-- pause -->

2. **Use strong secrets**
   - BAD: `String secret = "secret123"`
   - GOOD: `String secret = System.getenv("JWT_SECRET_256_BIT")`

<!-- pause -->

3. **Implement token refresh**
   - Short-lived access token (15 min)
   - Long-lived refresh token (7 days)
   - Refresh before expiration

<!-- pause -->

4. **Blacklist revoked tokens**
   - Store revoked JTIs in Redis
   - Check on each request

<!--
speaker_note: |
  JWT BEST PRACTICES (3 minutes)
  
  • Common mistakes:
    - Storing passwords
    - Weak secrets
    - No expiration
    - No revocation
  
  • Token refresh flow:
    1. Access token expires
    2. Use refresh token
    3. Get new access token
    4. Continue working
  
  • Revocation strategies:
    - Blacklist (simple)
    - Short expiration
    - Version tracking
  
  • Storage:
    - localStorage (XSS risk)
    - Cookie (CSRF risk)
    - Memory + refresh
-->

<!-- end_slide -->

---

## API Key Authentication

### For Service-to-Service Communication

Common patterns:
```http
# Header-based
GET /api/data
X-API-Key: sk_live_51H3bgKG7d9ipPkXC4pT8vL7u

# Query parameter (less secure)
GET /api/data?api_key=sk_live_51H3bgKG7d9ipPkXC4pT8vL7u
```

<!-- pause -->

### Implementation

```java
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiKeyFilter implements ContainerRequestFilter {
    
    @Inject
    private ApiKeyService apiKeyService;
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String apiKey = requestContext.getHeaderString("X-API-Key");
        
        if (apiKey == null) {
            abort(requestContext);
            return;
        }
        
        ApiKeyDetails details = apiKeyService.validate(apiKey);
        if (details == null || !details.isActive()) {
            abort(requestContext);
            return;
        }
        
        // Track usage for rate limiting
        apiKeyService.recordUsage(details);
    }
}
```

<!--
speaker_note: |
  API KEYS (3 minutes)
  
  • Use cases:
    - Public APIs
    - Partner integration
    - Mobile apps
    - IoT devices
  
  • Key features:
    - Easy to implement
    - Easy to revoke
    - Per-client limits
    - Usage tracking
  
  • Best practices:
    - Prefix keys (sk_, pk_)
    - Hash in database
    - Rotate regularly
    - Monitor usage
-->

<!-- end_slide -->

---

## API Key Best Practices

### 🔑 Key Management

```java
public class ApiKeyService {
    
    // Generate cryptographically secure keys
    public String generateApiKey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return "sk_live_" + Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }
    
    // Store hashed version
    public void storeApiKey(String apiKey, String clientId) {
        String hashedKey = BCrypt.hashpw(apiKey, BCrypt.gensalt());
        database.save(new ApiKeyEntity(hashedKey, clientId));
    }
    
    // Validate with rate limiting
    public boolean validate(String apiKey) {
        // Check rate limits first
        if (rateLimiter.isExceeded(apiKey)) {
            return false;
        }
        
        // Then validate key
        return database.findByKey(apiKey) != null;
    }
}
```

<!-- pause -->

### Security Checklist

✅ Hash keys in database  
✅ Use secure random generation  
✅ Implement rate limiting  
✅ Log all API key usage  
✅ Rotate keys periodically  

<!--
speaker_note: |
  API KEY SECURITY (2 minutes)
  
  • Storage security:
    - Never plain text
    - BCrypt or Argon2
    - Separate from code
  
  • Rate limiting essential:
    - Per key limits
    - Prevent abuse
    - Cost control
  
  • Monitoring:
    - Track all usage
    - Alert anomalies
    - Audit trails
  
  • Rotation:
    - Regular schedule
    - Grace period
    - Notify clients
-->

<!-- end_slide -->

---

## Rate Limiting & Throttling

### Protect Your API from Abuse

```java
@Provider
public class RateLimitFilter implements ContainerRequestFilter {
    
    private final RateLimiter rateLimiter = RateLimiter.create(100.0); // 100 req/sec
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String clientId = extractClientId(requestContext);
        
        if (!rateLimiter.tryAcquire()) {
            requestContext.abortWith(
                Response.status(429) // Too Many Requests
                    .header("X-RateLimit-Limit", "100")
                    .header("X-RateLimit-Remaining", "0")
                    .header("X-RateLimit-Reset", getResetTime())
                    .entity("Rate limit exceeded")
                    .build()
            );
        }
    }
}
```

<!-- pause -->

### Rate Limit Headers

```http
HTTP/1.1 429 Too Many Requests
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1678886400
Retry-After: 60
```

<!--
speaker_note: |
  RATE LIMITING (3 minutes)
  
  • Why essential:
    - Prevent DoS
    - Fair usage
    - Cost control
    - Stability
  
  • Strategies:
    - Fixed window
    - Sliding window
    - Token bucket
    - Leaky bucket
  
  • Per what:
    - IP address
    - API key
    - User account
    - Endpoint
  
  • Response codes:
    - 429 Too Many Requests
    - Include headers
    - Retry-After hint
-->

<!-- end_slide -->

---

## Input Validation & Sanitization

### Never Trust Client Input

```java
@POST
@Path("/users")
public Response createUser(@Valid @NotNull User user) {
    // Bean Validation handles basic validation
    
    // Additional security validation
    if (containsSqlInjection(user.getName())) {
        return Response.status(400)
            .entity("Invalid characters in name")
            .build();
    }
    
    // Sanitize HTML/JavaScript
    user.setName(Jsoup.clean(user.getName(), Whitelist.basic()));
    user.setBio(Jsoup.clean(user.getBio(), Whitelist.basic()));
    
    // Validate business rules
    if (userService.existsByEmail(user.getEmail())) {
        return Response.status(409)
            .entity("Email already registered")
            .build();
    }
    
    return Response.ok(userService.create(user)).build();
}
```

<!-- pause -->

### Common Attack Vectors

❌ SQL Injection: `'; DROP TABLE users; --`  
❌ XSS: `<script>alert('XSS')</script>`  
❌ Command Injection: `; rm -rf /`  

<!--
speaker_note: |
  INPUT VALIDATION (3 minutes)
  
  • Layer approach:
    1. Type validation
    2. Format validation
    3. Business validation
    4. Sanitization
  
  • Common attacks:
    - SQL injection
    - XSS attacks
    - Command injection
    - Path traversal
  
  • Best practices:
    - Whitelist, not blacklist
    - Parameterized queries
    - Escape output
    - Limit input size
  
  • Tools:
    - Bean Validation
    - OWASP Java Encoder
    - Jsoup for HTML
-->

<!-- end_slide -->

---

## CORS Security

### Cross-Origin Resource Sharing

```java
@Provider
public class CORSFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext request,
                      ContainerResponseContext response) {
        
        // BAD: Too permissive
        // response.getHeaders().add("Access-Control-Allow-Origin", "*");
        
        // GOOD: Whitelist origins
        String origin = request.getHeaderString("Origin");
        if (isAllowedOrigin(origin)) {
            response.getHeaders().add("Access-Control-Allow-Origin", origin);
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
            response.getHeaders().add("Access-Control-Allow-Methods", 
                "GET, POST, PUT, DELETE, OPTIONS");
            response.getHeaders().add("Access-Control-Max-Age", "3600");
        }
    }
    
    private boolean isAllowedOrigin(String origin) {
        return Arrays.asList(
            "https://app.example.com",
            "https://staging.example.com"
        ).contains(origin);
    }
}
```

<!--
speaker_note: |
  CORS SECURITY (2 minutes)
  
  • Common mistakes:
    - Allow all origins (*)
    - Allow all headers
    - No preflight cache
  
  • Security risks:
    - Data exposure
    - CSRF attacks
    - Credential leaks
  
  • Best practices:
    - Whitelist origins
    - Limit methods
    - Short cache time
    - Validate preflight
-->

<!-- end_slide -->

---

## Security Headers

### Essential HTTP Security Headers

```java
@Provider
public class SecurityHeadersFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext request,
                      ContainerResponseContext response) {
        
        // Prevent clickjacking
        response.getHeaders().add("X-Frame-Options", "DENY");
        
        // Prevent MIME sniffing
        response.getHeaders().add("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        response.getHeaders().add("X-XSS-Protection", "1; mode=block");
        
        // Force HTTPS
        response.getHeaders().add("Strict-Transport-Security", 
            "max-age=31536000; includeSubDomains");
        
        // Content Security Policy
        response.getHeaders().add("Content-Security-Policy", 
            "default-src 'self'; script-src 'self'");
        
        // Referrer Policy
        response.getHeaders().add("Referrer-Policy", "same-origin");
    }
}
```

<!-- pause -->

**These headers prevent 90% of common web attacks!** 🛡️

<!--
speaker_note: |
  SECURITY HEADERS (2 minutes)
  
  • Each header purpose:
    - Frame-Options: Clickjacking
    - Content-Type: MIME attacks
    - XSS-Protection: XSS
    - HSTS: Force HTTPS
    - CSP: Content control
    - Referrer: Data leakage
  
  • Implementation:
    - Global filter
    - All responses
    - Test thoroughly
  
  • Tools:
    - securityheaders.com
    - Mozilla Observatory
-->

<!-- end_slide -->

---

## Secure Password Storage

### Never Store Plain Text Passwords!

```java
public class PasswordService {
    
    // Use BCrypt for password hashing
    private static final int BCRYPT_ROUNDS = 12;
    
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    // Password requirements
    public boolean isPasswordStrong(String password) {
        return password.length() >= 12 &&
               password.matches(".*[A-Z].*") &&     // Uppercase
               password.matches(".*[a-z].*") &&     // Lowercase
               password.matches(".*[0-9].*") &&     // Digit
               password.matches(".*[!@#$%^&*].*");  // Special char
    }
}
```

<!-- pause -->

### Industry Standards

✅ BCrypt (recommended)  
✅ Argon2 (newer, better)  
✅ PBKDF2 (NIST approved)  
❌ MD5 (broken)  
❌ SHA-1 (broken)  
❌ Plain SHA-256 (too fast)  

<!--
speaker_note: |
  PASSWORD SECURITY (2 minutes)
  
  • Why hashing:
    - One-way function
    - Can't reverse
    - Unique per password
  
  • Salt importance:
    - Prevents rainbow tables
    - Unique per password
    - Stored with hash
  
  • Work factor:
    - Slow by design
    - Adjustable difficulty
    - Future-proof
  
  • Common mistakes:
    - Simple hashing
    - No salt
    - Weak passwords
    - No rotation
-->

<!-- end_slide -->

---

## Audit Logging

### Track Everything Security-Related

```java
@Provider
public class AuditLogFilter implements ContainerRequestFilter {
    
    @Inject
    private AuditService auditService;
    
    @Override
    public void filter(ContainerRequestContext request) {
        AuditEvent event = AuditEvent.builder()
            .timestamp(Instant.now())
            .user(extractUser(request))
            .ip(request.getHeaderString("X-Forwarded-For"))
            .method(request.getMethod())
            .path(request.getUriInfo().getPath())
            .headers(sanitizeHeaders(request.getHeaders()))
            .build();
        
        // Log authentication failures
        if (request.getProperty("auth.failed") != null) {
            event.setType("AUTH_FAILURE");
            event.setSeverity("HIGH");
        }
        
        // Log sensitive operations
        if (isSensitiveOperation(request)) {
            event.setType("SENSITIVE_OPERATION");
            event.setSeverity("MEDIUM");
        }
        
        auditService.log(event);
    }
}
```

<!-- pause -->

### What to Log

✅ Authentication attempts  
✅ Authorization failures  
✅ Data modifications  
✅ Admin actions  
✅ API key usage  

<!--
speaker_note: |
  AUDIT LOGGING (2 minutes)
  
  • Why audit:
    - Compliance (GDPR, etc)
    - Forensics
    - Debugging
    - Monitoring
  
  • What to log:
    - Who, what, when, where
    - Success and failure
    - Sensitive operations
  
  • Storage:
    - Separate system
    - Immutable
    - Encrypted
    - Retained properly
  
  • GDPR considerations:
    - No passwords
    - Minimal PII
    - Retention limits
-->

<!-- end_slide -->

---

## Common Security Mistakes

### Top 10 API Security Failures

1. **No HTTPS in production** 🔓
2. **Storing passwords in plain text** 📝
3. **No rate limiting** 🏃
4. **Exposing sensitive data in responses** 📊
5. **Missing authentication on endpoints** 🚪
6. **Using GET for state changes** 💾
7. **No input validation** 💉
8. **Verbose error messages** 📢
9. **No audit logging** 📓
10. **Default/weak secrets** 🔑

<!-- pause -->

### The #1 Rule

**"Never trust the client"** 🚫

- Validate everything
- Authenticate everything
- Log everything
- Encrypt everything

<!--
speaker_note: |
  COMMON MISTAKES (2 minutes)
  
  • Each mistake impact:
    1. HTTPS: Everything visible
    2. Plain passwords: Breach
    3. No rate limit: DoS
    4. Data exposure: Privacy
    5. No auth: Free access
    6. GET changes: CSRF
    7. No validation: Injection
    8. Verbose errors: Info leak
    9. No audit: No forensics
    10. Weak secrets: Easy break
  
  • Prevention:
    - Security checklist
    - Code reviews
    - Penetration testing
    - Security training
-->

<!-- end_slide -->

---

## Security Testing

### Validate Your Security

```java
@Test
public void testAuthenticationRequired() {
    // Should fail without auth
    given()
        .when()
        .get("/api/users")
        .then()
        .statusCode(401);
}

@Test
public void testInvalidTokenRejected() {
    given()
        .header("Authorization", "Bearer invalid.token.here")
        .when()
        .get("/api/users")
        .then()
        .statusCode(401);
}

@Test
public void testRateLimiting() {
    // Make 101 requests (limit is 100)
    for (int i = 0; i < 101; i++) {
        Response response = given()
            .header("X-API-Key", "test-key")
            .when()
            .get("/api/data");
        
        if (i == 100) {
            assertEquals(429, response.getStatusCode());
        }
    }
}
```

<!--
speaker_note: |
  SECURITY TESTING (2 minutes)
  
  • Test categories:
    - Authentication
    - Authorization  
    - Input validation
    - Rate limiting
    - Security headers
  
  • Tools:
    - OWASP ZAP
    - Burp Suite
    - RestAssured tests
    - Postman collections
  
  • Continuous testing:
    - CI/CD integration
    - Regular scans
    - Dependency checks
-->

<!-- end_slide -->

---

## Security Checklist

### Before Going to Production

**Authentication & Authorization**
- [ ] HTTPS enforced
- [ ] Strong authentication implemented
- [ ] Authorization checks on all endpoints
- [ ] Tokens expire appropriately

**Data Protection**
- [ ] Passwords hashed with BCrypt/Argon2
- [ ] Sensitive data encrypted at rest
- [ ] PII not logged
- [ ] API responses minimized

**Input/Output**
- [ ] All input validated
- [ ] SQL injection prevented
- [ ] XSS protection enabled
- [ ] Error messages sanitized

**Infrastructure**
- [ ] Rate limiting enabled
- [ ] Security headers configured
- [ ] CORS properly configured
- [ ] Audit logging implemented

<!--
speaker_note: |
  SECURITY CHECKLIST (1 minute)
  
  • Use as template:
    - Pre-production review
    - Security audit
    - Code review guide
  
  • Regular reviews:
    - Quarterly audits
    - Dependency updates
    - Penetration tests
  
  • Team responsibility:
    - Everyone's job
    - Security training
    - Incident response plan
-->

<!-- end_slide -->

---

## Resources & Standards

### Industry Standards

**OWASP Top 10 API Security Risks**
- API1: Broken Object Level Authorization
- API2: Broken User Authentication
- API3: Excessive Data Exposure
- API4: Lack of Resources & Rate Limiting

<!-- pause -->

### Essential Reading

📚 **OWASP API Security Top 10**  
📚 **NIST Cybersecurity Framework**  
📚 **OAuth 2.0 Security Best Practices (RFC 8252)**  
📚 **JWT Best Practices (RFC 8725)**  

<!-- pause -->

### Security Tools

🔧 **OWASP ZAP** - Security scanning  
🔧 **Burp Suite** - Penetration testing  
🔧 **Snyk** - Dependency scanning  
🔧 **SonarQube** - Code analysis  

<!--
speaker_note: |
  RESOURCES (1 minute)
  
  • OWASP importance:
    - Industry standard
    - Regular updates
    - Free resources
  
  • Continuous learning:
    - Security evolves
    - New threats
    - New defenses
  
  • Tool adoption:
    - Start with one
    - Integrate CI/CD
    - Regular scans
-->

<!-- end_slide -->

---

## Key Takeaways 🎯

<!-- pause -->

✅ **Security is not optional** - It's a fundamental requirement

<!-- pause -->

✅ **Defense in depth** - Multiple layers of security

<!-- pause -->

✅ **HTTPS everywhere** - Non-negotiable in production

<!-- pause -->

✅ **Never trust client input** - Validate and sanitize everything

<!-- pause -->

✅ **Use proven standards** - JWT, OAuth 2.0, BCrypt

<!-- pause -->

✅ **Monitor and audit** - Log security events

<!-- pause -->

✅ **Keep learning** - Security landscape changes rapidly

<!--
speaker_note: |
  TAKEAWAYS (1 minute)
  
  • Key message:
    - Security is everyone's job
    - Start from day one
    - Never stop improving
  
  • Career impact:
    - Valuable skill
    - Differentiator
    - Leadership quality
  
  • Next steps:
    - Implement one thing
    - Share knowledge
    - Stay updated
-->

<!-- end_slide -->

---

## Questions & Discussion

### Common Questions

**Q: Basic Auth vs JWT for internal APIs?**
→ JWT even internally - better for microservices

<!-- pause -->

**Q: How long should JWT tokens live?**
→ Access: 15-60 min, Refresh: 7-30 days

<!-- pause -->

**Q: Is OAuth 2.0 overkill for simple APIs?**
→ No - use existing providers (Auth0, Okta)

<!-- pause -->

**Q: How to handle API key rotation?**
→ Overlap period, versioning, notify clients

<!-- pause -->

### Your Security Questions?

<!--
speaker_note: |
  Q&A (2 minutes)
  
  • Common concerns:
    - Implementation complexity
    - Performance impact
    - User experience
  
  • Reassurance:
    - Start simple
    - Iterate
    - Use libraries
  
  • Encourage:
    - No stupid questions
    - Share experiences
    - Learn from mistakes
-->

<!-- end_slide -->

---

## What's Next? 🚀

### This Afternoon: Security Implementation

**Hands-on Exercise (60 minutes)**
- Implement JWT authentication
- Add role-based authorization
- Configure security headers
- Test security measures

<!-- pause -->

### You'll Build:

✅ Login endpoint with JWT generation  
✅ Token validation filter  
✅ Role-based access control  
✅ Rate limiting  
✅ Security tests  

<!-- pause -->

**Let's secure those APIs!** 🔐

<!--
speaker_note: |
  TRANSITION (1 minute)
  
  • Set expectations:
    - Practical implementation
    - Real-world patterns
    - Working code
  
  • Encourage:
    - Don't fear security
    - Ask questions
    - Help others
  
  • Lunch break:
    - Think about security
    - Prepare questions
    - Get ready to code
-->

<!-- end_slide -->