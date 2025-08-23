---
title: Exercise 06 - Jackson Basics
author: Thomas Becker
theme:
  name: dark
---

# Exercise 06: Jackson Basics

Configure Jackson and implement DTOs with annotations

⏱️ **Duration**: 45 minutes  
🎯 **Goal**: Master Jackson JSON processing fundamentals

<!-- end_slide -->

---

## Your Mission

Build a properly configured Jackson integration with:

✅ ObjectMapper configuration  
✅ Custom field naming  
✅ Date formatting  
✅ Nested objects  
✅ Security (hide passwords)

<!-- pause -->

**Let's configure professional JSON handling!** 💪

<!--
speaker_note: |
  INTRODUCTION (2 minutes)
  
  • Set the context:
    - "Jackson gives us control"
    - "Professional JSON APIs"
    - "Security and formatting"
  
  • Motivate the exercise:
    - Real-world requirements
    - Client expectations
    - API contracts
-->

<!-- end_slide -->

---

## Package Organization

### Project Structure

```
com.dbh.training.rest/
├── models/         # Domain models (User, Address)
├── dto/            # Data Transfer Objects
├── resources/      # REST endpoints
└── config/         # Configuration classes
```

<!-- pause -->

💡 **Key Points:**
- Models package: Core entities with Jackson annotations
- DTO package: Specialized representations when needed
- No "DTO" suffix - package indicates purpose

<!--
speaker_note: |
  PACKAGE STRUCTURE
  
  • Explain separation:
    - models: Domain objects
    - dto: API contracts
    - Clean architecture
  
  • When to use DTOs:
    - Different representations
    - Request/response specific
    - Not needed for basic Jackson
-->

<!-- end_slide -->

---

## Task 1: Configure Jackson (10 min)

### Add Dependencies

📚 [Jackson Documentation](https://github.com/FasterXML/jackson)

💡 **Hint**: Need Jackson + Java Time module

<!-- pause -->

```gradle
implementation 'org.glassfish.jersey.media:jersey-media-json-jackson:2.35'
implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2'
```

<!-- pause -->

### Register with Jersey

```java
public JerseyConfig() {
    register(JacksonFeature.class);
    register(new ObjectMapperContextResolver());
    packages("com.dbh.training.rest");
}
```

<!--
speaker_note: |
  TASK 1 (10 minutes)
  
  • Dependencies:
    - Jersey Jackson integration
    - Java Time module crucial
    - Version compatibility
  
  • Registration:
    - JacksonFeature required
    - ContextResolver for config
    - Package scanning
  
  • Common issue:
    - Forgetting JacksonFeature
    - Wrong versions
-->

<!-- end_slide -->

---

## Task 1: ObjectMapper Configuration

### Create ContextResolver

💡 **Hint**: Configure dates, nulls, and modules

<!-- pause -->

```java
@Provider
public class ObjectMapperContextResolver 
        implements ContextResolver<ObjectMapper> {
    
    private final ObjectMapper mapper;
    
    public ObjectMapperContextResolver() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
```

<!-- pause -->

✅ Java 8 time support enabled!

<!--
speaker_note: |
  OBJECTMAPPER CONFIG
  
  • Key configurations:
    - JavaTimeModule for LocalDate
    - Disable timestamps (readable)
    - Skip null fields
  
  • Why ContextResolver:
    - Jersey integration point
    - Global configuration
    - Reusable mapper
-->

<!-- end_slide -->

---

## Task 2: Enhance User Model (15 min)

### Apply Jackson Annotations

📚 [Jackson Annotations Guide](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations)

💡 **Hint**: Work with User in `models` package - no separate DTO needed yet

<!-- pause -->

```java
public class User {
    @JsonProperty("user_id")
    private Long id;
    
    @JsonProperty("user_name")
    @NotBlank(message = "Username is required")
    private String username;
    
    @JsonIgnore
    private String password;  // Never in JSON!
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
```

<!--
speaker_note: |
  TASK 2 (15 minutes)
  
  • Annotations explained:
    - @JsonProperty: field naming
    - @JsonIgnore: security
    - @JsonFormat: date patterns
  
  • Best practices:
    - Consistent naming (snake_case)
    - Always hide passwords
    - ISO date formats
  
  • Let them explore:
    - Try different formats
    - Test the output
-->

<!-- end_slide -->

---

## Task 2: Enum Handling

### Create AccountStatus Enum

💡 **Hint**: Control enum representation in JSON

<!-- pause -->

```java
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AccountStatus {
    @JsonProperty("active")
    ACTIVE,
    
    @JsonProperty("suspended")
    SUSPENDED,
    
    @JsonProperty("pending")
    PENDING_VERIFICATION
}
```

<!-- pause -->

**Result**: `"account_status": "active"` instead of `"ACTIVE"`

<!--
speaker_note: |
  ENUM HANDLING
  
  • Why customize enums:
    - Client-friendly values
    - Backward compatibility
    - Clear API contracts
  
  • Shape.STRING:
    - Not ordinal numbers
    - Readable values
-->

<!-- end_slide -->

---

## Task 3: Nested Objects (10 min)

### Create Address Class

📚 [Nested Object Patterns](https://www.baeldung.com/jackson-nested-values)

💡 **Hint**: Create Address in `models` package - it's a domain object

<!-- pause -->

```java
public class Address {
    @JsonProperty("street_line_1")
    private String streetLine1;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("postal_code")
    private String postalCode;
    
    @JsonProperty("country_code")
    @Size(min = 2, max = 2)
    private String countryCode;
}
```

<!-- pause -->

```java
// In User class
@JsonProperty("primary_address")
private Address primaryAddress;

@JsonProperty("billing_address") 
private Address billingAddress;
```

<!--
speaker_note: |
  TASK 3 (10 minutes)
  
  • Nested objects:
    - Automatic serialization
    - Reusable components
    - Clean structure
  
  • Validation works:
    - Bean validation on nested
    - Cascading with @Valid
-->

<!-- end_slide -->

---

## Task 4: Test JSON Output (10 min)

### Test Serialization

💡 **Hint**: Verify field names and password exclusion

<!-- pause -->

```java
@Test
public void testUserSerialization() {
    User user = new User();
    user.setPassword("secret123"); // Should be ignored
    user.setBirthDate(LocalDate.of(1990, 1, 15));
    
    Response response = target("/users/1")
        .request(MediaType.APPLICATION_JSON)
        .get();
    
    String json = response.readEntity(String.class);
    assertFalse(json.contains("password"));  // Security!
    assertTrue(json.contains("user_name"));  // Renamed
    assertTrue(json.contains("1990-01-15")); // Formatted
}
```

<!-- pause -->

✅ Secure, formatted, professional JSON!

<!--
speaker_note: |
  TASK 4 (10 minutes)
  
  • Testing priorities:
    - Security first (no password)
    - Field naming correct
    - Date formatting works
  
  • Tools:
    - Use jq for pretty print
    - Check actual JSON string
    - Both directions
-->

<!-- end_slide -->

---

## Test with cURL

### Create User

```bash
curl -X POST http://localhost:8080/api/v2/users \
  -H "Content-Type: application/json" \
  -d '{
    "user_name": "john_doe",
    "email_address": "john@example.com",
    "birth_date": "1990-01-15",
    "account_status": "active",
    "primary_address": {
      "street_line_1": "123 Main St",
      "city": "Berlin",
      "postal_code": "10115",
      "country_code": "DE"
    }
  }'
```

<!-- pause -->

### Get User

```bash
curl -X GET http://localhost:8080/api/v2/users/1 | jq .
```

<!--
speaker_note: |
  CURL TESTING
  
  • Show them:
    - POST with nested JSON
    - GET to verify output
    - jq for formatting
  
  • Check together:
    - Password not in response
    - Dates formatted
    - Nested objects work
-->

<!-- end_slide -->

---

## Common Mistakes ❌

### 1. Jackson Not Working

```java
// WRONG - Forgot to register
public JerseyConfig() {
    packages("com.dbh.training.rest");
}

// RIGHT - Register Jackson
public JerseyConfig() {
    register(JacksonFeature.class);  // Required!
    packages("com.dbh.training.rest");
}
```

<!-- pause -->

### 2. Dates as Timestamps

```java
// WRONG - Produces: "birthDate": 1579046400000

// RIGHT - Configure ObjectMapper
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
// Produces: "birthDate": "1990-01-15"
```

<!--
speaker_note: |
  COMMON ISSUES
  
  • Issue 1: No JSON
    - Check JacksonFeature
    - Verify dependencies
  
  • Issue 2: Ugly dates
    - Disable timestamps
    - Add JavaTimeModule
  
  • Quick fixes:
    - Show them the solutions
    - Let them fix themselves
-->

<!-- end_slide -->

---

## ⏰ Checkpoint: 20 Minutes

You should have:

✅ Jackson configured and registered  
✅ ObjectMapper with date handling  
✅ User with @JsonProperty annotations  
✅ Password field hidden with @JsonIgnore  

<!-- pause -->

### Quick Test

```bash
curl http://localhost:8080/api/v2/users/1 | jq .
```

Should see: `user_name`, `email_address`, formatted dates  
Should NOT see: `password` field

<!--
speaker_note: |
  20 MINUTE CHECK
  
  • Check progress:
    - Who has Jackson working?
    - Anyone stuck on config?
    - Date formatting OK?
  
  • Help strugglers:
    - Pair them up
    - Provide config snippet
    - Move forward together
-->

<!-- end_slide -->

---

## ⏰ Checkpoint: 40 Minutes

Complete solution should have:

✅ All Jackson annotations working  
✅ Nested Address objects  
✅ Enum with custom values  
✅ Tests for serialization/deserialization  
✅ No sensitive data in JSON  

<!-- pause -->

### Final Validation

```bash
./gradlew test --tests "*Jackson*"
```

All tests should pass! 🎉

<!--
speaker_note: |
  40 MINUTE CHECK
  
  • Final verification:
    - Run tests together
    - Check JSON output
    - Validate security
  
  • Wrap up:
    - Quick review of annotations
    - Why Jackson matters
    - Transition to advanced
-->

<!-- end_slide -->

---

## Bonus Tasks 🚀

### For Fast Finishers

1. **Custom Date Format** (10 min)
   - Create ISO 8601 serializer
   - Apply globally

2. **Polymorphic Types** (15 min)
   - Base `Notification` class
   - Email/SMS subclasses
   - Use @JsonTypeInfo

3. **JSON Views** (10 min)
   - Summary vs Detailed views
   - Different field sets

<!--
speaker_note: |
  BONUS TASKS
  
  • Guide fast finishers:
    - Point to bonus section
    - Suggest based on interest
    - Views preview Exercise 07
  
  • Keep them engaged:
    - Challenge themselves
    - Help others
    - Explore documentation
-->

<!-- end_slide -->

---

## Key Takeaways 🎯

<!-- pause -->

✅ **Jackson requires explicit registration** in Jersey

<!-- pause -->

✅ **@JsonIgnore protects sensitive data** like passwords

<!-- pause -->

✅ **@JsonProperty controls field names** in JSON

<!-- pause -->

✅ **ObjectMapper configuration is global** and reusable

<!-- pause -->

✅ **Always test both serialization AND deserialization**

<!--
speaker_note: |
  TAKEAWAYS
  
  • Reinforce key points:
    - Configuration critical
    - Security paramount
    - Testing essential
  
  • Connect to production:
    - Real APIs need this
    - Clients expect quality
    - Standards matter
-->

<!-- end_slide -->

---

## Questions? 🤔

### Common Topics

- Date timezone handling?
- Null vs missing fields?
- Collection formatting?
- Error response format?

<!-- pause -->

### Next Up

**Exercise 07**: Jackson Advanced Features
- Custom serializers
- JSON Views
- Performance optimization

<!--
speaker_note: |
  Q&A AND TRANSITION
  
  • Address questions:
    - Quick answers only
    - Defer complex to break
    - Note for later
  
  • Preview next:
    - Build on basics
    - More control
    - Production patterns
  
  • Energy check:
    - Break needed?
    - Pace OK?
    - Ready for advanced?
-->

<!-- end_slide -->