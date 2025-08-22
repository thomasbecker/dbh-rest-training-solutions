---
title: Idempotency & REST Alternatives
sub_title: Safe Operations and API Paradigms
author: Thomas Becker
date: August 2025
---

# Idempotency & REST Alternatives

Safe Operations and API Paradigms

Duration: 45 minutes

<!-- end_slide -->

## Agenda

1. Safe vs Idempotent Operations
1. Idempotency in Practice
1. REST Alternatives Overview
1. GraphQL Deep Dive
1. gRPC Overview
1. SOAP Legacy Systems
1. When to Use Which

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

<!-- speaker_note: Safe operations are always idempotent, but not vice versa -->

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

<!-- speaker_note: PATCH with JSON Patch operations may not be idempotent -->

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

<!-- speaker_note: Financial systems often require idempotency keys -->

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

<!-- speaker_note: No one-size-fits-all solution -->

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

<!-- speaker_note: GraphQL shines with complex, nested data -->

<!-- end_slide -->

## GraphQL Considerations

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

<!-- speaker_note: Binary format is much more efficient than JSON -->

<!-- end_slide -->

## gRPC Considerations

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

<!-- speaker_note: Verbose but comprehensive -->

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

<!-- speaker_note: Consider your specific requirements -->

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

<!-- speaker_note: Never do big-bang migrations -->

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

<!-- speaker_note: Pragmatic architecture uses multiple paradigms -->

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

<!-- speaker_note: 
- Ask about their current systems
- What paradigms they've encountered
- Pain points with current APIs
-->

<!-- end_slide -->