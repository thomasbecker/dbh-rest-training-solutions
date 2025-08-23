---
title: Jackson Integration with Jersey
author: Thomas Becker
theme:
  name: dark
---

# Jackson Integration with Jersey

JSON Processing Made Easy

⏱️ **Duration**: 90 minutes  
🎯 **Goal**: Master JSON handling in Jersey applications

<!--
speaker_note: |
  DAY 2 START (3 minutes)
  
  • Welcome back:
    - "Hope you had a good evening"
    - "Ready for Day 2?"
    - Quick recap of Day 1
  
  • Today's focus:
    - Deep dive into Jackson
    - Security implementation
    - Production readiness
  
  • This module:
    - JSON is everywhere
    - Jackson is the standard
    - Jersey integration seamless
-->

<!-- end_slide -->

---

## Why Jackson?

### The De Facto Standard

- **Most Popular** - Used by Spring, Jersey, Quarkus
- **Feature-Rich** - Annotations, custom serializers, views
- **Performance** - Fast and efficient
- **Flexible** - Highly configurable

<!-- pause -->

### What It Does

```java
// Java Object → JSON
User user = new User("john", "john@example.com");
String json = objectMapper.writeValueAsString(user);
// {"username":"john","email":"john@example.com"}

// JSON → Java Object  
User parsed = objectMapper.readValue(json, User.class);
```

<!--
speaker_note: |
  JACKSON INTRODUCTION (5 minutes)
  
  • Why Jackson matters:
    - JSON is THE format
    - Manual parsing is error-prone
    - Type safety crucial
    - Performance matters
  
  • Jackson ecosystem:
    - jackson-core: Streaming API
    - jackson-databind: Object mapping
    - jackson-annotations: Configuration
  
  • Alternatives exist:
    - Gson (Google)
    - JSON-B (Jakarta)
    - But Jackson dominates
-->

<!-- end_slide -->

---

## Jackson in Jersey

### Automatic Integration

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jackson</artifactId>
    <version>2.35</version>
</dependency>
```

<!-- pause -->

### What You Get

- Automatic JSON ↔ Object conversion
- Content-Type: application/json handling
- Exception mapping for JSON errors
- No manual configuration needed!

<!--
speaker_note: |
  JERSEY INTEGRATION (5 minutes)
  
  • Setup simplicity:
    - Add dependency
    - Jersey auto-discovers
    - Jackson provider registered
    - Just works!
  
  • Behind the scenes:
    - JacksonFeature class
    - MessageBodyReader/Writer
    - Content negotiation
    - Error handling
  
  • No boilerplate:
    - No ObjectMapper setup
    - No manual conversion
    - Clean resources
-->

<!-- end_slide -->

---

## Basic Object Mapping

### Simple POJO

```java
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private LocalDateTime createdAt;
    
    // Getters and setters
}
```

<!-- pause -->

### Automatic Serialization

```java
@GET
@Path("/{id}")
@Produces(MediaType.APPLICATION_JSON)
public Product getProduct(@PathParam("id") Long id) {
    return productService.findById(id);
    // Automatically converted to JSON
}
```

<!--
speaker_note: |
  BASIC MAPPING (7 minutes)
  
  • POJO requirements:
    - Default constructor
    - Getters/setters OR public fields
    - Serializable types
    - No special annotations needed
  
  • Automatic handling:
    - Primitives and wrappers
    - Strings
    - Collections and arrays
    - Nested objects
  
  • Common gotcha:
    - No default constructor = error
    - Private fields without getters = ignored
    - Circular references = stack overflow
-->

<!-- end_slide -->

---

## Jackson Annotations Overview

### Essential Annotations

```java
public class User {
    @JsonProperty("user_name")  // Different JSON field name
    private String username;
    
    @JsonIgnore  // Don't serialize/deserialize
    private String password;
    
    @JsonFormat(pattern = "yyyy-MM-dd")  // Date formatting
    private LocalDate birthDate;
    
    @JsonInclude(Include.NON_NULL)  // Skip null values
    private String middleName;
}
```

<!--
speaker_note: |
  ANNOTATIONS INTRO (8 minutes)
  
  • Most common annotations:
    - @JsonProperty: Field naming
    - @JsonIgnore: Exclusion
    - @JsonFormat: Formatting
    - @JsonInclude: Conditional inclusion
  
  • Why use annotations:
    - API contracts
    - Security (passwords!)
    - Bandwidth optimization
    - Backward compatibility
  
  • Best practice:
    - Use sparingly
    - Document why
    - Consider DTOs instead
-->

<!-- end_slide -->

---

## @JsonProperty - Field Naming

### Mapping Different Names

```java
public class Order {
    @JsonProperty("order_id")
    private Long id;
    
    @JsonProperty("customer_name")
    private String customerName;
    
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
}
```

<!-- pause -->

### JSON Result

```json
{
    "order_id": 123,
    "customer_name": "John Doe",
    "total_amount": 99.99
}
```

<!--
speaker_note: |
  FIELD NAMING (6 minutes)
  
  • Use cases:
    - Legacy API compatibility
    - Different naming conventions
    - External API integration
    - Database column names
  
  • Naming strategies:
    - SNAKE_CASE (Python style)
    - camelCase (JavaScript style)
    - kebab-case (URL style)
    - PascalCase (C# style)
  
  • Can configure globally:
    - PropertyNamingStrategy
    - Avoid annotation repetition
-->

<!-- end_slide -->

---

## @JsonIgnore - Excluding Fields

### Security and Optimization

```java
public class User {
    private Long id;
    private String email;
    
    @JsonIgnore  // Never expose passwords!
    private String password;
    
    @JsonIgnore  // Internal only
    private String salt;
    
    @JsonIgnoreProperties({"password", "salt"})  // Alternative
    public class UserSummary { }
}
```

<!-- pause -->

### Also for Methods

```java
@JsonIgnore
public String getFullName() {
    return firstName + " " + lastName;
}
```

<!--
speaker_note: |
  FIELD EXCLUSION (7 minutes)
  
  • Security critical:
    - Passwords
    - API keys
    - Internal IDs
    - Sensitive data
  
  • Performance:
    - Large fields
    - Computed properties
    - Lazy-loaded data
  
  • Alternatives:
    - @JsonIgnoreProperties on class
    - @JsonView for different views
    - DTOs for different representations
  
  • Common mistake:
    - Forgetting @JsonIgnore on passwords!
-->

<!-- end_slide -->

---

## @JsonFormat - Date and Time

### Controlling Date Formats

```java
public class Event {
    @JsonFormat(shape = JsonFormat.Shape.STRING, 
                pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date timestamp;  // As Unix timestamp
}
```

<!-- pause -->

### Output Examples

```json
{
    "eventDate": "2024-08-26",
    "startTime": "2024-08-26 09:00:00",
    "timestamp": 1724659200000
}
```

<!--
speaker_note: |
  DATE FORMATTING (8 minutes)
  
  • Java 8 Time API:
    - LocalDate, LocalDateTime
    - Instant, ZonedDateTime
    - Duration, Period
    - Need JavaTimeModule
  
  • Format patterns:
    - ISO-8601 default
    - Custom patterns
    - Timezone handling
    - Locale specific
  
  • Best practices:
    - Use ISO-8601 when possible
    - UTC for storage
    - Client timezone for display
    - Document format clearly
-->

<!-- end_slide -->

---

## @JsonInclude - Conditional Inclusion

### Reducing Payload Size

```java
public class Product {
    private Long id;
    private String name;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;  // Omit if null
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> tags;  // Omit if empty
    
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer quantity;  // Omit if 0
}
```

<!-- pause -->

### Global Configuration

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompactResponse {
    // All null fields omitted
}
```

<!--
speaker_note: |
  CONDITIONAL INCLUSION (6 minutes)
  
  • Include options:
    - ALWAYS (default)
    - NON_NULL
    - NON_EMPTY
    - NON_DEFAULT
    - CUSTOM
  
  • Benefits:
    - Smaller payloads
    - Cleaner responses
    - Bandwidth savings
    - Mobile-friendly
  
  • Trade-offs:
    - Implicit vs explicit
    - Client confusion
    - Debugging harder
    - Document behavior
-->

<!-- end_slide -->

---

## Collections and Generics

### Lists and Arrays

```java
@GET
@Produces(MediaType.APPLICATION_JSON)
public List<User> getUsers() {
    return userService.findAll();
    // Automatically serialized to JSON array
}

@POST
@Consumes(MediaType.APPLICATION_JSON)
public Response createUsers(List<User> users) {
    // Automatically deserialized from JSON array
    userService.saveAll(users);
    return Response.ok().build();
}
```

<!-- pause -->

### JSON Arrays

```json
[
    {"id": 1, "name": "Alice"},
    {"id": 2, "name": "Bob"},
    {"id": 3, "name": "Charlie"}
]
```

<!--
speaker_note: |
  COLLECTIONS (7 minutes)
  
  • Supported collections:
    - List, Set, Collection
    - Arrays
    - Maps (as JSON objects)
    - Custom collections
  
  • Type erasure issues:
    - Generic type info lost
    - TypeReference for complex types
    - Jackson handles most cases
  
  • Performance tip:
    - Arrays faster than Lists
    - ArrayList vs LinkedList
    - Consider pagination
-->

<!-- end_slide -->

---

## Maps and Dynamic JSON

### Flexible Structures

```java
@GET
@Path("/config")
public Map<String, Object> getConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put("version", "1.0");
    config.put("features", Arrays.asList("auth", "api"));
    config.put("maintenance", false);
    return config;
}
```

<!-- pause -->

### JSON Output

```json
{
    "version": "1.0",
    "features": ["auth", "api"],
    "maintenance": false
}
```

<!--
speaker_note: |
  MAPS AND DYNAMIC (6 minutes)
  
  • When to use Maps:
    - Dynamic structures
    - Configuration data
    - Metadata
    - Unknown schemas
  
  • Limitations:
    - No compile-time checking
    - Type safety lost
    - Documentation harder
    - Prefer POJOs when possible
  
  • JsonNode alternative:
    - Tree model
    - More control
    - Better for manipulation
-->

<!-- end_slide -->

---

## Nested Objects

### Complex Structures

```java
public class Order {
    private Long id;
    private Customer customer;  // Nested object
    private List<OrderItem> items;  // Nested list
    private Address shippingAddress;  // Nested object
    
    public static class OrderItem {
        private Product product;  // Another level
        private Integer quantity;
        private BigDecimal price;
    }
}
```

<!-- pause -->

### Deep JSON Structure

```json
{
    "id": 123,
    "customer": {
        "id": 456,
        "name": "John Doe"
    },
    "items": [
        {
            "product": {"id": 789, "name": "Widget"},
            "quantity": 2,
            "price": 19.99
        }
    ]
}
```

<!--
speaker_note: |
  NESTED OBJECTS (8 minutes)
  
  • Automatic handling:
    - Deep serialization
    - Recursive processing
    - Circular reference detection
  
  • Common issues:
    - N+1 queries with JPA
    - Lazy loading exceptions
    - Infinite recursion
    - Large payloads
  
  • Solutions:
    - DTOs for views
    - @JsonManagedReference
    - @JsonBackReference
    - @JsonIdentityInfo
-->

<!-- end_slide -->

---

## Handling Null Values

### Null Serialization Options

```java
public class ApiResponse {
    // Include nulls (default)
    private String message;
    
    // Exclude nulls
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    
    // Custom null handling
    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Object data;
}
```

<!-- pause -->

### Global Null Handling

```java
ObjectMapper mapper = new ObjectMapper();
mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
```

<!--
speaker_note: |
  NULL HANDLING (6 minutes)
  
  • Null strategies:
    - Include (explicit)
    - Exclude (compact)
    - Custom representation
    - Default values
  
  • API design:
    - Null vs missing
    - Optional fields
    - Partial updates
    - PATCH operations
  
  • Client considerations:
    - JavaScript undefined vs null
    - Mobile app crashes
    - Backward compatibility
-->

<!-- end_slide -->

---

## Polymorphic Types

### Handling Inheritance

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, 
              property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreditCard.class, name = "credit"),
    @JsonSubTypes.Type(value = PayPal.class, name = "paypal"),
    @JsonSubTypes.Type(value = BankTransfer.class, name = "bank")
})
public abstract class Payment {
    private BigDecimal amount;
}

public class CreditCard extends Payment {
    private String cardNumber;
}
```

<!-- pause -->

### JSON with Type Info

```json
{
    "type": "credit",
    "amount": 99.99,
    "cardNumber": "****1234"
}
```

<!--
speaker_note: |
  POLYMORPHISM (8 minutes)
  
  • Use cases:
    - Payment methods
    - Event types
    - Notification types
    - Plugin systems
  
  • Type discriminators:
    - Property name
    - Class name
    - Custom ID
    - Wrapper object
  
  • Challenges:
    - Security concerns
    - Deserialization attacks
    - Version compatibility
    - Documentation complexity
-->

<!-- end_slide -->

---

## Error Handling

### Jackson Exceptions

```java
@Provider
public class JsonMappingExceptionMapper 
    implements ExceptionMapper<JsonMappingException> {
    
    @Override
    public Response toResponse(JsonMappingException e) {
        return Response.status(400)
            .entity(new ErrorResponse(
                "Invalid JSON format", 
                e.getMessage()
            ))
            .build();
    }
}
```

<!-- pause -->

### Common Errors

- `JsonMappingException` - Structure mismatch
- `JsonParseException` - Invalid JSON syntax
- `UnrecognizedPropertyException` - Unknown fields

<!--
speaker_note: |
  ERROR HANDLING (7 minutes)
  
  • Common issues:
    - Missing required fields
    - Type mismatches
    - Invalid dates
    - Unknown properties
  
  • Best practices:
    - Clear error messages
    - Don't expose internals
    - Log full errors
    - Return helpful hints
  
  • Validation integration:
    - Bean Validation first
    - Jackson parsing second
    - Business logic third
-->

<!-- end_slide -->

---

## Configuration Best Practices

### Centralized ObjectMapper

```java
@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {
    private final ObjectMapper mapper;
    
    public JacksonConfig() {
        mapper = new ObjectMapper();
        
        // Java 8 Time support
        mapper.registerModule(new JavaTimeModule());
        
        // Don't fail on unknown properties
        mapper.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, 
            false
        );
        
        // Pretty print for development
        if (isDevelopment()) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
    }
    
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
```

<!--
speaker_note: |
  CONFIGURATION (8 minutes)
  
  • Common settings:
    - Date formats
    - Null handling
    - Unknown properties
    - Pretty printing
  
  • Modules:
    - JavaTimeModule (Java 8)
    - Jdk8Module (Optional)
    - ParameterNamesModule
    - Custom modules
  
  • Performance:
    - Reuse ObjectMapper
    - Thread-safe
    - Expensive to create
    - Cache if possible
-->

<!-- end_slide -->

---

## Performance Optimization

### Tips for Large Payloads

```java
// Streaming for large arrays
@GET
@Path("/stream")
@Produces(MediaType.APPLICATION_JSON)
public StreamingOutput streamLargeData() {
    return output -> {
        JsonGenerator generator = objectMapper
            .getFactory()
            .createGenerator(output);
        
        generator.writeStartArray();
        for (User user : userRepository.streamAll()) {
            generator.writeObject(user);
        }
        generator.writeEndArray();
        generator.close();
    };
}
```

<!-- pause -->

### Other Optimizations

- Use `@JsonRawValue` for pre-serialized JSON
- Pagination for large collections
- Field filtering with `@JsonView`
- Compression (gzip) for large responses

<!--
speaker_note: |
  PERFORMANCE (7 minutes)
  
  • Streaming benefits:
    - Lower memory usage
    - Faster first byte
    - Handles millions of records
    - Progressive rendering
  
  • Other techniques:
    - Connection pooling
    - Async processing
    - Caching headers
    - CDN for static JSON
  
  • Measurement:
    - Profile first
    - Measure impact
    - Load test
    - Monitor production
-->

<!-- end_slide -->

---

## Testing JSON Serialization

### Unit Testing

```java
@Test
public void testUserSerialization() throws Exception {
    User user = new User("john", "john@example.com");
    
    String json = objectMapper.writeValueAsString(user);
    
    assertThat(json).contains("\"username\":\"john\"");
    assertThat(json).contains("\"email\":\"john@example.com\"");
    assertThat(json).doesNotContain("password");
}

@Test
public void testUserDeserialization() throws Exception {
    String json = "{\"username\":\"jane\",\"email\":\"jane@example.com\"}";
    
    User user = objectMapper.readValue(json, User.class);
    
    assertThat(user.getUsername()).isEqualTo("jane");
    assertThat(user.getEmail()).isEqualTo("jane@example.com");
}
```

<!--
speaker_note: |
  TESTING (6 minutes)
  
  • Test scenarios:
    - Serialization format
    - Deserialization parsing
    - Null handling
    - Error cases
  
  • REST Assured testing:
    - Full integration tests
    - JSON path assertions
    - Schema validation
    - Response mapping
  
  • Tools:
    - JSONAssert library
    - JsonPath expressions
    - Schema validators
    - Snapshot testing
-->

<!-- end_slide -->

---

## Common Pitfalls

### Things to Avoid

1. **Circular References**
```java
// BAD: Causes infinite recursion
public class Parent {
    private List<Child> children;
}
public class Child {
    private Parent parent;  // Circular!
}
```

<!-- pause -->

2. **Exposing Sensitive Data**
```java
// BAD: Password exposed!
public class User {
    private String password;  // Missing @JsonIgnore
}
```

<!-- pause -->

3. **Large Eager Loading**
```java
// BAD: Loads entire database
@JsonIgnore
public List<Order> getAllOrders() {
    return orderRepository.findAll();  // Could be millions!
}
```

<!--
speaker_note: |
  PITFALLS (7 minutes)
  
  • Circular references:
    - Use @JsonManagedReference
    - Use @JsonBackReference
    - Use DTOs
    - Break the cycle
  
  • Security issues:
    - Always audit outputs
    - Use security scanners
    - Review annotations
    - Test with tools
  
  • Performance traps:
    - N+1 queries
    - Eager loading
    - No pagination
    - Missing indexes
  
  • Prevention:
    - Code reviews
    - Integration tests
    - Security audits
    - Performance tests
-->

<!-- end_slide -->

---

## DTOs vs Entities

### Separation of Concerns

```java
// Entity (Database)
@Entity
public class UserEntity {
    @Id
    private Long id;
    private String username;
    private String passwordHash;
    private String salt;
    @OneToMany
    private List<Order> orders;
}

// DTO (API)
public class UserDTO {
    private Long id;
    private String username;
    // No password fields!
    // No lazy-loaded collections!
}
```

<!-- pause -->

### Benefits

- Security - Control what's exposed
- Performance - No lazy loading issues
- Flexibility - Different views of data
- Stability - API independent of database

<!--
speaker_note: |
  DTOS VS ENTITIES (8 minutes)
  
  • Why separate:
    - Security control
    - API stability
    - Performance tuning
    - Clear boundaries
  
  • Mapping strategies:
    - Manual mapping
    - ModelMapper
    - MapStruct
    - Custom converters
  
  • Trade-offs:
    - More classes
    - Mapping code
    - Maintenance overhead
    - But worth it!
  
  • Best practice:
    - Always use DTOs for APIs
    - Never expose entities
    - Version your DTOs
    - Document differences
-->

<!-- end_slide -->

---

## Integration with Bean Validation

### Validation + JSON

```java
public class CreateUserRequest {
    @NotBlank
    @JsonProperty("user_name")
    private String username;
    
    @Email
    @NotNull
    private String email;
    
    @JsonIgnore  // Don't accept from client
    private LocalDateTime createdAt;
}

@POST
@Consumes(MediaType.APPLICATION_JSON)
public Response createUser(@Valid CreateUserRequest request) {
    // Validation happens before deserialization completes
    // Invalid JSON returns 400
    // Validation errors return 422
}
```

<!--
speaker_note: |
  VALIDATION INTEGRATION (5 minutes)
  
  • Order of operations:
    1. JSON parsing
    2. Jackson deserialization
    3. Bean Validation
    4. Business logic
  
  • Error handling:
    - Parse errors = 400
    - Validation errors = 422
    - Business errors = 409
  
  • Best practice:
    - Validate at edge
    - Clear error messages
    - Separate concerns
    - Test all paths
-->

<!-- end_slide -->

---

## Real-World Example

### Complete User API

```java
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    
    @GET
    public List<UserDTO> getUsers(
            @QueryParam("role") String role) {
        return userService.findByRole(role)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @POST
    public Response createUser(@Valid CreateUserRequest request) {
        User user = fromRequest(request);
        user = userService.create(user);
        
        UserDTO dto = toDTO(user);
        return Response.created(URI.create("/users/" + dto.getId()))
            .entity(dto)
            .build();
    }
    
    private UserDTO toDTO(User user) {
        // Safe mapping - no sensitive data
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
}
```

<!--
speaker_note: |
  COMPLETE EXAMPLE (5 minutes)
  
  • Putting it together:
    - DTOs for safety
    - Validation for correctness
    - Proper status codes
    - Clean separation
  
  • Production ready:
    - No entity exposure
    - No password leaks
    - Proper error handling
    - Clear API contract
  
  • Next steps:
    - Add pagination
    - Add filtering
    - Add sorting
    - Add field selection
-->

<!-- end_slide -->

---

## Summary - Jackson Basics

### What We Covered

✅ **Automatic Integration** - Jersey + Jackson = Magic  
✅ **Annotations** - Control serialization precisely  
✅ **Collections** - Lists, Maps, Arrays handled  
✅ **Error Handling** - Graceful JSON failures  
✅ **Best Practices** - DTOs, validation, security

<!-- pause -->

### Key Takeaways

1. **Use DTOs** - Never expose entities directly
2. **Annotate Carefully** - Each one has a purpose
3. **Test Thoroughly** - JSON is your API contract
4. **Think Security** - Never expose sensitive data

<!--
speaker_note: |
  MODULE SUMMARY (3 minutes)
  
  • Main points:
    - Jackson just works
    - But configuration matters
    - Security is critical
    - DTOs are essential
  
  • Common mistakes avoided:
    - Password exposure
    - Circular references
    - Performance issues
    - Missing validation
  
  • Ready for:
    - Advanced features
    - Custom serializers
    - Complex scenarios
    - Production APIs
-->

<!-- end_slide -->

---

## Exercise Time! 🚀

### Exercise 06: Jackson Basics

**Duration**: 45 minutes

**Your Tasks**:
1. Configure Jackson with Jersey
2. Create DTOs with annotations
3. Handle date formatting
4. Implement field filtering
5. Test JSON serialization

<!-- pause -->

### Success Criteria

- ✅ Products serialize correctly
- ✅ Dates formatted as ISO-8601
- ✅ Passwords never exposed
- ✅ Null values handled properly

<!--
speaker_note: |
  EXERCISE INTRODUCTION (2 minutes)
  
  • Exercise goals:
    - Hands-on Jackson
    - Real scenarios
    - Common patterns
    - Best practices
  
  • Support:
    - README has instructions
    - Tests define behavior
    - Ask if stuck
    - Work in pairs OK
  
  • Timing:
    - 45 minutes work
    - 10 minutes review
    - 5 minutes break
-->

<!-- end_slide -->