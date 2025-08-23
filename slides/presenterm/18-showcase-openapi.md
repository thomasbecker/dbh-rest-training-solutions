---
title: "Showcase: OpenAPI & Swagger Documentation"
author: Thomas Becker
theme:
  name: dark
---

# OpenAPI & Swagger Documentation

## Auto-Generated API Documentation

Duration: 15 minutes

<!--
speaker_note: |
  This is a showcase demonstration only.
  Show the value of API documentation.
  Focus on how it improves developer experience.
-->

<!-- end_slide -->

---

## What is OpenAPI?

### The Industry Standard

**OpenAPI Specification (OAS)**
- Machine-readable API description
- Language-agnostic format
- Formerly known as Swagger

<!-- pause -->

**Swagger** = Tools ecosystem
- Swagger UI - Interactive documentation
- Swagger Editor - Design APIs
- Swagger Codegen - Generate code

<!-- pause -->

```yaml
openapi: 3.0.0
info:
  title: User API
  version: 1.0.0
paths:
  /users:
    get:
      summary: List all users
      responses:
        '200':
          description: Success
```

<!--
speaker_note: |
  OpenAPI is the specification.
  Swagger is the tooling.
  Like Java (spec) vs JDK (implementation).
-->

<!-- end_slide -->

---

## Why API Documentation Matters

### For API Consumers
- 🎯 **Try it out** - Live API testing
- 📖 **Self-service** - No hand-holding needed
- 🔍 **Discoverable** - See all endpoints
- ✅ **Examples** - Request/response samples

<!-- pause -->

### For API Providers
- 🤖 **Auto-generated** - From code annotations
- 📝 **Single source** - Code is documentation
- 🔄 **Always current** - Updates with code
- 🌍 **Client generation** - Auto-create SDKs

<!--
speaker_note: |
  Emphasize the "try it out" feature.
  Developers love interactive documentation.
  Reduces support burden significantly.
-->

<!-- end_slide -->

---

## Jersey + Swagger Integration

### Add Dependencies (Gradle)

```gradle
dependencies {
    // Swagger Core for annotations
    implementation 'io.swagger.core.v3:swagger-jaxrs2:2.2.8'
    implementation 'io.swagger.core.v3:swagger-jaxrs2-servlet-initializer:2.2.8'
    
    // Swagger UI for interactive docs
    implementation 'org.webjars:swagger-ui:4.15.5'
}
```

<!-- pause -->

### Configure in Jersey

```java
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Register Swagger
        register(OpenApiResource.class);
        
        // Configure Swagger
        SwaggerConfiguration config = new SwaggerConfiguration()
            .openAPI(new OpenAPI()
                .info(new Info()
                    .title("DBH REST API")
                    .version("1.0.0")
                    .description("Training API")));
        
        register(new SwaggerContextService().withConfig(config));
    }
}
```

<!--
speaker_note: |
  Simple setup - just add dependencies and configure.
  Works automatically with JAX-RS annotations.
  No code changes needed in resources.
-->

<!-- end_slide -->

---

## Annotating Your Resources

### Basic Annotations

```java
@Path("/users")
@Tag(name = "User Management", 
     description = "Operations for managing users")
public class UserResource {
    
    @GET
    @Operation(summary = "Get all users",
               description = "Returns a list of all users in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", 
                    description = "Success",
                    content = @Content(schema = @Schema(
                        implementation = User[].class))),
        @ApiResponse(responseCode = "403", 
                    description = "Not authorized")
    })
    public Response getUsers() {
        return Response.ok(users).build();
    }
}
```

<!--
speaker_note: |
  Annotations are optional but valuable.
  Even without them, basic docs are generated.
  With them, you get rich documentation.
-->

<!-- end_slide -->

---

## Documenting Request Bodies

```java
@POST
@Operation(summary = "Create a new user")
@RequestBody(
    description = "User to create",
    required = true,
    content = @Content(
        schema = @Schema(implementation = User.class),
        examples = @ExampleObject(
            name = "New User",
            value = """
                {
                  "username": "johndoe",
                  "email": "john@example.com",
                  "firstName": "John",
                  "lastName": "Doe"
                }
                """
        )
    )
)
public Response createUser(@Valid User user) {
    // Create user logic
    return Response.status(201)
        .entity(user)
        .build();
}
```

<!-- pause -->

**Pro tip**: Examples make APIs easier to understand!

<!--
speaker_note: |
  Examples are incredibly valuable.
  They show exactly what to send.
  Reduces trial and error for consumers.
-->

<!-- end_slide -->

---

## Documenting Models

### Schema Annotations

```java
@Schema(description = "User representation")
public class User {
    
    @Schema(description = "Unique identifier",
            example = "123",
            readOnly = true)
    private Long id;
    
    @Schema(description = "Username for login",
            example = "johndoe",
            required = true,
            minLength = 3,
            maxLength = 50)
    @NotNull
    @Size(min = 3, max = 50)
    private String username;
    
    @Schema(description = "Email address",
            example = "john@example.com",
            pattern = "^[A-Za-z0-9+_.-]+@(.+)$")
    @Email
    private String email;
}
```

<!-- pause -->

Bean Validation annotations are **automatically** included!

<!--
speaker_note: |
  Schema annotations enhance model documentation.
  Bean Validation annotations are picked up automatically.
  Creates comprehensive documentation.
-->

<!-- end_slide -->

---

## Swagger UI Demo

### Interactive API Explorer

```
http://localhost:8080/swagger-ui/
```

**Live Features:**
- 📋 List all endpoints
- 🔍 See request/response schemas  
- 🎯 Try requests directly
- 🔐 Test authentication
- 📥 Download OpenAPI spec

<!-- pause -->

*[Instructor shows live Swagger UI]*

<!--
speaker_note: |
  Live demo time - show actual Swagger UI.
  Demonstrate:
  1. Browsing endpoints
  2. Trying a GET request
  3. Creating a resource with POST
  4. Seeing validation errors
  Keep it under 3 minutes.
-->

<!-- end_slide -->

---

## Advanced Features

### API Versioning in OpenAPI

```java
@Path("/v1/users")
@Tag(name = "Users v1 (Deprecated)")
@Deprecated
public class UserResourceV1 {
    
    @GET
    @Operation(summary = "Get users (deprecated)",
               deprecated = true)
    public Response getUsers() {
        // V1 implementation
    }
}

@Path("/v2/users")
@Tag(name = "Users v2")
public class UserResourceV2 {
    
    @GET
    @Operation(summary = "Get users with pagination")
    public Response getUsers(
        @QueryParam("page") @DefaultValue("1") 
        @Parameter(description = "Page number") int page,
        @QueryParam("size") @DefaultValue("10")
        @Parameter(description = "Page size") int size) {
        // V2 implementation
    }
}
```

<!--
speaker_note: |
  Show how versioning appears in documentation.
  Deprecated endpoints are clearly marked.
  Helps with API evolution.
-->

<!-- end_slide -->

---

## Security Documentation

### Document Authentication Requirements

```java
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Enter JWT token"
)
@OpenAPIDefinition(
    info = @Info(title = "Secure API"),
    security = @SecurityRequirement(name = "bearerAuth")
)
public class JerseyConfig extends ResourceConfig {
    // Configuration
}
```

<!-- pause -->

```java
@GET
@Path("/admin")
@Operation(
    summary = "Admin only endpoint",
    security = @SecurityRequirement(name = "bearerAuth")
)
@RolesAllowed("ADMIN")
public Response getAdminData() {
    return Response.ok(data).build();
}
```

<!--
speaker_note: |
  Security requirements are clearly documented.
  Users know which endpoints need authentication.
  Can test with tokens directly in Swagger UI.
-->

<!-- end_slide -->

---

## API-First vs Code-First

### Code-First (What we've seen)
```
Code → Annotations → OpenAPI → Documentation
```
- Write code, add annotations
- Documentation generated from code
- Good for existing projects

<!-- pause -->

### API-First (Design-driven)
```
OpenAPI YAML → Code Generation → Implementation
```
- Design API in OpenAPI first
- Generate server stubs and clients
- Ensures contract compliance

<!-- pause -->

**Which is better?** Depends on your workflow!

<!--
speaker_note: |
  No right answer here.
  Code-first is easier to start.
  API-first better for large teams.
  Contract-first prevents breaking changes.
-->

<!-- end_slide -->

---

## Client SDK Generation

### Generate Java Client

```bash
# Using OpenAPI Generator
openapi-generator generate \
  -i http://localhost:8080/openapi.json \
  -g java \
  -o ./generated-client \
  --library jersey2
```

<!-- pause -->

### Generated Client Usage

```java
// Auto-generated client code
ApiClient apiClient = new ApiClient();
apiClient.setBasePath("http://localhost:8080/api");

UserApi userApi = new UserApi(apiClient);

// Type-safe API calls!
List<User> users = userApi.getUsers();
User newUser = userApi.createUser(
    new User()
        .username("johndoe")
        .email("john@example.com")
);
```

<!--
speaker_note: |
  Huge time saver for API consumers.
  Type-safe clients in multiple languages.
  Reduces integration errors.
-->

<!-- end_slide -->

---

## Best Practices

### DO ✅

- **Write clear summaries** - One line, action-oriented
- **Provide examples** - Real-world data
- **Document errors** - All possible responses
- **Version your API** - Use semantic versioning
- **Include contact info** - Support channels

<!-- pause -->

### DON'T ❌

- **Over-annotate** - JAX-RS already provides info
- **Duplicate descriptions** - DRY principle
- **Expose internals** - Hide implementation details
- **Forget updates** - Keep docs current

<!--
speaker_note: |
  Good documentation is an art.
  Balance between comprehensive and readable.
  Focus on what consumers need to know.
-->

<!-- end_slide -->

---

## Integration with CI/CD

### Automated Documentation Pipeline

```yaml
# .gitlab-ci.yml or .github/workflows/api-docs.yml
stages:
  - build
  - document
  - deploy

generate-docs:
  stage: document
  script:
    - ./gradlew generateOpenApiDocs
    - openapi-generator generate -i openapi.json -g html2
    - deploy-to-docs-site ./docs
```

<!-- pause -->

### Breaking Change Detection

```bash
# Compare OpenAPI specs
openapi-diff \
  old-openapi.json \
  new-openapi.json \
  --fail-on-breaking
```

Fail build if breaking changes detected!

<!--
speaker_note: |
  Automation is key for documentation.
  Treat docs as code - version, test, deploy.
  Catch breaking changes early.
-->

<!-- end_slide -->

---

## Demo: Live Documentation

### What I'll Show:

1. 📝 Add Swagger annotations to our UserResource
2. 🚀 Start the application
3. 🌐 Open Swagger UI
4. 🎯 Try creating a user
5. ❌ See validation errors
6. ✅ Successful request with valid data

<!-- pause -->

*[Instructor performs live demonstration]*

<!--
speaker_note: |
  Quick 3-minute demo.
  Focus on the interactive aspects.
  Show how it helps during development.
-->

<!-- end_slide -->

---

## Real-World Benefits

### Case Study: API Adoption

**Before OpenAPI:**
- 📧 50+ support emails/week
- 📞 Onboarding calls required
- 🐛 Integration bugs common
- ⏱️ 2-week integration time

<!-- pause -->

**After OpenAPI:**
- 📧 5 support emails/week
- 🤖 Self-service onboarding
- ✅ Fewer integration issues
- ⏱️ 2-day integration time

<!-- pause -->

**90% reduction in support burden!**

<!--
speaker_note: |
  Real numbers from actual projects.
  Good documentation pays for itself.
  Developers prefer self-service.
-->

<!-- end_slide -->

---

## Resources for Self-Study

### 📚 Essential Links

- **OpenAPI Specification**: [spec.openapis.org](https://spec.openapis.org)
- **Swagger Tools**: [swagger.io](https://swagger.io)
- **OpenAPI Generator**: [openapi-generator.tech](https://openapi-generator.tech)

### 🛠️ Useful Tools

- **Swagger Editor** - Design APIs visually
- **Postman** - Import OpenAPI specs
- **Insomnia Designer** - API design tool
- **Stoplight Studio** - Visual API designer

### 💡 Next Steps

1. Add Swagger to existing project
2. Document one resource completely
3. Generate a client SDK
4. Set up CI/CD integration

<!--
speaker_note: |
  Provide actionable next steps.
  Start small with one resource.
  Build confidence before full adoption.
-->

<!-- end_slide -->

---

## Key Takeaways

### Remember:

- 📖 **Documentation as Code** - Lives with your source
- 🤖 **Auto-generated** - From annotations
- 🎯 **Interactive** - Try before integrate
- 🔄 **Always current** - Updates with code

<!-- pause -->

### Consider OpenAPI when:

- Building public APIs
- Multiple client teams
- Microservices architecture
- API-first development

<!-- pause -->

**Questions about OpenAPI/Swagger?**

<!--
speaker_note: |
  Quick recap of main benefits.
  Check for questions but watch time.
  Transition to container showcase next.
-->

<!-- end_slide -->