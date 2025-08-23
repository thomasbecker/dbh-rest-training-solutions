---
title: Advanced Jackson Features
author: Thomas Becker
theme:
  name: dark
---

# Advanced Jackson Features

Custom Serialization and Beyond

⏱️ **Duration**: 60 minutes  
🎯 **Goal**: Master advanced JSON processing techniques

<!--
speaker_note: |
  MODULE INTRODUCTION (2 minutes)
  
  • Building on basics:
    - You know annotations
    - You know mappings
    - Now let's go deeper
  
  • Advanced scenarios:
    - Custom serializers
    - Complex transformations
    - Performance tuning
    - API versioning with JSON
  
  • Real-world focus:
    - Production patterns
    - Enterprise requirements
    - Performance critical
-->

<!-- end_slide -->

---

## Custom Serializers

### When Default Isn't Enough

```java
public class MoneySerializer extends JsonSerializer<Money> {
    @Override
    public void serialize(Money value, 
                         JsonGenerator gen, 
                         SerializerProvider provider) 
                         throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("amount", value.getAmount());
        gen.writeStringField("currency", value.getCurrency());
        gen.writeStringField("formatted", 
            String.format("%s %.2f", 
                value.getCurrency(), 
                value.getAmount()));
        gen.writeEndObject();
    }
}
```

<!-- pause -->

### Registration

```java
@JsonSerialize(using = MoneySerializer.class)
private Money price;
```

<!--
speaker_note: |
  CUSTOM SERIALIZERS (8 minutes)
  
  • When to use:
    - Complex formatting
    - Business rules
    - Performance optimization
    - Legacy API compatibility
  
  • JsonGenerator methods:
    - writeString, writeNumber
    - writeStartObject/Array
    - writeFieldName
    - writeBinary
  
  • Best practices:
    - Keep stateless
    - Handle nulls
    - Consider performance
    - Document behavior
-->

<!-- end_slide -->

---

## Custom Deserializers

### Parsing Complex JSON

```java
public class MoneyDeserializer extends JsonDeserializer<Money> {
    @Override
    public Money deserialize(JsonParser parser, 
                           DeserializationContext ctx) 
                           throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        
        // Handle different formats
        if (node.isNumber()) {
            // Simple number: {"price": 19.99}
            return Money.of(node.doubleValue(), "USD");
        } else if (node.isObject()) {
            // Object: {"price": {"amount": 19.99, "currency": "EUR"}}
            double amount = node.get("amount").asDouble();
            String currency = node.get("currency").asText();
            return Money.of(amount, currency);
        } else if (node.isTextual()) {
            // String: {"price": "$19.99"}
            return Money.parse(node.asText());
        }
        
        throw ctx.wrongTokenException(parser, Money.class, 
            JsonToken.START_OBJECT, "Invalid money format");
    }
}
```

<!--
speaker_note: |
  CUSTOM DESERIALIZERS (8 minutes)
  
  • Complex parsing:
    - Multiple formats
    - Validation logic
    - Default values
    - Error handling
  
  • JsonNode API:
    - Tree traversal
    - Type checking
    - Safe access
    - Null handling
  
  • Error handling:
    - Clear messages
    - Context information
    - Recovery strategies
    - Logging
-->

<!-- end_slide -->

---

## JsonNode - Tree Model

### Dynamic JSON Processing

```java
@POST
@Path("/dynamic")
public Response processDynamic(String jsonString) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(jsonString);
    
    // Navigate the tree
    String type = root.path("type").asText();
    JsonNode data = root.path("data");
    
    // Conditional processing
    if ("user".equals(type)) {
        User user = mapper.treeToValue(data, User.class);
        return processUser(user);
    } else if ("order".equals(type)) {
        Order order = mapper.treeToValue(data, Order.class);
        return processOrder(order);
    }
    
    // Modify the tree
    ((ObjectNode) root).put("processed", true);
    ((ObjectNode) root).put("timestamp", Instant.now().toString());
    
    return Response.ok(root).build();
}
```

<!--
speaker_note: |
  TREE MODEL (7 minutes)
  
  • When to use:
    - Unknown structure
    - Conditional processing
    - Tree manipulation
    - Schema validation
  
  • Navigation methods:
    - path() - safe navigation
    - get() - may return null
    - at() - JSON Pointer
    - findValue() - deep search
  
  • Modification:
    - ObjectNode for objects
    - ArrayNode for arrays
    - put(), remove()
    - Immutable by default
-->

<!-- end_slide -->

---

## @JsonView - Multiple Views

### Different Representations

```java
public class User {
    public interface Summary {}
    public interface Detailed extends Summary {}
    public interface Admin extends Detailed {}
    
    @JsonView(Summary.class)
    private Long id;
    
    @JsonView(Summary.class)
    private String username;
    
    @JsonView(Detailed.class)
    private String email;
    
    @JsonView(Detailed.class)
    private LocalDate birthDate;
    
    @JsonView(Admin.class)
    private LocalDateTime lastLogin;
    
    @JsonView(Admin.class)
    private String ipAddress;
}
```

<!-- pause -->

### Using Views

```java
@GET
@Path("/summary")
@JsonView(User.Summary.class)
public List<User> getUsersSummary() {
    return userService.findAll();
    // Only id and username serialized
}

@GET
@Path("/admin")
@JsonView(User.Admin.class)
@RolesAllowed("ADMIN")
public List<User> getUsersAdmin() {
    return userService.findAll();
    // All fields serialized
}
```

<!--
speaker_note: |
  JSON VIEWS (8 minutes)
  
  • Use cases:
    - Role-based views
    - Summary vs detail
    - Public vs private
    - Mobile vs desktop
  
  • View inheritance:
    - Hierarchical views
    - Composition
    - Multiple interfaces
    - Default view
  
  • Best practices:
    - Document views
    - Test all views
    - Consider DTOs instead
    - Keep simple
-->

<!-- end_slide -->

---

## Mix-ins - External Configuration

### Adding Annotations Without Modifying Classes

```java
// Third-party class we can't modify
public class ExternalUser {
    private String userName;
    private String password;
    private Date createdDate;
    // No source code access!
}

// Mix-in class with annotations
public abstract class ExternalUserMixin {
    @JsonProperty("username")
    String userName;
    
    @JsonIgnore
    String password;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date createdDate;
}

// Configuration
ObjectMapper mapper = new ObjectMapper();
mapper.addMixIn(ExternalUser.class, ExternalUserMixin.class);
```

<!--
speaker_note: |
  MIX-INS (6 minutes)
  
  • When to use:
    - Third-party classes
    - Generated code
    - Legacy systems
    - Clean separation
  
  • How it works:
    - Annotation proxy
    - Applied at runtime
    - No bytecode modification
    - Clean and safe
  
  • Limitations:
    - Runtime only
    - Performance overhead
    - Debugging harder
    - Documentation challenge
-->

<!-- end_slide -->

---

## Streaming API

### Processing Large Data

```java
@GET
@Path("/export")
@Produces(MediaType.APPLICATION_JSON)
public StreamingOutput exportLargeDataset() {
    return output -> {
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartObject();
            generator.writeFieldName("metadata");
            generator.writeStartObject();
            generator.writeStringField("version", "1.0");
            generator.writeNumberField("count", getTotalCount());
            generator.writeEndObject();
            
            generator.writeFieldName("data");
            generator.writeStartArray();
            
            // Stream millions of records
            try (Stream<Record> records = repository.streamAll()) {
                records.forEach(record -> {
                    try {
                        generator.writeStartObject();
                        generator.writeNumberField("id", record.getId());
                        generator.writeStringField("name", record.getName());
                        generator.writeEndObject();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            
            generator.writeEndArray();
            generator.writeEndObject();
        }
    };
}
```

<!--
speaker_note: |
  STREAMING API (7 minutes)
  
  • Benefits:
    - Constant memory usage
    - No object creation
    - Fastest performance
    - Handles huge data
  
  • Use cases:
    - Data exports
    - Report generation
    - ETL processes
    - Log processing
  
  • Trade-offs:
    - More complex code
    - Manual writing
    - Error handling harder
    - No validation
-->

<!-- end_slide -->

---

## Module System

### Extending Jackson

```java
public class CustomModule extends SimpleModule {
    public CustomModule() {
        super("CustomModule", Version.unknownVersion());
        
        // Add custom serializers
        addSerializer(Money.class, new MoneySerializer());
        addDeserializer(Money.class, new MoneyDeserializer());
        
        // Add key serializers for Maps
        addKeySerializer(UserId.class, new UserIdKeySerializer());
        
        // Add mix-ins
        setMixInAnnotation(ExternalUser.class, ExternalUserMixin.class);
    }
}

// Registration
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new CustomModule());
mapper.registerModule(new JavaTimeModule());
mapper.registerModule(new Jdk8Module());
```

<!--
speaker_note: |
  MODULE SYSTEM (6 minutes)
  
  • Module benefits:
    - Reusable configuration
    - Clean organization
    - Dependency management
    - Version control
  
  • Standard modules:
    - JavaTimeModule
    - Jdk8Module (Optional)
    - ParameterNamesModule
    - JSR310Module (deprecated)
  
  • Custom modules:
    - Domain-specific
    - Company standards
    - API versions
    - Format converters
-->

<!-- end_slide -->

---

## Property Naming Strategies

### Global Naming Conventions

```java
ObjectMapper mapper = new ObjectMapper();

// Snake case: user_name, created_date
mapper.setPropertyNamingStrategy(
    PropertyNamingStrategies.SNAKE_CASE);

// Kebab case: user-name, created-date
mapper.setPropertyNamingStrategy(
    PropertyNamingStrategies.KEBAB_CASE);

// Lower camel case: userName, createdDate (default)
mapper.setPropertyNamingStrategy(
    PropertyNamingStrategies.LOWER_CAMEL_CASE);

// Custom strategy
mapper.setPropertyNamingStrategy(new PropertyNamingStrategy() {
    @Override
    public String nameForField(MapperConfig<?> config,
                               AnnotatedField field,
                               String defaultName) {
        return "api_" + defaultName.toLowerCase();
    }
});
```

<!--
speaker_note: |
  NAMING STRATEGIES (5 minutes)
  
  • Common strategies:
    - SNAKE_CASE (Python)
    - KEBAB_CASE (URLs)
    - LOWER_CAMEL_CASE (JS)
    - UPPER_CAMEL_CASE (C#)
  
  • Custom strategies:
    - Prefix/suffix
    - Translations
    - Legacy mapping
    - Department standards
  
  • Override:
    - @JsonProperty wins
    - Explicit over implicit
    - Document strategy
-->

<!-- end_slide -->

---

## Filtering Properties

### Dynamic Field Selection

```java
// Simple filter
@JsonFilter("userFilter")
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
}

// Dynamic filtering
@GET
@Path("/filtered")
public Response getFilteredUsers(@QueryParam("fields") String fields) {
    Set<String> fieldSet = new HashSet<>(Arrays.asList(fields.split(",")));
    
    SimpleFilterProvider filters = new SimpleFilterProvider()
        .addFilter("userFilter", 
            SimpleBeanPropertyFilter.filterOutAllExcept(fieldSet));
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.setFilterProvider(filters);
    
    List<User> users = userService.findAll();
    String json = mapper.writeValueAsString(users);
    
    return Response.ok(json).build();
}

// Usage: GET /users/filtered?fields=id,username
// Returns: [{"id": 1, "username": "john"}]
```

<!--
speaker_note: |
  PROPERTY FILTERING (6 minutes)
  
  • Use cases:
    - GraphQL-like selection
    - Bandwidth optimization
    - Privacy control
    - API flexibility
  
  • Filter types:
    - Include/exclude
    - Prefix matching
    - Custom predicates
    - Depth limiting
  
  • Considerations:
    - Performance impact
    - Cache implications
    - Documentation
    - Security
-->

<!-- end_slide -->

---

## Handling Circular References

### Breaking the Cycle

```java
// Option 1: @JsonManagedReference / @JsonBackReference
public class Author {
    @JsonManagedReference
    private List<Book> books;
}

public class Book {
    @JsonBackReference
    private Author author;  // Not serialized
}

// Option 2: @JsonIdentityInfo
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Employee {
    private Long id;
    private String name;
    private Employee manager;  // Circular OK
    private List<Employee> subordinates;  // Circular OK
}

// Option 3: @JsonIgnoreProperties
public class Department {
    @JsonIgnoreProperties("department")
    private List<Employee> employees;
}
```

<!--
speaker_note: |
  CIRCULAR REFERENCES (7 minutes)
  
  • Three strategies:
    - Managed/Back reference
    - Identity info
    - Ignore properties
  
  • Choosing strategy:
    - One-way: Managed/Back
    - Preserve identity: IdentityInfo
    - Simple break: Ignore
  
  • Best practice:
    - Use DTOs to avoid
    - Design better models
    - Document clearly
    - Test thoroughly
-->

<!-- end_slide -->

---

## Polymorphic Deserialization

### Advanced Type Handling

```java
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    property = "eventType",
    visible = true)
@JsonTypeIdResolver(EventTypeResolver.class)
public abstract class Event {
    private String eventType;
    private Instant timestamp;
}

public class EventTypeResolver extends TypeIdResolverBase {
    private JavaType superType;
    
    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }
    
    @Override
    public Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
    
    @Override
    public String idFromValue(Object obj) {
        return obj.getClass().getSimpleName().toLowerCase();
    }
    
    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<?> subType = resolveClass(id);
        return context.constructSpecializedType(superType, subType);
    }
    
    private Class<?> resolveClass(String id) {
        switch(id) {
            case "login": return LoginEvent.class;
            case "logout": return LogoutEvent.class;
            case "purchase": return PurchaseEvent.class;
            default: throw new IllegalArgumentException("Unknown event: " + id);
        }
    }
}
```

<!--
speaker_note: |
  POLYMORPHIC TYPES (8 minutes)
  
  • Custom resolver:
    - Full control
    - Dynamic loading
    - Plugin systems
    - Version handling
  
  • Security warning:
    - Class loading risks
    - Whitelist approach
    - Never use class names
    - Validate everything
  
  • Use cases:
    - Event systems
    - Message queues
    - Plugin architectures
    - Protocol buffers
-->

<!-- end_slide -->

---

## Custom Annotations

### Building Your Own

```java
// Custom annotation
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = MaskingSerializer.class)
public @interface JsonMasked {
    int visibleChars() default 4;
    char maskChar() default '*';
}

// Serializer using annotation
public class MaskingSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, 
                         SerializerProvider provider) throws IOException {
        JsonMasked annotation = findAnnotation(provider);
        if (annotation != null && value != null) {
            String masked = maskValue(value, 
                annotation.visibleChars(), 
                annotation.maskChar());
            gen.writeString(masked);
        } else {
            gen.writeString(value);
        }
    }
    
    private String maskValue(String value, int visible, char mask) {
        if (value.length() <= visible) return value;
        String visible = value.substring(value.length() - visible);
        String masks = String.valueOf(mask).repeat(value.length() - visible);
        return masks + visible;
    }
}

// Usage
public class CreditCard {
    @JsonMasked(visibleChars = 4)
    private String cardNumber;  // "************1234"
    
    @JsonMasked(visibleChars = 2, maskChar = 'X')
    private String cvv;  // "X23"
}
```

<!--
speaker_note: |
  CUSTOM ANNOTATIONS (7 minutes)
  
  • Creating annotations:
    - Combine existing
    - Add parameters
    - Custom behavior
    - Reusable logic
  
  • Use cases:
    - Data masking
    - Encryption
    - Formatting
    - Validation
  
  • Best practices:
    - Document well
    - Test edge cases
    - Consider performance
    - Keep simple
-->

<!-- end_slide -->

---

## Performance Tuning

### Optimization Techniques

```java
// 1. Reuse ObjectMapper (thread-safe)
@Singleton
public class JsonService {
    private final ObjectMapper mapper = createMapper();
    
    private ObjectMapper createMapper() {
        ObjectMapper m = new ObjectMapper();
        // Disable features you don't need
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return m;
    }
}

// 2. Use @JsonRawValue for pre-serialized JSON
public class CachedResponse {
    @JsonRawValue
    private String cachedJson;  // Already JSON, don't re-serialize
}

// 3. Afterburner module for speed
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new AfterburnerModule());  // 20-30% faster

// 4. Smile format for binary
ObjectMapper smileMapper = new ObjectMapper(new SmileFactory());
byte[] smileData = smileMapper.writeValueAsBytes(object);  // 25% smaller
```

<!--
speaker_note: |
  PERFORMANCE (6 minutes)
  
  • Key optimizations:
    - Reuse ObjectMapper
    - Disable unused features
    - Afterburner module
    - Binary formats
  
  • Measurement:
    - Profile first
    - Benchmark changes
    - Load test
    - Monitor production
  
  • Trade-offs:
    - Speed vs features
    - Size vs readability
    - Memory vs CPU
    - Development vs runtime
-->

<!-- end_slide -->

---

## API Evolution

### Handling Changes Gracefully

```java
// Version 1
public class UserV1 {
    private String name;  // Single field
}

// Version 2 - Split name, maintain compatibility
public class UserV2 {
    private String firstName;
    private String lastName;
    
    @JsonProperty("name")
    @JsonSetter
    public void setFullName(String name) {
        String[] parts = name.split(" ", 2);
        this.firstName = parts[0];
        this.lastName = parts.length > 1 ? parts[1] : "";
    }
    
    @JsonProperty("name")
    @JsonGetter
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

// Version 3 - Deprecate old field
public class UserV3 {
    private String firstName;
    private String lastName;
    
    @Deprecated
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setName(String name) {
        // Still accept old format for compatibility
        setFullName(name);
    }
}
```

<!--
speaker_note: |
  API EVOLUTION (5 minutes)
  
  • Strategies:
    - Dual properties
    - Setter/getter aliases
    - Custom deserializers
    - Version headers
  
  • Best practices:
    - Never remove fields
    - Add, don't modify
    - Document changes
    - Deprecate gradually
  
  • Testing:
    - Old clients work
    - New clients work
    - Migration path clear
    - No data loss
-->

<!-- end_slide -->

---

## Testing Advanced Features

### Comprehensive Testing

```java
@Test
public void testCustomSerializer() {
    Money money = Money.of(19.99, "EUR");
    
    String json = mapper.writeValueAsString(money);
    
    JsonNode node = mapper.readTree(json);
    assertThat(node.get("amount").asDouble()).isEqualTo(19.99);
    assertThat(node.get("currency").asText()).isEqualTo("EUR");
    assertThat(node.get("formatted").asText()).isEqualTo("EUR 19.99");
}

@Test
public void testJsonView() {
    User user = createTestUser();
    
    // Test summary view
    ObjectWriter writer = mapper.writerWithView(User.Summary.class);
    String summaryJson = writer.writeValueAsString(user);
    assertThat(summaryJson).contains("username");
    assertThat(summaryJson).doesNotContain("email");
    
    // Test admin view
    writer = mapper.writerWithView(User.Admin.class);
    String adminJson = writer.writeValueAsString(user);
    assertThat(adminJson).contains("lastLogin");
}

@Test
public void testCircularReference() {
    Author author = new Author("Jane Doe");
    Book book = new Book("Java Guide", author);
    author.addBook(book);
    
    // Should not throw StackOverflowError
    String json = mapper.writeValueAsString(author);
    assertThat(json).contains("Jane Doe");
    assertThat(json).contains("Java Guide");
}
```

<!--
speaker_note: |
  TESTING (5 minutes)
  
  • Test scenarios:
    - Custom serializers
    - Views
    - Circular references
    - Error cases
  
  • Tools:
    - JsonNode assertions
    - Custom matchers
    - Snapshot testing
    - Property testing
  
  • Best practices:
    - Test all paths
    - Edge cases
    - Performance tests
    - Integration tests
-->

<!-- end_slide -->

---

## Real-World Patterns

### Production-Ready Configuration

```java
@Provider
@Singleton
public class JacksonConfiguration implements ContextResolver<ObjectMapper> {
    private final ObjectMapper mapper;
    
    public JacksonConfiguration() {
        mapper = new ObjectMapper();
        
        // Modules
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new AfterburnerModule());
        mapper.registerModule(new CustomDomainModule());
        
        // Features
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        
        // Global settings
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        // Security
        mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        
        // Pretty print in development
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
  PRODUCTION CONFIG (4 minutes)
  
  • Essential setup:
    - All modules
    - Security settings
    - Performance tuning
    - Error handling
  
  • Environment specific:
    - Pretty print dev only
    - Logging levels
    - Cache settings
    - Timeouts
  
  • Documentation:
    - Why each setting
    - Impact analysis
    - Migration notes
    - Performance data
-->

<!-- end_slide -->

---

## Summary - Advanced Jackson

### What We Covered

✅ **Custom Serializers** - Full control over JSON  
✅ **JsonNode** - Dynamic JSON processing  
✅ **Views & Filters** - Multiple representations  
✅ **Performance** - Optimization techniques  
✅ **Evolution** - API versioning strategies

<!-- pause -->

### Key Takeaways

1. **Power vs Complexity** - Use advanced features wisely
2. **Performance Matters** - Profile and optimize
3. **API Evolution** - Plan for change
4. **Test Everything** - Complex features need more tests

<!--
speaker_note: |
  MODULE SUMMARY (2 minutes)
  
  • Advanced features:
    - Powerful but complex
    - Use when needed
    - Document thoroughly
    - Test extensively
  
  • Production ready:
    - Performance tuned
    - Security configured
    - Evolution planned
    - Monitoring enabled
  
  • Next up:
    - Security implementation
    - Apply these concepts
    - Real-world scenarios
-->

<!-- end_slide -->

---

## Questions?

### Before We Move to Security...

Common Advanced Jackson Questions:

- Custom serializer vs DTO?
- Performance impact of Views?
- Circular reference best practice?
- Module organization?

<!-- pause -->

**Remember**: Simple solutions often beat complex ones!

<!--
speaker_note: |
  Q&A TIME (3 minutes)
  
  • Expected questions:
    - When to use advanced features
    - Performance implications
    - Testing strategies
    - Migration approaches
  
  • Key message:
    - Start simple
    - Add complexity as needed
    - Measure impact
    - Document decisions
  
  • Transition:
    - Break time?
    - Then security
    - Applied learning
-->

<!-- end_slide -->