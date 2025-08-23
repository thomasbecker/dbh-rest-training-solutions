---
title: Exercise 07 - Jackson Advanced
author: Thomas Becker
theme:
  name: dark
---

# Exercise 07: Jackson Advanced

Custom serializers, JSON Views, and performance optimization

⏱️ **Duration**: 30 minutes  
🎯 **Goal**: Master advanced Jackson features for production

<!-- end_slide -->

---

## Your Mission

Implement advanced Jackson features:

✅ Custom Money serializer/deserializer  
✅ JSON Views for role-based visibility  
✅ Streaming for performance  
✅ Mix-ins for third-party classes  

<!-- pause -->

**Let's build production-grade JSON handling!** 🚀

<!--
speaker_note: |
  INTRODUCTION (2 minutes)
  
  • Set expectations:
    - Real-world scenarios
    - Production patterns
    - Performance matters
  
  • Motivate:
    - These are pro skills
    - Differentiate yourself
    - Solve complex problems
-->

<!-- end_slide -->

---

## DTOs vs Models with Views

### When to Use What?

**JSON Views (this exercise):**
- Same model, different representations
- Role-based visibility
- Cleaner than multiple DTOs

<!-- pause -->

**Separate DTOs (production):**
- Completely different structure needed
- API versioning requirements
- Decoupling from domain model

<!-- pause -->

💡 **Today**: We'll use Views on the User model for simplicity

<!--
speaker_note: |
  DTO VS VIEWS
  
  • Views advantages:
    - Less code duplication
    - Single source of truth
    - Inheritance works well
  
  • When DTOs better:
    - Complex transformations
    - Legacy API support
    - Team preference
  
  • Both valid approaches!
-->

<!-- end_slide -->

---

## Task 1: Custom Money Serializer (10 min)

### Create Money Class

📚 [Custom Serializers Guide](https://www.baeldung.com/jackson-custom-serialization)

💡 **Hint**: Money goes in `models`, serializers in `config.jackson`

<!-- pause -->

```java
public class Money {
    private BigDecimal amount;
    private Currency currency;
    
    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    public String getFormatted() {
        return currency.getSymbol() + " " + 
               amount.setScale(2, RoundingMode.HALF_UP);
    }
}
```

<!--
speaker_note: |
  TASK 1 START (10 minutes)
  
  • Money class:
    - BigDecimal for precision
    - Currency for i18n
    - Formatted for display
  
  • Why custom serializer:
    - Complex formatting
    - Multiple representations
    - Business logic
-->

<!-- end_slide -->

---

## Task 1: Money Serializer

### Implement JsonSerializer

💡 **Hint**: Always handle null values!

<!-- pause -->

```java
public class MoneySerializer extends JsonSerializer<Money> {
    @Override
    public void serialize(Money value, JsonGenerator gen, 
                         SerializerProvider serializers) 
                         throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        
        gen.writeStartObject();
        gen.writeNumberField("amount", value.getAmount());
        gen.writeStringField("currency", 
                            value.getCurrency().getCurrencyCode());
        gen.writeStringField("formatted", value.getFormatted());
        gen.writeEndObject();
    }
}
```

<!-- pause -->

**Output**: `{"amount": 100.50, "currency": "EUR", "formatted": "€ 100.50"}`

<!--
speaker_note: |
  SERIALIZER IMPLEMENTATION
  
  • Key points:
    - Null check critical
    - writeStartObject/End
    - Multiple fields
  
  • JsonGenerator methods:
    - writeNumberField
    - writeStringField
    - writeObjectField
-->

<!-- end_slide -->

---

## Task 1: Money Deserializer

### Implement JsonDeserializer

💡 **Hint**: Parse JsonNode tree structure

<!-- pause -->

```java
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

<!-- pause -->

### Register on Field

```java
@JsonSerialize(using = MoneySerializer.class)
@JsonDeserialize(using = MoneyDeserializer.class)
private Money accountBalance;
```

<!--
speaker_note: |
  DESERIALIZER
  
  • JsonNode approach:
    - Tree structure
    - Easy navigation
    - Type conversion
  
  • Registration:
    - Field level
    - Or global in mapper
  
  • Test round-trip!
-->

<!-- end_slide -->

---

## Task 2: JSON Views (10 min)

### Define View Hierarchy

📚 [JSON Views Tutorial](https://www.baeldung.com/jackson-json-view-annotation)

💡 **Hint**: Views.java goes in `dto` package - it defines contracts

<!-- pause -->

```java
public class Views {
    public static class Public { }
    
    public static class Internal extends Public { }
    
    public static class Admin extends Internal { }
}
```

<!-- pause -->

**Hierarchy**: Admin sees all → Internal sees most → Public sees minimal

<!--
speaker_note: |
  TASK 2 START (10 minutes)
  
  • View concept:
    - Different audiences
    - Same model
    - Role-based visibility
  
  • Inheritance crucial:
    - Admin extends Internal
    - Internal extends Public
    - Cumulative fields
-->

<!-- end_slide -->

---

## Task 2: Apply Views to Fields

### Annotate User Fields

💡 **Hint**: Start with most restrictive (Public)

<!-- pause -->

```java
public class User {
    @JsonView(Views.Public.class)
    @JsonProperty("user_id")
    private Long id;
    
    @JsonView(Views.Public.class)
    @JsonProperty("user_name")
    private String username;
    
    @JsonView(Views.Internal.class)
    @JsonProperty("birth_date")
    private LocalDate birthDate;
    
    @JsonView(Views.Admin.class)
    @JsonProperty("last_login")
    private LocalDateTime lastLogin;
    
    @JsonIgnore  // Never shown!
    private String password;
}
```

<!--
speaker_note: |
  APPLYING VIEWS
  
  • Strategy:
    - Public: Minimal info
    - Internal: User's data
    - Admin: Operational data
    - JsonIgnore: Never visible
  
  • Field appears in:
    - Its view
    - All extending views
-->

<!-- end_slide -->

---

## Task 2: Views in REST Endpoints

### Apply View per Endpoint

💡 **Hint**: Same resource, different views

<!-- pause -->

```java
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
```

<!-- pause -->

✅ Role-based field visibility achieved!

<!--
speaker_note: |
  VIEWS IN ENDPOINTS
  
  • Same object, different output:
    - /users/1 → minimal
    - /users/1/details → more
    - /users/1/admin → everything
  
  • Security integration:
    - @RolesAllowed
    - Views complement auth
-->

<!-- end_slide -->

---

## Task 3: Streaming Performance (10 min)

### Stream Large Collections

📚 [Jackson Streaming API](https://www.baeldung.com/jackson-streaming-api)

💡 **Hint**: Don't load all data into memory

<!-- pause -->

```java
@GET
@Path("/stream")
@Produces(MediaType.APPLICATION_JSON)
public StreamingOutput streamAllUsers() {
    return output -> {
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator gen = jsonFactory.createGenerator(output)) {
            gen.writeStartArray();
            
            userService.streamAll().forEach(user -> {
                gen.writeStartObject();
                gen.writeNumberField("user_id", user.getId());
                gen.writeStringField("user_name", user.getUsername());
                gen.writeEndObject();
            });
            
            gen.writeEndArray();
        }
    };
}
```

<!--
speaker_note: |
  TASK 3 (10 minutes)
  
  • Why streaming:
    - Large datasets
    - Memory efficiency
    - Prevents OOM
  
  • Pattern:
    - StreamingOutput
    - Process one by one
    - No collection in memory
  
  • When to use:
    - Reports
    - Exports
    - Bulk operations
-->

<!-- end_slide -->

---

## Mix-ins for Third-Party Classes

### Modify Classes You Don't Own

💡 **Hint**: Can't modify library classes? Use mix-ins!

<!-- pause -->

```java
// Third-party class we can't modify
public class ThirdPartyUser {
    public String getUsername() { ... }
    public String getInternalId() { ... }  // Don't want this
}

// Mix-in to control serialization
public abstract class ThirdPartyUserMixIn {
    @JsonProperty("user_name")
    abstract String getUsername();
    
    @JsonIgnore
    abstract String getInternalId();
}
```

<!-- pause -->

```java
// Register in ObjectMapper
mapper.addMixIn(ThirdPartyUser.class, ThirdPartyUserMixIn.class);
```

<!--
speaker_note: |
  MIX-INS
  
  • Problem solved:
    - Library classes
    - Generated code
    - Legacy systems
  
  • How it works:
    - Abstract class
    - Matching methods
    - Annotations apply
  
  • Very powerful tool!
-->

<!-- end_slide -->

---

## Testing Your Implementation

### Test Different Views

```bash
# Public view - minimal fields
curl http://localhost:8080/api/users/1 | jq .

# Internal view - more fields
curl http://localhost:8080/api/users/1/details \
  -H "Authorization: Bearer $TOKEN" | jq .

# Admin view - all fields except password
curl http://localhost:8080/api/users/1/admin \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq .
```

<!-- pause -->

### Test Streaming

```bash
# Stream all users efficiently
curl http://localhost:8080/api/users/stream | jq .
```

<!--
speaker_note: |
  TESTING
  
  • Verify views:
    - Different field counts
    - Proper hierarchy
    - No password ever
  
  • Check streaming:
    - Large datasets
    - Memory usage
    - Performance
-->

<!-- end_slide -->

---

## ⏰ Checkpoint: 15 Minutes

You should have:

✅ Money serializer/deserializer working  
✅ Money showing amount, currency, formatted  
✅ View hierarchy defined  
✅ Fields annotated with views  

<!-- pause -->

### Quick Validation

```bash
# Test Money serialization
curl http://localhost:8080/api/users/1 | jq '.account_balance'

# Should see:
# {
#   "amount": 1250.50,
#   "currency": "EUR",
#   "formatted": "€ 1,250.50"
# }
```

<!--
speaker_note: |
  15 MINUTE CHECK
  
  • Progress check:
    - Custom serializers done?
    - Views understood?
    - Any blockers?
  
  • Help needed:
    - Pair struggling students
    - Provide snippets
    - Keep momentum
-->

<!-- end_slide -->

---

## ⏰ Final Checkpoint: 25 Minutes

Complete solution includes:

✅ All custom serializers tested  
✅ Views showing different field sets  
✅ Streaming endpoint working  
✅ Mix-ins configured (bonus)  
✅ All tests passing  

<!-- pause -->

### Run Full Test Suite

```bash
./gradlew test --tests "*Advanced*"
```

<!--
speaker_note: |
  FINAL CHECK
  
  • Everyone ready?
    - Tests passing?
    - Concepts clear?
    - Questions?
  
  • Quick recap:
    - Why these features
    - When to use
    - Production value
-->

<!-- end_slide -->

---

## Common Pitfalls ⚠️

### 1. Forgetting Null Checks

```java
// WRONG - NullPointerException!
public void serialize(Money value, ...) {
    gen.writeNumberField("amount", value.getAmount());
}

// RIGHT - Handle null
public void serialize(Money value, ...) {
    if (value == null) {
        gen.writeNull();
        return;
    }
    // ... rest of serialization
}
```

<!-- pause -->

### 2. View Hierarchy Wrong

```java
// WRONG - No inheritance
public static class Internal { }

// RIGHT - Extends parent
public static class Internal extends Public { }
```

<!--
speaker_note: |
  COMMON MISTAKES
  
  • Null handling:
    - Always check
    - Write null explicitly
    - Return early
  
  • View inheritance:
    - Must extend
    - Cumulative visibility
    - Test all levels
-->

<!-- end_slide -->

---

## Bonus Challenges 🏆

### For Advanced Students

1. **Conditional Serialization** (10 min)
   - Include fields based on user permissions
   - Use SerializerProvider context

2. **@Masked Annotation** (15 min)
   - Create custom annotation
   - Mask credit cards: `4111****1111`
   - Apply via custom serializer

3. **JsonNode Manipulation** (10 min)
   - Parse arbitrary JSON
   - Modify tree programmatically
   - Convert back to POJO

<!--
speaker_note: |
  BONUS TASKS
  
  • Guide advanced students:
    - Custom annotations
    - Context-aware serialization
    - Tree manipulation
  
  • Keep engaged:
    - Real-world scenarios
    - Production patterns
    - Advanced techniques
-->

<!-- end_slide -->

---

## Key Takeaways 🎯

<!-- pause -->

✅ **Custom serializers handle complex business types** (Money, Distance)

<!-- pause -->

✅ **JSON Views provide role-based field visibility** without duplication

<!-- pause -->

✅ **Streaming prevents OutOfMemoryError** for large datasets

<!-- pause -->

✅ **Mix-ins modify third-party classes** you can't change

<!-- pause -->

✅ **Always handle null** in custom serializers!

<!--
speaker_note: |
  TAKEAWAYS
  
  • Reinforce value:
    - Production skills
    - Real problems solved
    - Professional level
  
  • Connect to career:
    - Valuable knowledge
    - Differentiator
    - Senior-level skills
-->

<!-- end_slide -->

---

## Production Considerations

### When to Use These Features

**Custom Serializers:**
- Complex business types
- Legacy API compatibility  
- Special formatting needs

<!-- pause -->

**JSON Views:**
- Public APIs with multiple consumers
- Role-based access control
- Mobile vs Web clients

<!-- pause -->

**Streaming:**
- Reports and exports
- Large datasets (>1000 items)
- Memory-constrained environments

<!--
speaker_note: |
  PRODUCTION CONTEXT
  
  • Real-world usage:
    - Not always needed
    - Right tool for job
    - Performance trade-offs
  
  • Decision factors:
    - Team knowledge
    - Maintenance cost
    - Actual requirements
-->

<!-- end_slide -->

---

## What's Next? 🚀

### This Afternoon: Security Implementation

- Basic Authentication
- JWT tokens
- API keys
- Security best practices

<!-- pause -->

### You're Ready For:

✅ Production JSON APIs  
✅ Complex data handling  
✅ Performance optimization  
✅ Professional development  

<!-- pause -->

**Great job mastering Jackson!** 💪

<!--
speaker_note: |
  WRAP UP AND TRANSITION
  
  • Celebrate success:
    - Complex topics mastered
    - Production skills gained
    - Well done!
  
  • Preview afternoon:
    - Security critical
    - Build on morning
    - More hands-on
  
  • Energy check:
    - Lunch break?
    - Questions?
    - Ready to continue?
-->

<!-- end_slide -->