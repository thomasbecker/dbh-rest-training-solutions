# Instructor Notes for Exercise 02

## Presentation Slides

Present the exercise using:
```bash
presenterm ../../slides/presenterm/02-jersey-crud.md
```

The slides cover:
- Step-by-step implementation guide
- Common pitfalls
- Testing strategies
- Bonus challenges

## Important Setup Requirements

### Java Version
- **Required**: Java 8 
- The project is configured for Java 8 compatibility
- Participants should have Java 8 installed
- If testing locally with newer Java versions, you may need to adjust Gradle wrapper version

### Pre-Exercise Setup
1. Ensure participants have the starter-project set up
2. Verify they can run: `./gradlew build`
3. Have them start the server: `./gradlew run`
4. Verify server is running: `curl http://localhost:8080/api/example`

### TDD Workflow
1. Participants start with skeleton UserResource.java
2. All methods return 501 "Not Implemented"
3. They run tests: `./gradlew test --tests UserResourceTest`
4. Tests will fail (RED phase)
5. They implement each method to make tests pass (GREEN phase)
6. Optionally refactor (REFACTOR phase)

### Time Management
- Total: 45-50 minutes
- User Model: 5 min (already provided to save time)
- GET all users: 15 min
- POST create user: 10 min
- PUT update user: 10 min
- DELETE user: 5 min
- Testing/debugging: 5-10 min

### Common Issues

1. **Test Connection Refused**
   - Server must be running: `./gradlew run`
   - Check port 8080 is free

2. **JSON Parsing Errors**
   - Ensure User has default constructor
   - Check Jackson annotations if used

3. **404 on Valid Endpoints**
   - Check @Path annotations
   - Verify Application class registers UserResource

4. **Concurrent Modification**
   - Use ConcurrentHashMap (already provided)
   - Use AtomicLong for ID generation (already provided)

### Fast Learners
Direct them to bonus tasks in order:
1. Pagination (adds query parameters)
2. Filtering (username search)
3. PATCH (partial updates)
4. Search (more complex queries)

### Teaching Points
- Emphasize RESTful conventions (proper status codes)
- Discuss thread safety with concurrent collections
- Show how REST Assured makes testing APIs easy
- Mention that validation will be added in Exercise 03

### Solution Walkthrough
If needed, the complete solution is in:
`instructor-solution/src/main/java/com/dbh/training/rest/resources/UserResource.java`

Key points to highlight:
- Proper use of Response builders
- Status codes (200, 201, 204, 404)
- Location header for POST
- Null checks for 404 responses