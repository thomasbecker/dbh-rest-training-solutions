---
title: API Versioning Strategies
author: Thomas Becker
theme:
  name: dark

---

# API Versioning Strategies

Modern approaches to evolving REST APIs

⏱️ **Duration**: 45 minutes → 35 min optimal
📚 **Module 7**: Day 1 Afternoon
⏱️ Start: 15:45 | End: 16:30
⏱️ Checkpoint 15min: Strategy 1 (URI)
⏱️ Checkpoint 30min: Start exercise prep

<!--
speaker_note: |
  INTRODUCTION (2 minutes)
  
  • Context setting:
    - "APIs evolve over time"
    - "Breaking changes are inevitable"
    - "Need strategy to manage evolution"
  
  • Why this matters:
    - Multiple client versions in production
    - Can't force immediate updates
    - Business continuity critical
  
  • What we'll cover:
    - Three main versioning strategies
    - Pros and cons of each
    - Implementation in Jersey
    - Best practices
  
  • Set expectations:
    - "No perfect solution"
    - "Trade-offs everywhere"
    - "Choose based on your context"
-->

<!-- end_slide -->

---

## Why Version APIs?

### The Evolution Problem

<!-- pause -->

Your API **will** change:
- New features added
- Fields renamed or removed  
- Business rules evolve
- Performance improvements

<!-- pause -->

### The Client Problem

<!-- pause -->

You can't control clients:
- Mobile apps take weeks to update
- Enterprise integrations move slowly
- Third-party developers resist changes
- Legacy systems can't change

<!--
speaker_note: |
  THE PROBLEM (3 minutes)
  
  • Evolution examples:
    - "Started with username, now need email"
    - "Date format needs to change"
    - "New required field added"
    - "Resource structure redesigned"
  
  • Client reality:
    - iOS/Android app store delays
    - Enterprise change windows
    - Third-party integration cycles
    - Some clients NEVER update
  
  • Business impact:
    - Can't break existing clients
    - Need to support old versions
    - Migration must be gradual
  
  • Ask the audience:
    - "Who has broken an API?"
    - "Who has been broken by an API?"
    - "How long do you support old versions?"
-->

<!-- end_slide -->

---

## Breaking vs Non-Breaking Changes

### Non-Breaking (Safe) Changes ✅

<!-- pause -->

- Adding new optional fields
- Adding new endpoints
- Adding new optional parameters
- Extending enum values (carefully)

<!-- pause -->

### Breaking (Dangerous) Changes ❌

<!-- pause -->

- Removing fields
- Renaming fields
- Changing field types
- Making optional fields required
- Changing validation rules

<!--
speaker_note: |
  CHANGE TYPES (3 minutes)
  
  • Non-breaking changes:
    - Generally safe to deploy
    - Clients ignore unknown fields
    - Old clients continue working
    - Example: Adding "middleName" field
  
  • Breaking changes:
    - Will crash or confuse clients
    - Require version bump
    - Need migration strategy
    - Example: Renaming "name" to "fullName"
  
  • Gray areas:
    - Changing validation (stricter/looser)
    - Deprecating features
    - Performance characteristics
  
  • Best practice:
    - Assume all changes are breaking
    - Test with actual clients
    - Document everything
-->

<!-- end_slide -->

---

## Three Main Versioning Strategies

### 1. URI Path Versioning
### 2. Header Versioning  
### 3. Media Type Versioning

<!-- pause -->

Each has trade-offs!

<!--
speaker_note: |
  STRATEGY OVERVIEW (2 minutes)
  
  • Three main approaches:
    - URI: Version in the URL
    - Header: Version in HTTP headers
    - Media Type: Version in Accept header
  
  • Also mention:
    - Query parameter (less common)
    - No versioning (risky)
    - Hybrid approaches
  
  • We'll explore:
    - How each works
    - Implementation in Jersey
    - Pros and cons
    - When to use each
  
  • Industry reality:
    - URI most common
    - Header gaining popularity
    - Media type most "RESTful"
-->

<!-- end_slide -->

---

## Strategy 1: URI Path Versioning

### The Most Common Approach

```
/api/v1/users
/api/v2/users
/api/v3/users
```

<!-- pause -->

### Implementation

```java
@Path("/v1/users")
public class UserResourceV1 {
    // Version 1 implementation
}

@Path("/v2/users")
public class UserResourceV2 {
    // Version 2 implementation
}
```

<!--
speaker_note: |
  URI VERSIONING (4 minutes)
  
  • How it works:
    - Version number in URL path
    - Different classes per version
    - Clear and explicit
  
  • Examples:
    - Twitter: /1.1/statuses
    - Stripe: /v1/charges
    - GitHub: /v3/repos
  
  • Implementation:
    - Separate resource classes
    - Can share business logic
    - Clear code organization
  
  • Client perspective:
    - Very obvious
    - Easy to understand
    - Simple to implement
-->

<!-- end_slide -->

---

## URI Versioning: Pros & Cons

### Pros ✅

- **Extremely clear** - version is obvious
- **Easy to route** - load balancers, proxies
- **Simple caching** - different URLs
- **Browser testable** - just change URL

<!-- pause -->

### Cons ❌

- **URL pollution** - not RESTful purist
- **Code duplication** - multiple classes
- **Bookmark breaks** - URLs change
- **Not resource-focused** - version everything

<!--
speaker_note: |
  URI PROS/CONS (3 minutes)
  
  • Advantages deep dive:
    - Developer friendly
    - Operations friendly
    - Cache friendly
    - Debug friendly
  
  • Disadvantages deep dive:
    - Roy Fielding doesn't like it
    - Lots of duplicate code
    - Version entire API, not resources
    - URL aesthetics suffer
  
  • Real world:
    - Most common despite drawbacks
    - Pragmatic choice
    - Tools support it well
  
  • When to use:
    - Public APIs
    - Multiple client types
    - Need simple clarity
-->

<!-- end_slide -->

---

## [OPTIONAL] Strategy 2: Header Versioning

### Version in Custom Header

```http
GET /api/users
Api-Version: 1

GET /api/users  
Api-Version: 2
```

<!-- pause -->

### Implementation

```java
@Path("/users")
public class UserResource {
    @GET
    public Response getUsers(@HeaderParam("Api-Version") String version) {
        if ("2".equals(version)) {
            return getUsersV2();
        }
        return getUsersV1(); // Default to v1
    }
}
```

<!--
speaker_note: |
  HEADER VERSIONING (4 minutes)
  
  • How it works:
    - Custom header carries version
    - Single URL for resource
    - Logic branches on version
  
  • Header options:
    - Api-Version: 2
    - X-API-Version: 2
    - Version: 2
  
  • Implementation patterns:
    - Single resource, branching logic
    - Version injection
    - Strategy pattern
  
  • Clean URLs:
    - /api/users always
    - RESTful purists happier
    - Resources stay same
-->

<!-- end_slide -->

---

## Header Versioning: Jersey Filter

### Cleaner Implementation

```java
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class VersionFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String version = requestContext.getHeaderString("Api-Version");
        if (version == null) {
            version = "1"; // Default version
        }
        requestContext.setProperty("api.version", version);
    }
}
```

<!-- pause -->

```java
@GET
public Response getUsers(@Context ContainerRequestContext ctx) {
    String version = (String) ctx.getProperty("api.version");
    // Version-specific logic
}
```

<!--
speaker_note: |
  FILTER IMPLEMENTATION (3 minutes)
  
  • Filter advantages:
    - Centralized version handling
    - Default version logic
    - Clean resource methods
  
  • Pattern benefits:
    - Separation of concerns
    - Reusable across resources
    - Easy to test
  
  • Advanced patterns:
    - Version validation
    - Version deprecation warnings
    - Metrics per version
  
  • Jersey-specific:
    - ContainerRequestFilter
    - Property injection
    - Context propagation
-->

<!-- end_slide -->

---

## Header Versioning: Pros & Cons

### Pros ✅

- **Clean URLs** - RESTful approach
- **Per-resource versioning** - granular control
- **No URL changes** - bookmarks work
- **Flexible** - easy version negotiation

<!-- pause -->

### Cons ❌

- **Hidden version** - not obvious
- **Harder testing** - need header tools
- **Cache complexity** - same URL, different content
- **Proxy issues** - some strip headers

<!--
speaker_note: |
  HEADER PROS/CONS (3 minutes)
  
  • Advantages deep dive:
    - RESTful purists approve
    - Can version individual resources
    - URLs remain stable
    - Gradual migration possible
  
  • Disadvantages deep dive:
    - Not visible in browser
    - Postman/curl need headers
    - CDN configuration harder
    - Some proxies problematic
  
  • Caching challenges:
    - Must use Vary header
    - Cache key complexity
    - CDN configuration
  
  • When to use:
    - Internal APIs
    - Sophisticated clients
    - Need granular control
-->

<!-- end_slide -->

---

## [OPTIONAL] Strategy 3: Media Type Versioning

### Version in Accept Header

```http
GET /api/users
Accept: application/vnd.company.user-v1+json

GET /api/users
Accept: application/vnd.company.user-v2+json
```

<!-- pause -->

### The "Most RESTful" Approach

Uses standard HTTP content negotiation

<!--
speaker_note: |
  MEDIA TYPE VERSIONING (4 minutes)
  
  • How it works:
    - Custom media types
    - Standard Accept header
    - Content negotiation
  
  • Media type format:
    - application/vnd.{company}.{resource}-v{version}+json
    - vnd = vendor specific
    - Very explicit
  
  • RESTful philosophy:
    - Resources don't change
    - Representations change
    - HTTP as designed
  
  • Examples:
    - GitHub API
    - Some enterprise APIs
-->

<!-- end_slide -->

---

## [OPTIONAL] Media Type: Jersey Implementation

### Using @Produces

```java
@Path("/users")
public class UserResource {
    
    @GET
    @Produces("application/vnd.company.user-v1+json")
    public List<UserV1> getUsersV1() {
        return userService.getUsersV1();
    }
    
    @GET
    @Produces("application/vnd.company.user-v2+json")
    public List<UserV2> getUsersV2() {
        return userService.getUsersV2();
    }
}
```

<!-- pause -->

Jersey automatically routes based on Accept header!

<!--
speaker_note: |
  JERSEY MEDIA TYPE (3 minutes)
  
  • Jersey advantages:
    - Built-in content negotiation
    - Automatic routing
    - Clean separation
  
  • How it works:
    - Matches Accept to @Produces
    - Picks best match
    - 406 if no match
  
  • Quality factors:
    - Accept: type1;q=0.9, type2;q=0.8
    - Priority handling
    - Fallback options
  
  • Clean code:
    - Each method focused
    - Type safety
    - Clear contracts
-->

<!-- end_slide -->

---

## [OPTIONAL] Media Type: Pros & Cons

### Pros ✅

- **True REST** - HTTP as designed
- **Fine-grained** - version per representation
- **Clean URLs** - no version visible
- **Standard mechanism** - content negotiation

<!-- pause -->

### Cons ❌

- **Most complex** - harder to understand
- **Tooling issues** - many tools struggle
- **Verbose headers** - long media types
- **Discovery problem** - what versions exist?

<!--
speaker_note: |
  MEDIA TYPE PROS/CONS (3 minutes)
  
  • Advantages deep dive:
    - Philosophically correct
    - Very flexible
    - Standards compliant
    - Resource focused
  
  • Disadvantages deep dive:
    - Steep learning curve
    - Browser testing hard
    - Some clients can't set Accept
    - Documentation complex
  
  • Real world issues:
    - Developers confused
    - Testing tools limited
    - Debugging harder
  
  • When to use:
    - Sophisticated teams
    - True REST needed
    - Complex versioning needs
-->

<!-- end_slide -->

---

## Comparison Matrix

| Strategy | Clarity | REST | Caching | Testing |
|----------|---------|------|---------|---------|
| **URI Path** | ⭐⭐⭐ | ⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| **Header** | ⭐⭐ | ⭐⭐ | ⭐⭐ | ⭐⭐ |
| **Media Type** | ⭐ | ⭐⭐⭐ | ⭐⭐ | ⭐ |

<!-- pause -->

### Industry Usage

- **URI Path**: ~70% of APIs
- **Header**: ~20% of APIs
- **Media Type**: ~10% of APIs

<!--
speaker_note: |
  COMPARISON (3 minutes)
  
  • Matrix discussion:
    - No clear winner
    - Trade-offs everywhere
    - Context matters
  
  • URI dominance:
    - Simplicity wins
    - Developer experience
    - Tool support
  
  • Trends:
    - Header versioning growing
    - Hybrid approaches emerging
    - GraphQL different model
  
  • Decision factors:
    - Client sophistication
    - Team experience
    - Tool ecosystem
    - Business requirements
-->

<!-- end_slide -->

---

## Best Practices

### 1. Start with Versioning

Don't add it later - build it in from v1

<!-- pause -->

### 2. Version Major Changes Only

Not every change needs a new version

<!-- pause -->

### 3. Support 2-3 Versions Maximum

More becomes unmaintainable

<!-- pause -->

### 4. Deprecation Policy

Clear timeline and communication

<!--
speaker_note: |
  BEST PRACTICES (4 minutes)
  
  • Start early:
    - Even if just v1
    - Sets expectation
    - Infrastructure ready
  
  • Major only:
    - v1, v2, v3 (not v1.2.3)
    - Breaking changes only
    - Additive changes OK
  
  • Version limit:
    - Support cost multiplies
    - Testing complexity
    - Security patches
    - Typical: current + previous
  
  • Deprecation:
    - 6-12 month notice
    - Sunset headers
    - Migration guides
    - Clear communication
-->

<!-- end_slide -->

---

## Deprecation Strategy

### Sunset Header

```java
@GET
@Path("/v1/users")
public Response getUsersV1() {
    return Response.ok(users)
        .header("Sunset", "31 Dec 2024")
        .header("Link", "</v2/users>; rel=\"successor-version\"")
        .build();
}
```

<!-- pause -->

### Deprecation Response

```java
@GET
@Path("/v0/users")
public Response getUsersV0() {
    return Response.status(410) // Gone
        .entity("Version 0 API was retired. Use /v1/users")
        .build();
}
```

<!--
speaker_note: |
  DEPRECATION (3 minutes)
  
  • Sunset header:
    - RFC 8594 standard
    - Clear retirement date
    - Machine readable
  
  • Link header:
    - Points to new version
    - Helps migration
    - Standard format
  
  • Status codes:
    - 410 Gone - version retired
    - 301 Moved - redirects possible
    - 426 Upgrade Required
  
  • Communication:
    - Email notifications
    - Dashboard warnings
    - Blog posts
    - Gradual enforcement
-->

<!-- end_slide -->

---

## [OPTIONAL] Version Migration Patterns

### 1. Dual-Write Period

```java
public void createUser(User user) {
    // Write to both v1 and v2 storage
    userServiceV1.save(convertToV1(user));
    userServiceV2.save(user);
}
```

<!-- pause -->

### 2. Facade Pattern

```java
@Path("/users")
public class UserResourceFacade {
    @Inject UserResourceV1 v1;
    @Inject UserResourceV2 v2;
    
    public Response getUsers(String version) {
        return "2".equals(version) ? 
            v2.getUsers() : v1.getUsers();
    }
}
```

<!--
speaker_note: |
  MIGRATION PATTERNS (3 minutes)
  
  • Dual-write:
    - Transition period strategy
    - Keep both versions in sync
    - No data migration needed
    - Eventually stop v1 writes
  
  • Facade pattern:
    - Single entry point
    - Delegates to versions
    - Shared business logic
    - Clean separation
  
  • Other patterns:
    - Adapter pattern
    - Strategy pattern
    - Bridge pattern
  
  • Migration steps:
    1. Deploy new version
    2. Dual-write period
    3. Migrate old clients
    4. Deprecate old version
    5. Remove old code
-->

<!-- end_slide -->

---

## Real-World Example: User Evolution

### Version 1
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

<!-- pause -->

### Version 2 - Split Name
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john@example.com",
  "fullName": "John Doe"  // Computed
}
```

<!--
speaker_note: |
  EVOLUTION EXAMPLE (3 minutes)
  
  • Common scenario:
    - Started with simple name
    - Need first/last for sorting
    - Breaking change!
  
  • Version 2 features:
    - Split name fields
    - Computed fullName for compatibility
    - Same email field
  
  • Migration code:
    - Parse v1 name field
    - Split on space (naive)
    - Handle edge cases
  
  • Client migration:
    - v1 clients use fullName
    - v2 clients use firstName/lastName
    - Gradual transition
-->

<!-- end_slide -->

---

## Jersey Implementation Example

### Shared Business Logic

```java
public abstract class BaseUserResource {
    @Inject
    protected UserService userService;
    
    protected List<User> getBaseUsers() {
        return userService.findAll();
    }
}
```

<!-- pause -->

### Version-Specific Resources

```java
@Path("/v1/users")
public class UserResourceV1 extends BaseUserResource {
    @GET
    public List<UserV1DTO> getUsers() {
        return getBaseUsers().stream()
            .map(UserV1DTO::fromDomain)
            .collect(Collectors.toList());
    }
}
```

<!--
speaker_note: |
  CODE ORGANIZATION (3 minutes)
  
  • Shared base:
    - Common business logic
    - Dependency injection
    - Reduce duplication
  
  • Version DTOs:
    - Different representations
    - Version-specific fields
    - Conversion methods
  
  • Package structure:
    - com.api.v1.resources
    - com.api.v2.resources
    - com.api.common
  
  • Testing strategy:
    - Test each version
    - Shared test utilities
    - Contract tests
-->

<!-- end_slide -->

---

## Choosing a Strategy

### Use URI Versioning When:
- Public API
- Simple client base
- Clear version boundaries
- Developer experience priority

<!-- pause -->

### Use Header Versioning When:
- Internal APIs
- Need granular control
- Clean URLs important
- Sophisticated clients

<!-- pause -->

### Use Media Type When:
- True REST required
- Complex versioning needs
- Standards compliance critical

<!--
speaker_note: |
  DECISION GUIDE (3 minutes)
  
  • URI versioning fit:
    - B2C APIs
    - Mobile apps
    - Public developers
    - Quick understanding needed
  
  • Header versioning fit:
    - B2B APIs
    - Microservices
    - Internal systems
    - Gradual migration needed
  
  • Media type fit:
    - Enterprise APIs
    - Standards-focused orgs
    - Complex requirements
    - Academic/government
  
  • Hybrid approaches:
    - Major in URI, minor in header
    - Different strategies per resource
    - Migration strategies
-->

<!-- end_slide -->

---

## [OPTIONAL] Common Pitfalls

### ❌ Over-versioning
Every small change gets a version

<!-- pause -->

### ❌ Under-versioning  
Breaking changes without version bump

<!-- pause -->

### ❌ No deprecation plan
Versions live forever

<!-- pause -->

### ❌ Inconsistent versioning
Mix of strategies in same API

<!--
speaker_note: |
  PITFALLS (3 minutes)
  
  • Over-versioning:
    - v1.0.1, v1.0.2, etc.
    - Version fatigue
    - Maintenance nightmare
    - Solution: Major only
  
  • Under-versioning:
    - "It's just a small change"
    - Breaks clients silently
    - Trust destroyed
    - Solution: When in doubt, version
  
  • No deprecation:
    - Supporting v1-v15
    - Security vulnerabilities
    - Technical debt
    - Solution: Clear policy
  
  • Inconsistent:
    - Some URI, some header
    - Developer confusion
    - Documentation nightmare
    - Solution: Pick one, stick to it
-->

<!-- end_slide -->

---

## [OPTIONAL - DEEP DIVE] GraphQL Alternative

### Different Philosophy

```graphql
type User {
  id: ID!
  name: String @deprecated(reason: "Use firstName/lastName")
  firstName: String
  lastName: String
  email: String!
}
```

<!-- pause -->

- Schema evolution, not versioning
- Field deprecation
- Client specifies needed fields
- No version management

<!--
speaker_note: |
  GRAPHQL MENTION (2 minutes)
  
  • Different approach:
    - No versions needed
    - Schema evolves
    - Clients adapt
  
  • How it works:
    - Add fields freely
    - Deprecate old fields
    - Clients query what they need
    - No breaking changes
  
  • Trade-offs:
    - More complex setup
    - Client complexity
    - Caching harder
    - Not REST
  
  • When to consider:
    - Many client types
    - Rapid evolution
    - Complex data needs
    - Team has GraphQL experience
-->

<!-- end_slide -->

---

## Key Takeaways

### 1. **Plan for versioning from day one**

### 2. **Choose strategy based on your context**

### 3. **Document versioning clearly**

### 4. **Have a deprecation strategy**

### 5. **Keep it simple - avoid over-engineering**

<!--
speaker_note: |
  SUMMARY (2 minutes)
  
  • Key messages:
    - No perfect solution
    - Context is king
    - Simplicity valuable
  
  • Remember:
    - URI most common for reasons
    - Header growing in popularity
    - Media type most "correct"
  
  • Your decision:
    - Consider your clients
    - Consider your team
    - Consider your tools
    - Be consistent
  
  • Next exercise:
    - Implement URI versioning
    - Create v1 and v2
    - See trade-offs firsthand
-->

<!-- end_slide -->

---

## Questions?

### Next: Exercise 05 - API Versioning

Implement versioned endpoints in Jersey

<!--
speaker_note: |
  Q&A AND TRANSITION (3 minutes)
  
  • Common questions:
    - "Which do you recommend?" - URI for public, header for internal
    - "How long to support?" - 6-12 months typical
    - "Can we change strategies?" - Yes, but migration is painful
  
  • Exercise preview:
    - 25 minutes
    - Implement URI versioning
    - Create UserV1 and UserV2
    - Test both versions
  
  • Transition:
    - "Let's see this in practice"
    - "You'll implement URI versioning"
    - "Experience the trade-offs"
-->

<!-- end_slide -->