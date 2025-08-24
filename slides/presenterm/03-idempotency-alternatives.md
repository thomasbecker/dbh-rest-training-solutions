---
title: Idempotency & REST Alternatives
sub_title: Safe Operations and API Paradigms
author: Thomas Becker
date: August 2025
---

# Idempotency & REST Alternatives

Safe Operations and API Paradigms

Duration: 45 minutes → **30 min optimal**

⏱️ **Checkpoint: Should be ~11:00 AM**

<!--
speaker_note: |
  MODULE 3 START (1 minute)

  • Time check: Should be ~11:00 AM
  • Energy level check
  • This is the last module before lunch
  • More theoretical but critical concepts
-->

<!-- end_slide -->

## Agenda

1. Safe vs Idempotent Operations [CORE - 15 min]
1. Idempotency in Practice [CORE - 10 min]
1. REST Alternatives Overview [OPTIONAL - 10 min]
1. When to Use Which [OPTIONAL - 5 min]

⏱️ **Focus on idempotency, skip alternatives if behind**

<!--
speaker_note: |
  [TIMING: 1 minute]
  [PRIORITY: CORE]
  
  • Focus on IDEMPOTENCY (critical)
  • Alternatives are OPTIONAL
  • If behind: Skip to "When to Use Which"
    - 45 minutes total
    - Theory + practical examples
    - Decision framework
    - Real-world scenarios

  • Energy check:
    - "Everyone still with me?"
    - "Questions from resource design?"
-->

<!-- end_slide -->

## Safe vs Idempotent

Two critical concepts in API design

<!-- pause -->

### Safe Operations

Operations that **do not modify** server state

<!-- pause -->

- **GET** - Retrieve data
- **HEAD** - Like GET but headers only
- **OPTIONS** - Check allowed methods

<!-- pause -->

### Idempotent Operations

Operations that produce the **same result** when called multiple times

<!-- pause -->

- **GET, HEAD, OPTIONS** - Also safe
- **PUT** - Replace resource entirely
- **DELETE** - Remove resource

<!-- pause -->

### Not Idempotent

- **POST** - Creates new resources each time

<!--
speaker_note: |
  SAFE VS IDEMPOTENT (5 minutes)

  • Start with confusion clearing:
    - "These terms often confused"
    - "Critical for API reliability"
    - "Prevents duplicate charges/orders"

  • Safe operations:
    - Read-only, no side effects
    - Can be cached aggressively
    - Browser can retry automatically
    - Example: "Refresh a page - no harm"

  • Idempotent operations:
    - Same result if called 1 or N times
    - PUT replaces entire resource
    - DELETE already gone? Still 204/404
    - Critical for network failures

  • Key distinction:
    - "All safe ops are idempotent"
    - "But DELETE is idempotent, not safe"
    - "PUT modifies but is idempotent"

  • Ask audience:
    - "Why is POST not idempotent?"
    - Let them think... "Creates NEW each time"
-->

<!-- end_slide -->

## Idempotency Matrix

| Method | Safe | Idempotent | Example |
|--------|------|------------|---------|
| GET    | ✅   | ✅         | GET /users/123 |
| HEAD   | ✅   | ✅         | HEAD /users/123 |
| OPTIONS| ✅   | ✅         | OPTIONS /users |
| PUT    | ❌   | ✅         | PUT /users/123 |
| DELETE | ❌   | ✅         | DELETE /users/123 |
| POST   | ❌   | ❌         | POST /users |
| PATCH  | ❌   | ❌*        | PATCH /users/123 |

*PATCH can be idempotent depending on implementation

<!--
speaker_note: |
  IDEMPOTENCY MATRIX (3 minutes)

  • Use this table as reference:
    - Keep on screen during discussion
    - Point to each row as you explain

  • PATCH special case:
    - "PATCH /users/123 {\"age\": 30}" - idempotent
    - "PATCH /users/123 {\"age\": +1}" - NOT idempotent
    - JSON Patch ops often not idempotent

  • Real-world impact:
    - "GET can be retried safely"
    - "PUT can be retried safely"
    - "POST needs special handling"

  • Transition:
    - "Let's see why this matters in practice..."
-->

<!-- end_slide -->

## Why Idempotency Matters

Real-world scenarios

<!-- pause -->

### Network Issues

```java
// Client sends request
POST /api/orders

// Network timeout - did it succeed?
// Client retries...
POST /api/orders  // Duplicate order? 😱
```

<!-- pause -->

### Solution: Idempotency Keys

```java
POST /api/orders
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000

// Server checks: Have I seen this key before?
// If yes: return cached response
// If no: process and cache result
```

<!--
speaker_note: |
  WHY IDEMPOTENCY MATTERS (4 minutes)

  • Set the scene:
    - "Customer clicks 'Buy Now'"
    - "Network timeout occurs"
    - "Did order go through?"
    - "Customer clicks again..."

  • Real examples:
    - Stripe uses idempotency keys
    - PayPal requires them
    - AWS APIs use client tokens

  • Implementation pattern:
    - Client generates UUID
    - Sends with request
    - Server checks cache/DB
    - Returns cached if exists
    - Processes if new

  • Best practices:
    - 24-hour cache typical
    - Store in Redis/Memcached
    - Include in API docs
    - Return same status code

  • Ask audience:
    - "Any payment processing experience?"
    - "Ever seen duplicate charges?"
-->

<!-- end_slide -->

## Implementing Idempotency

Jersey implementation example

```java
@POST
@Path("/orders")
public Response createOrder(
    @HeaderParam("Idempotency-Key") String idempotencyKey,
    Order order) {
    
    if (idempotencyKey != null) {
        // Check cache for existing response
        Response cached = cache.get(idempotencyKey);
        if (cached != null) {
            return cached;  // Return same response
        }
    }
    
    // Process order
    Order created = orderService.create(order);
    Response response = Response.status(201)
        .entity(created)
        .build();
    
    // Cache response with key
    if (idempotencyKey != null) {
        cache.put(idempotencyKey, response, 24, TimeUnit.HOURS);
    }
    
    return response;
}
```

<!-- end_slide -->

## REST Alternatives

Different paradigms for different needs

<!-- pause -->

### The Landscape

1. **REST** - Resource-oriented, stateless
1. **GraphQL** - Query language for APIs
1. **gRPC** - High-performance RPC
1. **SOAP** - Enterprise legacy
1. **WebSockets** - Real-time bidirectional
1. **JSON-RPC** - Simple remote procedure calls

<!-- pause -->

### Key Question

**"What problem are you solving?"**

<!--
speaker_note: |
  REST ALTERNATIVES OVERVIEW (3 minutes)

  • Transition from idempotency:
    - "REST handles idempotency well"
    - "But REST isn't always best choice"
    - "Let's explore alternatives"

  • Set expectations:
    - "Not REST vs others"
    - "Right tool for right job"
    - "Many can coexist"

  • Quick overview (30 seconds each):
    - REST: Resources over HTTP
    - GraphQL: Facebook 2015, query language
    - gRPC: Google RPC, binary
    - SOAP: Still alive in enterprise
    - WebSockets: Real-time needs

  • Key message:
    - "What problem are you solving?"
    - Not resume-driven development
    - Consider team skills too

  • Transition:
    - "Let's revisit REST strengths/weaknesses"
-->

<!-- end_slide -->

## REST Overview

What we've been learning

<!-- pause -->

### Strengths ✅

- Simple and intuitive
- HTTP native
- Cacheable
- Stateless
- Wide tooling support

<!-- pause -->

### Weaknesses ❌

- Over/under-fetching
- Multiple round trips
- No real-time updates
- Version management complexity

<!-- pause -->

### Best For

- Public APIs
- CRUD operations
- Microservices
- Mobile apps

<!-- end_slide -->

## GraphQL Deep Dive

Query language for your API

<!-- pause -->

### Core Concept

Client specifies exactly what data they need

```graphql
# Request only what you need
query {
  user(id: 123) {
    name
    email
    posts {
      title
      createdAt
    }
  }
}
```

<!-- pause -->

### Response

```json
{
  "data": {
    "user": {
      "name": "John Doe",
      "email": "john@example.com",
      "posts": [
        {
          "title": "My First Post",
          "createdAt": "2024-01-15"
        }
      ]
    }
  }
}
```

<!-- end_slide -->

## GraphQL vs REST

Side-by-side comparison

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### REST Approach

```
GET /users/123
GET /users/123/posts
GET /posts/456/comments
GET /posts/789/comments
```

4 requests, over-fetching

<!-- column: 1 -->

### GraphQL Approach

```graphql
query {
  user(id: 123) {
    name
    posts {
      title
      comments {
        text
      }
    }
  }
}
```

1 request, exact data

<!-- reset_layout -->

<!--
speaker_note: |
  GRAPHQL VS REST COMPARISON (4 minutes)

  • Set up the comparison:
    - "Mobile app scenario"
    - "User profile with posts and comments"
    - "Limited bandwidth"

  • REST problems (left side):
    - 4 separate HTTP calls
    - Over-fetching (all fields)
    - Under-fetching (need more calls)
    - Waterfall loading pattern

  • GraphQL solution (right side):
    - Single request
    - Exact fields needed
    - Nested data in one go
    - Better for mobile

  • Real examples:
    - GitHub moved v4 to GraphQL
    - Facebook mobile app
    - Shopify storefront API

  • When NOT to use GraphQL:
    - Simple CRUD (overkill)
    - File uploads (complex)
    - Caching (harder than REST)

  • Ask audience:
    - "Anyone tried GraphQL?"
    - "What was your experience?"
-->

<!-- end_slide -->

## GraphQL Considerations

<!--
speaker_note: |
  GRAPHQL TRADE-OFFS (3 minutes)
  
  • Strengths: flexibility, efficiency
  • Weaknesses: complexity, caching
  • When it shines: mobile, multiple clients
  • Real examples: GitHub, Shopify
  
  • N+1 problem explanation:
    - Query asks for users + their posts
    - 1 query for all users
    - N queries for each user's posts
    - Solution: DataLoader pattern (batching)
    - "This is THE GraphQL gotcha"
-->

<!-- pause -->

### Strengths ✅

- No over/under-fetching
- Single request for complex data
- Strong typing
- Self-documenting
- Real-time subscriptions

<!-- pause -->

### Weaknesses ❌

- Complex caching
- Learning curve
- N+1 query problems
- Security complexity (query depth)
- File uploads are tricky

<!-- pause -->

### Best For

- Complex data relationships
- Mobile apps (bandwidth)
- Rapid frontend iteration
- Multiple client types

<!-- end_slide -->

## gRPC Overview

Google's Remote Procedure Call framework

<!-- pause -->

### Key Features

- **Protocol Buffers** - Binary serialization
- **HTTP/2** - Multiplexing, streaming
- **Code Generation** - Type-safe clients
- **Bidirectional Streaming** - Real-time communication

<!-- pause -->

### Proto Definition

```protobuf
service UserService {
  rpc GetUser(GetUserRequest) returns (User);
  rpc ListUsers(ListUsersRequest) returns (stream User);
  rpc CreateUser(User) returns (User);
}

message User {
  int32 id = 1;
  string name = 2;
  string email = 3;
}
```

<!--
speaker_note: |
  GRPC OVERVIEW (4 minutes)

  • Context setting:
    - "Google internal RPC system"
    - "Open sourced 2015"
    - "Built for microservices"

  • Protocol Buffers demo:
    - Binary vs JSON size (3-10x smaller)
    - Strongly typed
    - Code generation benefit
    - Version compatibility built-in

  • Performance numbers:
    - "10x faster than REST/JSON"
    - "Streaming built-in"
    - "HTTP/2 multiplexing"

  • Real use cases:
    - Netflix backend services
    - Uber microservices
    - Google Cloud APIs

  • Limitations to mention:
    - "No browser support directly"
    - "Need proxy for web"
    - "Binary = not human readable"
    - "Debugging harder"

  • Transition:
    - "Now let's look at enterprise legacy..."
-->

<!-- end_slide -->

## gRPC Considerations

<!--
speaker_note: |
  GRPC TRADE-OFFS (2 minutes)
  
  • Quick comparison
  • When to use gRPC
  • When to avoid
  • Real-world fit
-->

<!-- pause -->

### Strengths ✅

- High performance (binary)
- Streaming support
- Strong typing
- Code generation
- Language agnostic

<!-- pause -->

### Weaknesses ❌

- Not browser-friendly
- Binary format (not human-readable)
- Limited tooling
- Learning curve
- Requires HTTP/2

<!-- pause -->

### Best For

- Microservice communication
- High-performance requirements
- Streaming data
- Polyglot environments

<!-- end_slide -->

## SOAP Legacy

Still alive in enterprise

<!-- pause -->

### What is SOAP?

- **Simple Object Access Protocol**
- XML-based messaging
- Protocol independent
- WS-* standards

<!-- pause -->

### Example

```xml
<soap:Envelope>
  <soap:Header>
    <auth:Authentication>
      <auth:Username>user</auth:Username>
      <auth:Password>pass</auth:Password>
    </auth:Authentication>
  </soap:Header>
  <soap:Body>
    <GetUserRequest>
      <UserId>123</UserId>
    </GetUserRequest>
  </soap:Body>
</soap:Envelope>
```

<!--
speaker_note: |
  SOAP LEGACY (3 minutes)

  • Set the tone:
    - "SOAP seems outdated but..."
    - "Still dominates banking/insurance"
    - "Government loves it"
    - "Not going away soon"

  • Why it persists:
    - WS-Security standards
    - Transaction support
    - Formal contracts (WSDL)
    - Tool support in enterprise

  • The XML example:
    - "Yes, it's verbose"
    - "But self-documenting"
    - "Security built into protocol"
    - "Not just HTTP - works over JMS, SMTP"

  • Migration reality:
    - "Banks won't migrate soon"
    - "Regulatory compliance"
    - "If it works, don't touch"

  • Transition:
    - "So how do we choose?"
-->

<!-- end_slide -->

## SOAP Considerations

<!-- pause -->

### Strengths ✅

- Enterprise features (WS-Security, WS-Transaction)
- ACID compliance
- Formal contracts (WSDL)
- Language neutral
- Built-in error handling

<!-- pause -->

### Weaknesses ❌

- Verbose XML
- Complex standards
- Poor performance
- Limited browser support
- Steep learning curve

<!-- pause -->

### Best For

- Enterprise integrations
- Financial systems
- Legacy systems
- High security requirements

<!-- end_slide -->

## Decision Matrix

When to use which?

| Scenario | Best Choice | Why |
|----------|------------|-----|
| Public API | REST | Simple, well-understood |
| Mobile App | GraphQL | Bandwidth efficiency |
| Microservices | gRPC | Performance, streaming |
| Enterprise Integration | SOAP | Standards, security |
| Real-time Updates | WebSockets | Bidirectional |
| Browser SPA | REST/GraphQL | Native support |
| High Performance | gRPC | Binary protocol |

<!--
speaker_note: |
  DECISION MATRIX (3 minutes)

  • How to use this table:
    - "Not absolute rules"
    - "Starting points for decisions"
    - "Consider multiple factors"

  • Walk through examples:
    - Public API: REST wins (tooling, understanding)
    - Mobile: GraphQL (bandwidth matters)
    - Internal services: gRPC (performance)
    - Banking: SOAP (compliance, legacy)

  • Additional factors:
    - Team expertise
    - Existing infrastructure
    - Client requirements
    - Time to market

  • Hybrid reality:
    - "Most companies use 2-3"
    - "REST for public"
    - "gRPC for internal"
    - "GraphQL for mobile"

  • Ask audience:
    - "What's your scenario?"
    - Let's categorize together
-->

<!-- end_slide -->

## Migration Considerations

Moving between paradigms

<!-- pause -->

### REST → GraphQL

```java
// Can run both simultaneously
@Path("/api/users")  // REST endpoint
public class UserResource { }

// GraphQL endpoint at /graphql
@GraphQLApi
public class UserResolver { }
```

<!-- pause -->

### Gradual Migration Strategy

1. Run both APIs in parallel
1. Migrate one client at a time
1. Monitor usage patterns
1. Deprecate old endpoints
1. Remove legacy code

<!--
speaker_note: |
  MIGRATION STRATEGIES (3 minutes)

  • Key message:
    - "Never do big-bang migrations"
    - "Always run parallel first"
    - "Gradual client migration"

  • REST to GraphQL example:
    - Start with read-only GraphQL
    - Keep REST for writes
    - Migrate one client at a time
    - Monitor performance/errors
    - Deprecate REST endpoints slowly

  • Success stories:
    - GitHub: 3 years REST + GraphQL
    - Shopify: Still has both
    - Netflix: gRPC internal, REST external

  • Common mistakes:
    - Forcing all clients to migrate
    - Removing old API too soon
    - Not monitoring usage

  • Transition:
    - "Let's see who uses what in production"
-->

<!-- end_slide -->

## Real-World Examples

Who uses what?

<!-- pause -->

### REST
- Twitter API
- Stripe API
- AWS (mostly)

<!-- pause -->

### GraphQL
- GitHub API v4
- Facebook
- Shopify

<!-- pause -->

### gRPC
- Netflix (internal)
- Google (internal)
- Uber

<!-- pause -->

### SOAP
- Banks
- Government
- Healthcare

<!-- end_slide -->

## Hybrid Approaches

Best of multiple worlds

<!-- pause -->

### REST + GraphQL

```
/api/v1/users     # REST for simple CRUD
/graphql          # GraphQL for complex queries
```

<!-- pause -->

### REST + WebSockets

```
GET /api/messages      # Fetch initial
WS /api/messages/live  # Real-time updates
```

<!-- pause -->

### gRPC + REST Gateway

```
Internal: gRPC for microservices
External: REST gateway for public API
```

<!--
speaker_note: |
  HYBRID APPROACHES (2 minutes)

  • Real-world reality:
    - "Pure architectures rare"
    - "Use best tool for each job"
    - "Complexity vs pragmatism"

  • Common patterns:
    - REST + GraphQL (GitHub)
    - REST + WebSockets (Slack)
    - gRPC + REST Gateway (Google)

  • Implementation tips:
    - Separate endpoints
    - Shared business logic
    - Common auth layer
    - Unified monitoring

  • Cost considerations:
    - More complexity
    - Multiple skill sets
    - More infrastructure
    - But better fit for purpose

  • Transition:
    - "Quick decision guide coming up"
-->

<!-- end_slide -->

## 💡 Quick Decision Guide

<!-- pause -->

**Need simple CRUD?** → REST

<!-- pause -->

**Complex nested data?** → GraphQL

<!-- pause -->

**Microservice communication?** → gRPC

<!-- pause -->

**Real-time updates?** → WebSockets

<!-- pause -->

**Enterprise integration?** → SOAP

<!-- pause -->

**Banking/Financial?** → SOAP + REST

<!-- pause -->

**Starting fresh?** → REST, then evolve

<!-- end_slide -->

## Practical Exercise

Analyze these scenarios

<!--
speaker_note: |
  PRACTICAL EXERCISE (3 minutes)

  • Interactive section:
    - "Let's apply what we learned"
    - Show scenario, pause for thinking
    - Get audience answers first
    - Then reveal and explain

  • Scenario 1 discussion:
    - E-commerce = different client needs
    - Mobile wants minimal data
    - Web wants rich data
    - GraphQL better for this

  • Scenario 2 discussion:
    - Microsecond latency = gRPC
    - Binary protocol faster
    - Streaming capabilities
    - Used by HFT systems

  • Scenario 3 discussion:
    - Legacy = SOAP likely
    - Don't force modernization
    - Integration over migration

  • If time permits:
    - Ask for their scenarios
    - Analyze together
-->

<!-- pause -->

### Scenario 1
"E-commerce platform needs product catalog API for mobile and web"

<!-- pause -->
**Answer**: REST or GraphQL (different data needs per client)

<!-- pause -->

### Scenario 2
"Real-time stock trading system with microsecond latency"

<!-- pause -->
**Answer**: gRPC (performance critical)

<!-- pause -->

### Scenario 3
"Integration with 10-year-old banking system"

<!-- pause -->
**Answer**: SOAP (legacy compatibility)

<!-- end_slide -->

## Key Takeaways

<!-- pause -->

✅ Safe operations don't modify state, idempotent operations produce same result

<!-- pause -->

✅ Idempotency prevents duplicate operations in distributed systems

<!-- pause -->

✅ REST is simple but has limitations

<!-- pause -->

✅ GraphQL solves over/under-fetching

<!-- pause -->

✅ gRPC excels at performance and streaming

<!-- pause -->

✅ SOAP lives on in enterprise

<!-- pause -->

✅ Choose based on your specific needs, not trends

<!-- end_slide -->

## Questions?

Let's discuss your API challenges

<!--
speaker_note: |
  QUESTIONS & DISCUSSION (5 minutes)

  • Wrap up key points:
    - Idempotency critical for reliability
    - Multiple paradigms available
    - Choose based on requirements
    - Hybrid approaches common

  • Questions to prompt discussion:
    - "What APIs are you building?"
    - "Any GraphQL experience?"
    - "SOAP horror stories?"
    - "Performance requirements?"

  • Common questions to expect:
    - "Can we use GraphQL with Jersey?"
      Answer: Yes, graphql-java works
    - "Is REST dying?"
      Answer: No, still dominant
    - "Should we migrate from SOAP?"
      Answer: Depends on business value

  • Time check:
    - Should be ~11:45
    - Lunch break at 12:00
    - Afternoon: Jersey hands-on

  • Transition to lunch:
    - "After lunch: Jersey setup"
    - "We'll build real APIs"
    - "Questions over lunch welcome"
-->

<!-- end_slide -->