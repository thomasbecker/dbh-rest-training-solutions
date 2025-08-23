# Exercise 07: Jackson Advanced Features

⏱️ **Time:** 30 minutes  
🎯 **Objective:** Implement custom serializers, JSON Views, and performance optimizations

## Prerequisites

- Completed Exercise 06 (Jackson Basics)
- Understanding of Jackson annotations
- Familiarity with Java 8 streams

## Background

While Jackson's annotations handle most use cases, complex scenarios require custom serializers and deserializers. JSON Views allow different representations of the same object for different clients. In this exercise, you'll implement these advanced features to handle complex business requirements.

**Note on DTOs**: For this exercise, we'll enhance the existing User model with views rather than creating separate DTOs. In production, you might create separate DTO classes (UserSummary, UserDetails) in the `dto` package for different representations, but JSON Views provide a cleaner solution when the same model needs multiple representations.

## Your Tasks

### Task 1: Create Custom Money Serializer (10 minutes)

1. **Create Money Class in models package**
   
   Create `Money.java` in `com.dbh.training.rest.models`:
   ```java
   package com.dbh.training.rest.models;
   
   import java.math.BigDecimal;
   import java.math.RoundingMode;
   import java.util.Currency;
   
   public class Money {
       private BigDecimal amount;
       private Currency currency;
       
       public Money(BigDecimal amount, Currency currency) {
           this.amount = amount;
           this.currency = currency;
       }
       
       public Money(String amount, String currencyCode) {
           this.amount = new BigDecimal(amount);
           this.currency = Currency.getInstance(currencyCode);
       }
       
       // Getters
       public BigDecimal getAmount() { return amount; }
       public Currency getCurrency() { return currency; }
       
       // Business methods
       public String getFormatted() {
           return currency.getSymbol() + " " + 
                  amount.setScale(2, RoundingMode.HALF_UP);
       }
   }
   ```

2. **Create Custom Serializer**
   
   Create in `com.dbh.training.rest.config.jackson` package:
   ```java
   package com.dbh.training.rest.config.jackson;
   
   public class MoneySerializer extends JsonSerializer<Money> {
       @Override
       public void serialize(Money value, JsonGenerator gen, 
                            SerializerProvider serializers) throws IOException {
           if (value == null) {
               gen.writeNull();
               return;
           }
           
           // Write as nested object
           gen.writeStartObject();
           gen.writeNumberField("amount", value.getAmount());
           gen.writeStringField("currency", value.getCurrency().getCurrencyCode());
           gen.writeStringField("formatted", value.getFormatted());
           gen.writeEndObject();
       }
   }
   ```

3. **Create Custom Deserializer**
   
   Also in `com.dbh.training.rest.config.jackson`:
   ```java
   package com.dbh.training.rest.config.jackson;
   
   public class MoneyDeserializer extends JsonDeserializer<Money> {
       @Override
       public Money deserialize(JsonParser p, DeserializationContext ctxt) 
               throws IOException {
           JsonNode node = p.getCodec().readTree(p);
           
           BigDecimal amount = node.get("amount").decimalValue();
           String currency = node.get("currency").asText();
           
           return new Money(amount, Currency.getInstance(currency));
       }
   }
   ```

4. **Register with Jackson**
   
   ```java
   public class User {
       // ... existing fields ...
       
       @JsonSerialize(using = MoneySerializer.class)
       @JsonDeserialize(using = MoneyDeserializer.class)
       private Money accountBalance;
   }
   ```

### Task 2: Implement JSON Views (10 minutes)

1. **Define View Classes**
   
   Create `Views.java` in `com.dbh.training.rest.dto` package:
   ```java
   package com.dbh.training.rest.dto;
   
   public class Views {
       public static class Public { }
       public static class Internal extends Public { }
       public static class Admin extends Internal { }
   }
   ```

2. **Apply Views to User Fields**
   
   ```java
   public class User {
       @JsonView(Views.Public.class)
       @JsonProperty("user_id")
       private Long id;
       
       @JsonView(Views.Public.class)
       @JsonProperty("user_name")
       private String username;
       
       @JsonView(Views.Public.class)
       @JsonProperty("email_address")
       private String email;
       
       @JsonView(Views.Internal.class)
       @JsonProperty("birth_date")
       @JsonFormat(pattern = "yyyy-MM-dd")
       private LocalDate birthDate;
       
       @JsonView(Views.Internal.class)
       @JsonProperty("account_balance")
       @JsonSerialize(using = MoneySerializer.class)
       @JsonDeserialize(using = MoneyDeserializer.class)
       private Money accountBalance;
       
       @JsonView(Views.Admin.class)
       @JsonProperty("created_at")
       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
       private LocalDateTime createdAt;
       
       @JsonView(Views.Admin.class)
       @JsonProperty("last_login")
       private LocalDateTime lastLogin;
       
       @JsonView(Views.Admin.class)
       @JsonProperty("login_attempts")
       private Integer loginAttempts;
       
       @JsonIgnore  // Never shown
       private String password;
   }
   ```

3. **Apply Views in Resources**
   
   ```java
   @Path("/users")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public class UserResource {
       
       @GET
       @Path("/{id}")
       @JsonView(Views.Public.class)
       public Response getPublicUser(@PathParam("id") Long id) {
           User user = userService.findById(id);
           return Response.ok(user).build();
       }
       
       @GET
       @Path("/{id}/details")
       @JsonView(Views.Internal.class)
       @RolesAllowed("USER")
       public Response getInternalUser(@PathParam("id") Long id) {
           User user = userService.findById(id);
           return Response.ok(user).build();
       }
       
       @GET
       @Path("/{id}/admin")
       @JsonView(Views.Admin.class)
       @RolesAllowed("ADMIN")
       public Response getAdminUser(@PathParam("id") Long id) {
           User user = userService.findById(id);
           return Response.ok(user).build();
       }
   }
   ```

### Task 3: Performance Optimization with Streaming (10 minutes)

1. **Implement Streaming for Large Collections**
   
   ```java
   @GET
   @Path("/stream")
   @Produces(MediaType.APPLICATION_JSON)
   public StreamingOutput streamAllUsers() {
       return new StreamingOutput() {
           @Override
           public void write(OutputStream output) throws IOException {
               JsonFactory jsonFactory = new JsonFactory();
               try (JsonGenerator generator = jsonFactory.createGenerator(output)) {
                   generator.writeStartArray();
                   
                   // Stream users from database
                   userService.streamAll().forEach(user -> {
                       try {
                           generator.writeStartObject();
                           generator.writeNumberField("user_id", user.getId());
                           generator.writeStringField("user_name", user.getUsername());
                           generator.writeStringField("email_address", user.getEmail());
                           generator.writeEndObject();
                       } catch (IOException e) {
                           throw new RuntimeException(e);
                       }
                   });
                   
                   generator.writeEndArray();
               }
           }
       };
   }
   ```

2. **Create Custom Mix-in for Third-Party Classes**
   
   In `com.dbh.training.rest.config.jackson`:
   ```java
   package com.dbh.training.rest.config.jackson;
   
   // Mix-in for a third-party class we can't modify
   public abstract class ThirdPartyClassMixIn {
       @JsonProperty("renamed_field")
       abstract String getOriginalFieldName();
       
       @JsonIgnore
       abstract String getInternalField();
   }
   
   // Register in ObjectMapper
   public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
       public ObjectMapperContextResolver() {
           mapper = new ObjectMapper();
           // ... existing config ...
           
           // Register mix-in
           mapper.addMixIn(ThirdPartyClass.class, ThirdPartyClassMixIn.class);
       }
   }
   ```

## Running the Tests

```bash
# Set Java 8
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home

# Run tests
./gradlew test --tests "*Advanced*"

# Test different views
curl -X GET http://localhost:8080/api/users/1 | jq .           # Public view
curl -X GET http://localhost:8080/api/users/1/details | jq .   # Internal view
curl -X GET http://localhost:8080/api/users/1/admin | jq .     # Admin view

# Test streaming endpoint
curl -X GET http://localhost:8080/api/users/stream | jq .
```

## Expected Test Output

```
✓ testMoneySerializer: Money serialized with amount, currency, formatted
✓ testMoneyDeserializer: Money deserialized from JSON
✓ testPublicView: Only public fields visible
✓ testInternalView: Public + internal fields visible
✓ testAdminView: All fields except password visible
✓ testStreamingPerformance: Large dataset streamed efficiently
```

## Hints

💡 **Package Organization:**
- Custom serializers/deserializers in `com.dbh.training.rest.config.jackson`
- View definitions in `com.dbh.training.rest.dto`
- Domain models stay in `com.dbh.training.rest.models`
- Mix-ins also in `config.jackson` package

💡 **Custom Serializers:**
- Extend `JsonSerializer<T>` for serialization
- Extend `JsonDeserializer<T>` for deserialization
- Handle null values explicitly
- Use `JsonGenerator` for writing, `JsonParser` for reading

💡 **JSON Views:**
- Views are hierarchical (inheritance works)
- Apply at method level for REST endpoints
- Can mix with @JsonIgnore for absolute exclusion

💡 **Performance Tips:**
- Use streaming for large collections
- Avoid loading entire dataset into memory
- Consider pagination as alternative

## Bonus Tasks

### 1. Conditional Serialization (10 minutes)
- Create a serializer that includes fields based on user permissions
- Use `SerializerProvider` to access context

### 2. Custom Annotations (15 minutes)
- Create `@Masked` annotation for sensitive data
- Implement serializer that masks marked fields
- Example: "4111****1111" for credit cards

### 3. JsonNode Manipulation (10 minutes)
- Parse arbitrary JSON into JsonNode
- Modify tree structure programmatically
- Convert back to POJO

## Helpful Resources

- [Custom Serializers Guide](https://www.baeldung.com/jackson-custom-serialization)
- [JSON Views Tutorial](https://www.baeldung.com/jackson-json-view-annotation)
- [Jackson Streaming API](https://www.baeldung.com/jackson-streaming-api)
- [Mix-ins Documentation](https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations)

## Common Mistakes to Avoid

1. ❌ Not handling null in custom serializers
2. ❌ Forgetting to register custom serializers
3. ❌ Using wrong view hierarchy (not extending parent views)
4. ❌ Loading entire collection instead of streaming
5. ❌ Not closing JsonGenerator in streaming code

## Solution Checkpoint

- [ ] Money serializer/deserializer working
- [ ] JSON Views showing different field sets
- [ ] Public view shows minimal fields
- [ ] Admin view shows all except password
- [ ] Streaming endpoint handles large data
- [ ] Mix-in modifies third-party class
- [ ] All tests passing

## Need Help?

1. Check serializer registration
2. Verify view hierarchy
3. Debug with ObjectMapper directly
4. Review streaming API usage
5. Ask instructor for clarification