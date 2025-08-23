# Exercise 06: Jackson Integration Basics

⏱️ **Time:** 45 minutes  
🎯 **Objective:** Configure Jackson for JSON processing and implement DTOs with annotations

## Prerequisites

- Completed Exercise 05 (API Versioning)
- Understanding of JSON structure
- Basic knowledge of Java annotations

## Background

Jackson is the de facto standard for JSON processing in Java REST APIs. In this exercise, you'll configure Jackson in your Jersey application and use its powerful annotations to control JSON serialization and deserialization. You'll learn how to customize field names, format dates, and handle complex objects.

## Your Tasks

### Task 1: Configure Jackson in Jersey (10 minutes)

1. **Add Jackson Dependencies**
   
   Add to your `build.gradle`:
   ```gradle
   implementation 'org.glassfish.jersey.media:jersey-media-json-jackson:2.35'
   implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2'
   ```

2. **Register Jackson Feature**
   
   Update your `JerseyConfig.java`:
   ```java
   public JerseyConfig() {
       // Register Jackson for JSON
       register(JacksonFeature.class);
       
       // Register Java 8 time support
       register(new ObjectMapperContextResolver());
       
       // Your existing registrations
       packages("com.dbh.training.rest");
   }
   ```

3. **Create ObjectMapper Configuration**
   
   Create `ObjectMapperContextResolver.java`:
   ```java
   @Provider
   public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
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

### Task 2: Enhance User Model with Jackson (15 minutes)

1. **Add Jackson Annotations to User Model**
   
   Update `User.java` in the `models` package with Jackson annotations:
   ```java
   public class User {
       @JsonProperty("user_id")
       private Long id;
       
       @JsonProperty("user_name")
       @NotBlank(message = "Username is required")
       private String username;
       
       @JsonProperty("email_address")
       @Email(message = "Invalid email format")
       private String email;
       
       @JsonIgnore
       private String password;
       
       @JsonFormat(pattern = "yyyy-MM-dd")
       private LocalDate birthDate;
       
       @JsonProperty("created_at")
       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
       private LocalDateTime createdAt;
       
       @JsonProperty("account_status")
       private AccountStatus status;
       
       @JsonProperty("roles")
       private List<String> roles;
       
       // Constructors, getters, setters
   }
   ```

2. **Create AccountStatus Enum**
   
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

### Task 3: Implement Address Nested Object (10 minutes)

1. **Create Address Class in models package**
   
   Create `Address.java` in `com.dbh.training.rest.models`:
   ```java
   package com.dbh.training.rest.models;
   
   public class Address {
       @JsonProperty("street_line_1")
       private String streetLine1;
       
       @JsonProperty("street_line_2")
       private String streetLine2;
       
       @JsonProperty("city")
       private String city;
       
       @JsonProperty("postal_code")
       private String postalCode;
       
       @JsonProperty("country_code")
       @Size(min = 2, max = 2)
       private String countryCode;
       
       // Constructors, getters, setters
   }
   ```

2. **Add Address to User**
   
   ```java
   @JsonProperty("primary_address")
   private Address primaryAddress;
   
   @JsonProperty("billing_address")
   private Address billingAddress;
   ```

### Task 4: Test JSON Serialization (10 minutes)

1. **Create Test for JSON Output**
   
   ```java
   @Test
   public void testUserSerialization() {
       User user = new User();
       user.setId(1L);
       user.setUsername("john_doe");
       user.setEmail("john@example.com");
       user.setPassword("secret123"); // Should be ignored
       user.setBirthDate(LocalDate.of(1990, 1, 15));
       user.setCreatedAt(LocalDateTime.now());
       user.setStatus(AccountStatus.ACTIVE);
       
       Response response = target("/users/1")
           .request(MediaType.APPLICATION_JSON)
           .get();
       
       assertEquals(200, response.getStatus());
       
       String json = response.readEntity(String.class);
       assertFalse(json.contains("password"));
       assertTrue(json.contains("user_name"));
       assertTrue(json.contains("email_address"));
   }
   ```

2. **Test Deserialization**
   
   ```java
   @Test
   public void testUserDeserialization() {
       String json = "{" +
           "\"user_name\": \"jane_doe\"," +
           "\"email_address\": \"jane@example.com\"," +
           "\"birth_date\": \"1985-06-20\"," +
           "\"account_status\": \"active\"" +
           "}";
       
       Response response = target("/users")
           .request(MediaType.APPLICATION_JSON)
           .post(Entity.json(json));
       
       assertEquals(201, response.getStatus());
       
       User created = response.readEntity(User.class);
       assertEquals("jane_doe", created.getUsername());
       assertEquals(LocalDate.of(1985, 6, 20), created.getBirthDate());
       assertEquals(AccountStatus.ACTIVE, created.getStatus());
   }
   ```

## Running the Tests

```bash
# Set Java 8
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home

# Run tests
./gradlew test --tests "*Jackson*"

# Test with curl
curl -X GET http://localhost:8080/api/v2/users/1 | jq .

curl -X POST http://localhost:8080/api/v2/users \
  -H "Content-Type: application/json" \
  -d '{
    "user_name": "test_user",
    "email_address": "test@example.com",
    "birth_date": "1990-01-15",
    "account_status": "pending"
  }' | jq .
```

## Expected Test Output

```
✓ testUserSerialization: User serialized with custom field names
✓ testUserDeserialization: User deserialized from JSON
✓ testPasswordIgnored: Password field not included in JSON
✓ testDateFormatting: Dates formatted correctly
✓ testEnumSerialization: Enum values use custom names
✓ testAddressNesting: Nested objects serialized correctly
```

## Hints

💡 **Package Organization:**
- Keep domain models in `com.dbh.training.rest.models` package
- Place DTOs in `com.dbh.training.rest.dto` package when needed
- No need to add "DTO" suffix - package name indicates purpose

💡 **ObjectMapper Configuration:**
- Register JavaTimeModule for Java 8 time types
- Disable timestamps for dates (use ISO format)
- Set NON_NULL inclusion to skip null fields

💡 **Common Annotations:**
- `@JsonProperty`: Customize field name in JSON
- `@JsonIgnore`: Exclude field from serialization
- `@JsonFormat`: Control date/time formatting
- `@JsonInclude`: Control null handling

💡 **Testing Tips:**
- Use `jq` command to pretty-print JSON responses
- Check both serialization and deserialization
- Verify that ignored fields are truly absent

## Bonus Tasks

### 1. Custom Date Format (10 minutes)
- Create a custom date serializer for ISO 8601 format
- Apply it globally via ObjectMapper configuration

### 2. Polymorphic Types (15 minutes)
- Create a `Notification` base class
- Implement `EmailNotification` and `SmsNotification` subclasses
- Use `@JsonTypeInfo` and `@JsonSubTypes` for polymorphic serialization

### 3. JSON Views (10 minutes)
- Implement `@JsonView` for different user representations
- Create "Summary" and "Detailed" views
- Apply views to different endpoints

## Helpful Resources

- [Jackson Annotations Guide](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations)
- [Jackson Date Handling](https://www.baeldung.com/jackson-serialize-dates)
- [Jersey Jackson Integration](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/media.html#json.jackson)

## Common Mistakes to Avoid

1. ❌ Forgetting to register JacksonFeature in Jersey
2. ❌ Not handling Java 8 time types (LocalDate, LocalDateTime)
3. ❌ Using WRITE_DATES_AS_TIMESTAMPS (produces timestamps instead of readable dates)
4. ❌ Not testing both serialization AND deserialization
5. ❌ Forgetting @JsonIgnore on sensitive fields like passwords

## Solution Checkpoint

- [ ] Jackson configured with Jersey
- [ ] ObjectMapper customized for dates and nulls
- [ ] User DTO with Jackson annotations
- [ ] Password field ignored in JSON
- [ ] Custom field names working
- [ ] Date formatting correct
- [ ] Nested Address objects working
- [ ] All tests passing

## Need Help?

1. Check Jackson feature registration
2. Verify ObjectMapper configuration
3. Review annotation usage
4. Ask instructor for guidance