# Instructor Notes: Exercise 06 - Jackson Basics

## Overview
This exercise introduces Jackson JSON processing library integration with Jersey. Students configure Jackson, use annotations to control serialization, and handle complex object structures.

## Time Management
- **Total Time**: 45 minutes
- Task 1 (Configuration): 10 minutes
- Task 2 (User DTO): 15 minutes  
- Task 3 (Nested Objects): 10 minutes
- Task 4 (Testing): 10 minutes

## Key Learning Points

### 1. Jackson Configuration
- Jersey requires explicit Jackson feature registration
- ObjectMapper needs configuration for Java 8 time types
- Global settings vs per-field annotations

### 2. Common Annotations
- `@JsonProperty`: Field name mapping
- `@JsonIgnore`: Security (passwords)
- `@JsonFormat`: Date/time formatting
- `@JsonInclude`: Null handling

### 3. Testing JSON
- Test both serialization and deserialization
- Verify security (no passwords in output)
- Check field name transformations

## Common Issues and Solutions

### Issue 1: Jackson Not Working
**Problem**: JSON not being produced/consumed
**Solution**: 
```java
// Must register JacksonFeature
register(JacksonFeature.class);
```

### Issue 2: Date Serialization Issues
**Problem**: Dates appear as timestamps or fail
**Solution**:
```java
mapper.registerModule(new JavaTimeModule());
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
```

### Issue 3: Field Names Not Changing
**Problem**: @JsonProperty not working
**Solution**: Ensure Jackson is actually being used (not MOXy or other provider)

### Issue 4: Null Values in JSON
**Problem**: Unwanted null fields in output
**Solution**:
```java
mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
// Or per-class:
@JsonInclude(JsonInclude.Include.NON_NULL)
```

## Teaching Tips

### Start with Why
"Jackson gives us fine-grained control over JSON structure. This matters because:
- API contracts must be stable
- Security requires hiding sensitive data
- Different clients need different formats"

### Live Demonstration
1. Show JSON output BEFORE Jackson configuration
2. Add Jackson and show immediate improvement
3. Add annotations one by one, showing changes

### Debugging Technique
```java
// Show students how to debug serialization
ObjectMapper mapper = new ObjectMapper();
mapper.enable(SerializationFeature.INDENT_OUTPUT);
String json = mapper.writeValueAsString(user);
System.out.println(json);
```

## Progressive Difficulty

### Basic (First 20 min)
- Simple annotations (@JsonProperty, @JsonIgnore)
- Basic date formatting
- Field renaming

### Intermediate (Next 15 min)
- Nested objects (Address)
- Enum handling
- Collections

### Advanced (Final 10 min)
- Custom serializers (bonus)
- JSON Views (bonus)
- Polymorphic types (bonus)

## Code Review Points

### What to Look For:
1. **Security**: Password field must be @JsonIgnore
2. **Consistency**: All date fields formatted uniformly
3. **Naming**: Snake_case in JSON, camelCase in Java
4. **Null Handling**: Configured appropriately
5. **Testing**: Both directions tested

### Red Flags:
- Passwords visible in JSON
- Timestamps instead of formatted dates
- Missing Jackson registration
- No tests for deserialization

## Sample Solution Structure

```
instructor-solution/
├── src/main/java/com/dbh/training/rest/
│   ├── config/
│   │   ├── JerseyConfig.java           # Jackson registration
│   │   └── ObjectMapperContextResolver.java
│   ├── models/
│   │   ├── User.java                   # With annotations
│   │   ├── Address.java
│   │   └── AccountStatus.java
│   └── resources/
│       └── UserResource.java
└── src/test/java/
    └── JacksonIntegrationTest.java
```

## Talking Points for Wrap-up

1. **Why Jackson over alternatives?**
   - De facto standard
   - Excellent annotation support
   - Great performance
   - Active development

2. **Production Considerations:**
   - Version compatibility
   - Security (never expose internal fields)
   - Performance (streaming for large data)
   - Documentation of JSON structure

3. **What's Next:**
   - Custom serializers (Exercise 07)
   - JSON Views for different clients
   - Performance optimization
   - Error handling

## Quick Checks

Ask students:
1. "What annotation hides sensitive fields?" (@JsonIgnore)
2. "How do we rename fields in JSON?" (@JsonProperty)
3. "What module handles LocalDate?" (JavaTimeModule)
4. "Why disable WRITE_DATES_AS_TIMESTAMPS?" (Human readability)

## Extension Ideas

If students finish early:
1. Add validation messages to JSON error responses
2. Implement custom date serializer
3. Create different DTOs for input vs output
4. Add HATEOAS links to responses

## Connection to Next Exercise

"Now that we can control basic JSON structure, Exercise 07 will show advanced features:
- Custom serializers for complex logic
- JSON Views for different representations
- Performance optimization techniques"

## Common Misconceptions

1. **"Jackson is only for JSON"**
   - Also handles XML, YAML, CSV, etc.

2. **"Annotations are the only way"**
   - Can configure programmatically
   - Mix-ins for third-party classes

3. **"More annotations = better"**
   - Keep it simple
   - Use global config when possible

## Notes for Remote Training

- Screen share JSON output frequently
- Use online JSON validators for students
- Provide pre-configured ObjectMapper code
- Have backup JSON test data ready