# Instructor Notes: Exercise 07 - Jackson Advanced

## Overview
This exercise covers advanced Jackson features: custom serializers/deserializers, JSON Views for role-based field visibility, and performance optimization through streaming. Students apply these to real-world scenarios.

## Time Management
- **Total Time**: 30 minutes
- Task 1 (Custom Serializers): 10 minutes
- Task 2 (JSON Views): 10 minutes  
- Task 3 (Streaming): 10 minutes

## Key Learning Points

### 1. Custom Serializers
- When annotations aren't enough
- Complex business logic in serialization
- Handling special types (Money, dates, etc.)

### 2. JSON Views
- Role-based field visibility
- Single model, multiple representations
- Hierarchical view inheritance

### 3. Performance
- Streaming for large datasets
- Memory efficiency
- Mix-ins for third-party classes

## Common Issues and Solutions

### Issue 1: Serializer Not Called
**Problem**: Custom serializer ignored
**Solution**: 
```java
// Must register on field or in ObjectMapper
@JsonSerialize(using = MoneySerializer.class)
private Money accountBalance;

// OR globally:
SimpleModule module = new SimpleModule();
module.addSerializer(Money.class, new MoneySerializer());
mapper.registerModule(module);
```

### Issue 2: View Inheritance Confusion
**Problem**: Fields not showing in extended views
**Solution**:
```java
// Internal extends Public - gets both
public static class Internal extends Public { }

// Field will show in Internal AND Admin views
@JsonView(Views.Public.class)
private String username;
```

### Issue 3: Streaming Errors
**Problem**: IOException in forEach lambda
**Solution**:
```java
// Wrap in RuntimeException or use traditional for loop
userService.streamAll().forEach(user -> {
    try {
        generator.writeObject(user);
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
});
```

## Teaching Tips

### Explain the Why
"Custom serializers solve real problems:
- Money needs special formatting
- Dates need timezone handling
- Legacy systems expect specific formats
- Security requires field masking"

### Live Demonstration Sequence

1. **Show Problem First**
   - Try to serialize Money without custom serializer
   - Show ugly default output
   
2. **Build Solution Step-by-Step**
   - Create serializer
   - Show improved output
   - Add deserializer for round-trip

3. **Demonstrate Views**
   - Same endpoint, different outputs
   - Show security benefits

### Debugging Techniques
```java
// Show how to test serializers in isolation
ObjectMapper mapper = new ObjectMapper();
SimpleModule module = new SimpleModule();
module.addSerializer(Money.class, new MoneySerializer());
mapper.registerModule(module);

Money money = new Money("100.50", "USD");
String json = mapper.writeValueAsString(money);
System.out.println(json);
```

## Progressive Difficulty

### Basic (First 10 min)
- Simple custom serializer
- Basic JsonNode usage
- Field-level registration

### Intermediate (Next 10 min)
- JSON Views with hierarchy
- Multiple representations
- Role-based visibility

### Advanced (Final 10 min)
- Streaming API
- Performance considerations
- Mix-ins for external classes

## Code Review Points

### What to Look For:
1. **Null Handling**: Serializers handle null gracefully
2. **View Hierarchy**: Proper inheritance structure
3. **Resource Cleanup**: Streams/generators closed properly
4. **Error Handling**: IOExceptions handled
5. **Performance**: Not loading entire collections

### Red Flags:
- No null checks in serializers
- Views without inheritance
- Unclosed resources
- Loading all data for streaming
- Missing deserializers

## Sample Solution Key Points

```java
// Money Serializer - Key pattern
public void serialize(Money value, JsonGenerator gen, ...) {
    if (value == null) {
        gen.writeNull();  // IMPORTANT: Handle null
        return;
    }
    gen.writeStartObject();
    // ... write fields ...
    gen.writeEndObject();
}

// View hierarchy - Correct setup
public static class Public { }
public static class Internal extends Public { }  // Inherits!
public static class Admin extends Internal { }   // Gets all!

// Streaming - Memory efficient
generator.writeStartArray();
// Process one at a time, don't collect
userService.streamAll().forEach(user -> {
    generator.writeObject(user);
});
generator.writeEndArray();
```

## Talking Points for Wrap-up

1. **When to Use Custom Serializers:**
   - Complex business types (Money, Distance, Duration)
   - Legacy API compatibility
   - Special formatting requirements
   - Security (masking, encryption)

2. **View Strategy:**
   - Public: Minimal information
   - Internal: User's own data
   - Admin: Full visibility (except secrets)
   - Consider GraphQL for ultimate flexibility

3. **Performance at Scale:**
   - Streaming prevents OutOfMemoryError
   - Pagination as alternative
   - Caching strategies
   - Async processing options

## Quick Checks

Ask students:
1. "When would you use a custom serializer?" (Complex types, special formatting)
2. "How do views inherit?" (Extends parent class)
3. "Why use streaming?" (Memory efficiency for large data)
4. "What are mix-ins for?" (Modifying third-party classes)

## Extension Ideas

If students finish early:
1. Create serializer for credit card masking
2. Implement bi-directional currency conversion
3. Add caching to expensive serialization
4. Create custom annotation processor

## Connection to Day 2 Topics

"With Jackson mastered, this afternoon we'll see:
- How to secure these endpoints
- Document with OpenAPI
- Deploy to production
- Real-world integration patterns"

## Common Misconceptions

1. **"Custom serializers are slow"**
   - Actually can be faster than reflection
   - Cache expensive computations

2. **"Views are only for security"**
   - Also great for API versioning
   - Mobile vs web clients
   - Performance optimization

3. **"Streaming is always better"**
   - Only for large datasets
   - Adds complexity
   - Consider pagination first

## Performance Comparison

Show students the difference:

```java
// Bad: Loads everything
List<User> all = userService.findAll();  // 10,000 users = OOM
return Response.ok(all).build();

// Good: Streams one at a time
StreamingOutput stream = output -> {
    // Process individually
};
return Response.ok(stream).build();
```

## Notes for Remote Training

- Prepare JSON samples for each view
- Show memory profiler if possible
- Have performance metrics ready
- Use online JSON formatter for clarity

## Assessment Questions

1. Create a DistanceSerializer for kilometers/miles
2. Implement view for partner API (different fields)
3. Stream CSV instead of JSON
4. Add compression to streaming endpoint

## Wrap-up Message

"You now have professional-level Jackson skills:
- Control exact JSON structure
- Handle complex business types
- Optimize for performance
- Provide role-based responses

This is production-ready knowledge!"