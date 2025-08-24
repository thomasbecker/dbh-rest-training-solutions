---
title: REST API Training with Jersey
author: Thomas Becker
date: August 26, 2025
---

# Welcome to REST API Training

**DBH REST API Training**  
Day 1 - Module 1

Instructor: Thomas Becker  
Target Environment: **Java 8**

<!--
speaker_note: |
  INTRODUCTION (5 minutes)

  • Welcome everyone to REST API Training with Jersey

  • Introduce yourself:
    - Your background with REST APIs
    - Experience with Jersey framework
    - Years in enterprise Java development

  • Quick participant survey:
    - "Who has worked with REST APIs before?"
    - "Any experience with SOAP/XML services?"
    - "Who's using Spring Boot currently?"
    - "What are your main API challenges?"

  • Set expectations:
    - This is Jersey-focused (NOT Spring Boot)
    - Hands-on approach - 70% coding
    - Java 8 environment (confirm everyone has it)
    - We'll build a complete API by end of Day 2

  • Logistics:
    - Breaks every 60 minutes
    - Questions welcome anytime
    - Code will be in GitHub
    - Pair programming encouraged

  • Key point: Jersey is lighter than Spring Boot
    - Better for microservices
    - Less magic, more control
    - JAX-RS standard compliant
-->

<!-- end_slide -->

# About Me - Thomas Becker

**Consultant | Senior Developer | Architect | Coach**

## Technical Background

- **Eclipse/Jetty Committer**
- **Contributor SPDY Specification**
- Loves concurrent code
- Performance Tuning Expert
- JVM Tuning Expert

## Experience

- Doing web- and server applications since **1997**
- Doing REST since 2006

## When not coding...

🧗 My Children/Coding/Soccer/Bouldering/Climbing/Mountainbiking

<!--
speaker_note: |
  PERSONAL INTRODUCTION (2 minutes)

  • Highlight relevant experience:
    - 25+ years in web development
    - Deep JVM and performance expertise
    - Open source contributions (Jetty/SPDY)
    - Twitter introduced REST in 2006
    - de facto standard for APIs since ~2010-2015

  • Why this matters for the training:
    - Real-world production experience
    - Performance-aware API design
    - Concurrent/async patterns in REST
    - CI/CD best practices for APIs

  • Quick personal note:
    - Climbing teaches problem-solving
    - Similar to debugging complex APIs!

  • Transition:
    - "Let's start with today's agenda..."
-->

<!-- end_slide -->

# Agenda - Day 1

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### Morning (9:00 - 12:30)

- Module 1: REST Fundamentals
- Module 2: Resource Design
- Module 3: Jersey Framework Setup

<!-- column: 1 -->

### Afternoon (13:30 - 17:00)

- Module 4: CRUD Operations
- Module 5: API Versioning
- Module 6: Error Handling

<!-- reset_layout -->

<!--
speaker_note: |
  DAY 1 OVERVIEW (3 minutes)

  • Morning focus:
    - Theory heavy but interactive
    - REST fundamentals everyone needs
    - Jersey framework basics
    - We'll set up before lunch

  • Afternoon focus:
    - 70% hands-on coding
    - CRUD = Create, Read, Update, Delete
    - Versioning strategies
    - Error handling patterns

  • Pace management:
    - Check understanding frequently
    - Adjust based on questions
    - Have backup exercises ready
-->

<!-- end_slide -->

# What is REST?

## **Representational State Transfer**

Architectural style for distributed systems

<!-- pause -->

### Key Principles:

- Client-Server separation
- Statelessness
- Uniform Interface
- Layered System
- Cacheability

<!--
speaker_note: |
  WHAT IS REST? (5 minutes)

  • Key concept: Architectural STYLE not protocol
    - Like building architecture patterns
    - Not a specification you implement

  • History context:
    - Roy Fielding 2000 PhD dissertation
    - Co-author of HTTP specification
    - Became mainstream ~2010-2015

  • The 5 constraints (explain each):
    1. Client-Server: separation of concerns
    2. Stateless: each request complete
    3. Uniform Interface: standard methods
    4. Layered: proxies, gateways allowed
    5. Cacheable: responses mark cacheability

  • Quick poll: "Who has worked with SOAP?"
    - Use for comparison throughout
-->

<!-- end_slide -->

# REST vs Traditional Web Services

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### SOAP/XML Web Services

- Protocol-based (SOAP)
- WSDL for description
- XML only
- Complex standards
- Heavy tooling

<!-- column: 1 -->

### RESTful Services

- Architecture style
- Self-descriptive
- Multiple formats (JSON, XML)
- Simple HTTP
- Lightweight

<!-- reset_layout -->

<!--
speaker_note: |
  REST vs SOAP COMPARISON (4 minutes)

  • If participants know SOAP:
    - SOAP = heavyweight protocol
    - REST = lightweight style
    - WSDL = Web Services Description Language
    - WSDL vs self-descriptive

  • Key advantages of REST:
    - Test with curl/browser
    - No code generation needed
    - Human readable
    - Firewall friendly (just HTTP)

  • Format war is over:
    - JSON won (lighter, JavaScript native)
    - XML still used in enterprise
    - We'll focus on JSON
-->

<!-- end_slide -->

# SOAP vs REST: Real Example

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### SOAP (+ WSDL Definition)

```xml
<!-- WSDL Fragment (50+ lines) -->
<wsdl:definitions>
  <wsdl:types>
    <xsd:schema>
      <xsd:element name="GetUserDetails">
        <xsd:complexType>
          <xsd:sequence>
            <xsd:element name="UserId"
                        type="xsd:int"/>
          </xsd:sequence>
        </xsd:complexType>
      </xsd:element>
    </xsd:schema>
  </wsdl:types>
  <wsdl:message name="GetUserRequest">
    <wsdl:part element="GetUserDetails"/>
  </wsdl:message>
  <!-- ... 30+ more lines ... -->
</wsdl:definitions>
```

### Plus SOAP Request:

```xml
POST /UserService HTTP/1.1
Content-Type: application/soap+xml

<soap:Envelope xmlns:soap="...">
  <soap:Body>
    <GetUserDetails>
      <UserId>123</UserId>
    </GetUserDetails>
  </soap:Body>
</soap:Envelope>
```

<!-- column: 1 -->

<!-- pause -->

### REST

```http
GET /users/123 HTTP/1.1
Accept: application/json
```

**That's it!** ✅

<!-- pause -->

### Benefits:

- No WSDL needed
- No code generation
- No XML parsing
- No envelope overhead
- Self-descriptive
- Testable with curl

<!-- reset_layout -->

<!-- pause -->

**Result:** Same functionality, 95% less complexity!

<!--
speaker_note: |
  SOAP VS REST EXAMPLE (4 minutes)

  • Left side - SOAP nightmare:
    - WSDL fragment shown is just 20 lines
    - Real WSDL would be 100-200 lines!
    - Must define EVERY operation
    - Complex type definitions
    - Required for code generation
    - PLUS the actual SOAP request

  • Right side - REST simplicity:
    - 2 lines. That's it.
    - HTTP method tells the action
    - URL tells the resource
    - No contracts to maintain

  • Walk through benefits (click to reveal):
    - No WSDL = no contract hell
    - No code gen = direct HTTP calls
    - No XML parsing = faster
    - JSON smaller than XML
    - No envelope = less bandwidth
    - Self-descriptive = intuitive
    - curl testable = quick debugging

  • Powerful point: "This is why REST won"
    - Not because it's trendy
    - Because it's SIMPLE
    - Complexity kills projects
-->

<!-- end_slide -->

# HTTP Methods in REST

<!-- pause -->

```http +line_numbers
GET     - Retrieve resources
POST    - Create new resources
PUT     - Update/Replace resources
PATCH   - Partial update
DELETE  - Remove resources
```

<!-- pause -->

### Important Properties:

- **Safe Methods**: GET, HEAD, OPTIONS
- **Idempotent**: GET, PUT, DELETE

<!--
speaker_note: |
  HTTP METHODS (5 minutes)

  • Method meanings:
    - GET: retrieve (like SELECT)
    - POST: create new (like INSERT)
    - PUT: full replace (like UPDATE)
    - PATCH: partial update (newer)
    - DELETE: remove (like DELETE)

  • Also mention (not shown on slide):
    - OPTIONS: returns allowed methods for resource
      Example: "What can I do with /users/123?"
      Returns: Allow: GET, PUT, DELETE, OPTIONS
    - HEAD: like GET but only returns headers
      Use case: Check if resource exists or modified
      No body returned, saves bandwidth

  • Critical concepts:
    - SAFE: doesn't modify data (GET, HEAD, OPTIONS)
    - IDEMPOTENT: repeated calls = same result

  • Idempotency examples:
    - GET: always idempotent ✓
    - PUT: idempotent (replaces) ✓
    - DELETE: idempotent (already gone) ✓
    - POST: NOT idempotent (creates new) ✗
    - HEAD/OPTIONS: idempotent ✓

  • Common mistake: Using POST for everything
-->

<!-- end_slide -->

# REST Resources

## Everything is a **Resource**

- Identified by URIs
- Multiple representations
- Self-descriptive messages

<!-- pause -->

### Examples:

```http
/api/users           # User collection
/api/users/123       # Specific user
/api/users/123/orders # User's orders
```

<!--
speaker_note: |
  REST RESOURCES (4 minutes)

  • Core concept: Everything is a resource
    - Like database tables/entities
    - Identified by URIs (URLs)

  • Think NOUNS not VERBS:
    - ✓ /users (noun)
    - ✗ /getUsers (verb)

  • URI design principles:
    - Hierarchical: /users/123/orders
    - Predictable: developers can guess
    - Plural: /users not /user

  • Multiple representations:
    - Same resource, different formats
    - Content negotiation decides
-->

<!-- end_slide -->

# HTTP Status Codes

<!-- column_layout: [1, 1, 1] -->

<!-- column: 0 -->

### Success (2xx)

- 200 OK
- 201 Created
- 204 No Content

<!-- column: 1 -->

### Client Errors (4xx)

- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

<!-- column: 2 -->

### Server Errors (5xx)

- 500 Internal Server Error
- 502 Bad Gateway
- 503 Service Unavailable

<!-- reset_layout -->

<!--
speaker_note: |
  HTTP STATUS CODES (5 minutes)

  • Success (2xx):
    - 200 OK: general success
    - 201 Created: new resource (needs Location header!)
    - 204 No Content: success, nothing to return (DELETE)

  • Client errors (4xx):
    - 400 Bad Request: malformed request
    - 401 Unauthorized: who are you?
    - 403 Forbidden: I know you, but no access
    - 404 Not Found: resource doesn't exist

  • Server errors (5xx):
    - 500 Internal Error: our fault
    - 503 Unavailable: temporary issue

  • Security tip: NEVER expose stack traces in 500s
  • Common mistake: Always returning 200
-->

<!-- end_slide -->

# Richardson Maturity Model

## Level 0: The Swamp of POX

- Single URI, single HTTP method
- RPC style over HTTP

```http
POST /api/endpoint
{
  "method": "getUser",
  "id": 123
}
```

<!--
speaker_note: |
  RICHARDSON MODEL - LEVEL 0 (3 minutes)

  • Level 0 = "Swamp of POX"
    - POX = Plain Old XML/JSON
    - Just RPC over HTTP
    - Single endpoint, single method

  • Example shown:
    - Everything goes to /api/endpoint
    - Method in payload ("getUser")
    - This is NOT REST!

  • Common in:
    - Legacy enterprise systems
    - SOAP-to-REST migrations
    - Quick and dirty APIs

  https://martinfowler.com/articles/richardsonMaturityModel.htmlhttps://martinfowler.com/articles/richardsonMaturityModel.html
-->

<!-- end_slide -->

# Richardson Maturity Model (cont.)

## Level 1: Resources

- Multiple URIs for different resources
- Still using single HTTP method

```http
POST /api/users/123
POST /api/orders/456
```

<!-- pause -->

## Level 2: HTTP Verbs

- Correct use of HTTP methods
- Proper status codes

```http
GET /api/users/123
DELETE /api/orders/456
```

<!--
speaker_note: |
  RICHARDSON MODEL (2 minutes)

  • Level 1: Resources
    - Multiple URIs ✓
    - Still using POST for everything ✗
    - Better than Level 0

  • Level 2: HTTP Verbs
    - Correct methods (GET, POST, PUT, DELETE) ✓
    - Proper status codes ✓
    - THIS IS OUR TARGET!

  • Industry reality:
    - 90% of APIs stop at Level 2
    - Good enough for most use cases
    - Level 3 adds complexity

  • We'll focus on solid Level 2
-->

<!-- end_slide -->

# Richardson Maturity Model - Level 3

## Level 3: Hypermedia Controls (HATEOAS)

```json {1-8|9-16|all} +line_numbers
{
  "id": 123,
  "name": "John Doe",
  "email": "john@example.com",
  "_links": {
    "self": "/api/users/123",
    "orders": "/api/users/123/orders",
    "update": {
      "href": "/api/users/123",
      "method": "PUT"
    },
    "delete": {
      "href": "/api/users/123",
      "method": "DELETE"
    }
  }
}
```

<!--
speaker_note: |
  RICHARDSON LEVEL 3 - HATEOAS (3 minutes)

  • HATEOAS = Hypermedia as Engine of Application State
    - Long acronym, simple idea
    - Response includes next actions
    - Like web pages with links

  • Example shows:
    - _links section with actions
    - Client discovers what's possible
    - No hardcoded URLs in client

  • Reality check:
    - <10% of APIs implement this
    - Adds complexity
    - Clients often don't use it

  • We'll mention but not implement
-->

<!-- end_slide -->

# REST Best Practices

## 1. **Use Nouns, Not Verbs**

✅ `/users`  
❌ `/getUsers`

<!-- pause -->

## 2. **Use Plural Nouns**

✅ `/books`  
❌ `/book`

<!-- pause -->

## 3. **Hierarchical Resources**

✅ `/users/123/orders/456`

<!--
speaker_note: |
  REST BEST PRACTICES (4 minutes)

  • Practice 1: Nouns not verbs
    - Resources are things, not actions
    - Let HTTP method be the verb

  • Practice 2: Plural nouns
    - Controversial but common
    - /users not /user
    - Consistency > perfection

  • Practice 3: Hierarchical URIs
    - Show relationships in path
    - /users/123/orders/456
    - Intuitive navigation

  • Remember: Guidelines not laws!
    - Team consistency matters most
    - Document your choices
-->

<!-- end_slide -->

# Content Negotiation

## Client Request:

```http +line_numbers
GET /api/users/123 HTTP/1.1
Host: api.example.com
Accept: application/json
```

<!-- pause -->

## Server Response:

```http +line_numbers
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 123,
  "name": "John Doe"
}
```

<!--
speaker_note: |
  CONTENT NEGOTIATION (4 minutes)

  • How client and server agree on format:

  • Request headers:
    - Accept: what client wants
    - "application/json" or "application/xml"

  • Response headers:
    - Content-Type: what server sends
    - Must match or be compatible

  • Advanced:
    - Quality values: Accept: application/json;q=0.9
    - Multiple formats per endpoint
    - Server picks best match

  • We'll mostly use JSON
-->

<!-- end_slide -->

# Real-World REST APIs

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### GitHub API

```http
GET /repos/{owner}/{repo}
GET /users/{username}
POST /user/repos
DELETE /repos/{owner}/{repo}
```

<!-- column: 1 -->

### Twitter API

```http
GET /statuses/show/{id}
POST /statuses/update
DELETE /statuses/destroy/{id}
GET /users/show
```

<!-- reset_layout -->

<!--
speaker_note: |
  REAL-WORLD EXAMPLES (3 minutes)

  • GitHub API:
    - Level 3 (has hypermedia)
    - Excellent documentation
    - Good example to study

  • Twitter API:
    - Level 2 (our target)
    - Simpler, still successful
    - More typical approach

  • Key observations:
    - Both use plural nouns
    - Clear hierarchies
    - Consistent patterns

  • Lesson: Pragmatism > Purity
    - Don't over-engineer
    - Level 2 is production-ready
-->

<!-- end_slide -->

# Exercise 1: Analyze REST APIs

## 🏃 **Task**: Analyze a public REST API

1. Choose: GitHub, Twitter, or Spotify
2. Identify REST principles used
3. Note Richardson Maturity Level
4. Find any anti-patterns

**Time**: 15 minutes

<!--
speaker_note: |
  EXERCISE 1 - API ANALYSIS (15 minutes)

  • Setup (2 min):
    - Groups of 2-3 people
    - Pick one API to analyze
    - Open documentation first

  • APIs to explore:
    - GitHub: api.github.com (no auth needed)
    - Twitter: developer.twitter.com (need account)
    - Spotify: developer.spotify.com (need account)

  • What to look for:
    - REST principles applied
    - Richardson level (probably 2)
    - Any anti-patterns?
    - Good patterns to copy?

  • Teacher: Walk around, help stuck groups
  • Debrief: 5 min discussion after
-->

<!-- end_slide -->

# Exercise: Spot the Anti-Pattern! 🔍

## Example 1: What's wrong here?

```http
POST /api/getUser
Content-Type: application/json

{
  "userId": 123
}
```

<!-- pause -->

**Problems:**

- ❌ Verb in URL (`getUser`)
- ❌ Using POST for retrieval
- ❌ RPC-style thinking

**Solution:** ✅ `GET /api/users/123`

<!--
speaker_note: |
  ANTI-PATTERN EXERCISE 1 (2 minutes)

  • Ask audience: "What's wrong with this request?"
  • Wait for responses (30 seconds)
  • Common answers they should give:
    - "getUser is a verb in the URL"
    - "Why POST for getting data?"
    - "Looks like SOAP/RPC"

  • Reveal problems one by one
  • Show the correct REST approach
  • Emphasize: Let HTTP method be the verb!
-->

<!-- end_slide -->

# Exercise: Spot the Anti-Pattern! 🔍

## Example 2: What's wrong here?

```http
POST /api/users/delete/123
POST /api/users/update/123
POST /api/users/create
```

<!-- pause -->

**Problems:**

- ❌ Actions in URLs (`delete`, `update`, `create`)
- ❌ Using POST for everything
- ❌ Not leveraging HTTP methods

**Solution:**

- ✅ `DELETE /api/users/123`
- ✅ `PUT /api/users/123`
- ✅ `POST /api/users`

<!--
speaker_note: |
  ANTI-PATTERN EXERCISE 2 (2 minutes)

  • Ask: "What's the pattern you see here?"
  • Wait for responses (30 seconds)
  • They should notice:
    - "Everything is POST!"
    - "Actions in the URLs"
    - "Not using DELETE/PUT"

  • Key point: HTTP methods ARE your verbs
  • POST-only APIs lose semantics
  • Can't use HTTP caching properly
-->

<!-- end_slide -->

# Exercise: Spot the Anti-Pattern! 🔍

## Example 3: What's wrong here?

```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": false,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "User with ID 999 not found"
  }
}
```

<!-- pause -->

**Problems:**

- ❌ Returns 200 OK for an error
- ❌ Error info buried in response body
- ❌ Breaks HTTP semantics

**Solution:**

```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "error": "User with ID 999 not found"
}
```

<!--
speaker_note: |
  ANTI-PATTERN EXERCISE 3 (2 minutes)

  • Ask: "The request failed but something's off..."
  • Wait for responses (30 seconds)
  • They should spot:
    - "200 OK but it's an error!"
    - "Should be 404"
    - "Proxies/tools won't know it failed"

  • Explain impact:
    - Monitoring tools think all is well
    - Caches might store error as success
    - Client libraries can't handle properly

  • Rule: Use HTTP status codes correctly!
-->

<!-- end_slide -->

# Exercise: Design This Resource! 🏗️

## Scenario: Online Library System

You need endpoints for:

- Getting all books
- Getting a specific book
- Getting books by an author
- Searching/filtering books
- Borrowing a book
- Returning a book

**Take 2 minutes - design the URLs and methods!**

<!-- pause -->

## Suggested Solution:

```http
GET    /api/books              # All books
GET    /api/books/123          # Specific book
GET    /api/authors/456/books  # Books by author
POST   /api/books/123/borrow   # Borrow (creates loan)
DELETE /api/books/123/borrow   # Return (ends loan)
```

**Query String Examples:**

```http
GET /api/books?genre=fiction&year=2024
GET /api/books?search=java&limit=10&offset=20
GET /api/books?available=true&sort=title
GET /api/books?author=King&genre=horror
```

Alternative for borrow/return:

```http
POST   /api/loans              # Create loan
DELETE /api/loans/789          # End loan
```

<!--
speaker_note: |
  DESIGN EXERCISE (5 minutes)

  • Give them 2 minutes to think/discuss
  • Have 2-3 people share their designs
  • Common mistakes to address:
    - /api/borrowBook/123 (verb in URL)
    - /api/getBooksByAuthor (verb again)
    - Using POST for return

  • Discuss alternatives:
    - Borrow/return as actions vs. loans as resources
    - Both are valid REST approaches!
    - Loans as resources is more "pure" REST

  • Key lesson: Multiple correct solutions exist
  • Focus on consistency and clarity
-->

<!-- end_slide -->

# REST Security Considerations

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### Authentication Methods

- Basic Authentication
- API Keys
- OAuth 2.0
- **JWT Tokens** ← Our focus

<!-- column: 1 -->

### Best Practices

- Always use HTTPS
- Validate all inputs
- Rate limiting
- Audit logging
- CORS configuration

<!-- reset_layout -->

<!--
speaker_note: |
  SECURITY OVERVIEW (5 minutes)

  • Authentication methods:
    - Basic Auth: simple, credentials every request
    - API Keys: good for service-to-service
    - OAuth 2.0: complex, for user delegation
    - JWT: our focus tomorrow (stateless tokens)

  • Security best practices:
    - HTTPS always (not optional!)
    - Validate ALL inputs
    - Rate limiting (prevent abuse)
    - Audit logging (who did what)
    - CORS config (browser security)

  • Golden rule: NEVER trust client input
    - Validate everything
    - Sanitize data
    - Check permissions

  • Tomorrow: Full JWT implementation
-->

<!-- end_slide -->

# REST vs GraphQL

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### REST

- Multiple endpoints
- Fixed data structure
- Simple caching
- Mature ecosystem

<!-- column: 1 -->

### GraphQL

- Single endpoint
- Flexible queries
- Complex caching
- Growing adoption

<!-- reset_layout -->

**For this training: Focus on REST with Jersey**

<!--
speaker_note: |
  [OPTIONAL - SKIP IF SHORT ON TIME]

  REST VS GRAPHQL (2 minutes MAX)

  • Quick comparison only if asked
  • Key point: REST still dominates enterprise
  • We focus on REST with Jersey
  • GraphQL = different training entirely
-->

<!-- end_slide -->

# Summary

## Key Takeaways

✓ REST is an architectural style  
✓ Resources identified by URIs  
✓ HTTP methods define operations  
✓ Stateless communication  
✓ Multiple representation formats

<!-- pause -->

## Next: Module 2 - Resource Design

How to model your API resources effectively

<!--
speaker_note: |
  MODULE 1 SUMMARY (5 minutes)

  • Key takeaways:
    - REST = architectural style, not protocol
    - Resources + HTTP methods + status codes
    - Stateless by design
    - Multiple formats (we use JSON)

  • What we learned:
    - 5 REST constraints
    - Richardson Level 2 is our target
    - Best practices (nouns, plural, hierarchy)
    - Common anti-patterns to avoid

  • Coming next:
    - Module 2: Resource Design
    - How to model your domain
    - E-commerce example

  • Quick break: 5 minutes
  • Questions? Note them for later
-->

<!-- end_slide -->

# Questions?

## Ready for Module 2: Resource Design

<!--
speaker_note: |
  Q&A SESSION (5-10 minutes flexible)

  • Common questions to expect:
    - \"Why not Spring Boot?\" → Lighter, less magic
    - \"PATCH vs PUT?\" → PATCH partial, PUT full
    - \"Version in URL or header?\" → Module 5
    - \"How to handle auth?\" → Tomorrow

  • If no questions:
    - \"Think about your current APIs\"
    - \"What would you redesign?\"
    - \"Any anti-patterns you recognize?\"

  • Wrap up:
    - \"Implementation starts after lunch\"
    - \"Module 2 next: Resource Design\"
    - \"5 minute break\"
-->
