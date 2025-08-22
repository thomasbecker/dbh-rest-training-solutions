# Exercise 02: Jersey Project Setup

**Duration**: 15-20 minutes  
**Objective**: Set up a basic Jersey project from scratch

## Prerequisites

- Completed Exercise 01 (REST Basics)
- Java 8 installed
- Gradle installed
- IDE ready

## Your Task

Create a basic Jersey project with the following:

1. **Create Main Application Class** (5 min)
   - Set up embedded Jetty server
   - Configure Jersey servlet
   - Start server on port 8080

2. **Create Jersey Configuration** (5 min)
   - Package scanning for resources
   - Register Jackson for JSON
   - Enable CORS filter

3. **Create Health Check Endpoint** (5 min)
   - Simple GET /health endpoint
   - Return JSON with status "UP"

4. **Test Your Setup** (5 min)
   - Start the server
   - Test with curl or browser
   - Verify JSON response

## Expected Structure

```
src/main/java/com/dbh/training/rest/
├── Application.java
├── config/
│   └── JerseyConfig.java
├── resources/
│   └── HealthResource.java
└── filters/
    └── CORSFilter.java
```

## Testing Your Setup

```bash
# Start the server
./gradlew run

# In another terminal
curl http://localhost:8080/api/health

# Should return:
{"status": "UP", "timestamp": "2025-08-26T10:00:00"}
```

## Hints

💡 Use the starter-project as reference but type it yourself to learn

💡 Remember to add `/api` as your application path

💡 Don't forget the @Path annotation on your resource

## Common Issues

❌ **404 Not Found** - Check your servlet mapping and @Path

❌ **No JSON** - Did you register JacksonFeature?

❌ **Port already in use** - Stop other servers or change port

## Success Criteria

✅ Server starts without errors

✅ GET /api/health returns JSON

✅ CORS headers are present in response

✅ Logs show Jersey initialized

## Need Help?

If you get stuck:
1. Check the starter-project for reference
2. Review the Jersey Setup slides
3. Ask your instructor