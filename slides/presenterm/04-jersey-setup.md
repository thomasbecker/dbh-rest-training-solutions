---
title: Plain Java with Jersey
sub_title: Building REST APIs without Spring Boot
author: Thomas Becker
date: August 2025
---

# Plain Java with Jersey

Building REST APIs without Spring Boot

Duration: 60 minutes

<!--
speaker_note: |
  AFTERNOON SESSION START (2 minutes)

  • Welcome back from lunch:
    - "Hope everyone had a good break"
    - "Ready for hands-on coding?"
    - Quick energy check

  • Afternoon overview:
    - Jersey setup (60 min)
    - CRUD exercise (75 min)
    - Validation (30 min)
    - Versioning (45 min)
    - Spring Boot comparison (30 min)

  • This module:
    - Understanding Jersey framework
    - No Spring Boot magic
    - Foundation for exercises
-->

<!-- end_slide -->

## Agenda

1. What is Jersey?
1. Jersey vs Spring Boot
1. Project Setup
1. Core Dependencies
1. Server Configuration
1. Application Structure
1. Configuration Patterns

<!--
speaker_note: |
  MODULE OVERVIEW (1 minute)

  • What we'll cover:
    - Jersey as JAX-RS implementation
    - Comparison with Spring Boot
    - Hands-on setup
    - Real configuration

  • Key message:
    - "Less magic, more control"
    - "Understand what's happening"
    - "Better for microservices"

  • Check:
    - "Everyone has Java 8?"
    - "Gradle installed?"
    - "IDE ready?"
-->

<!-- end_slide -->

## What is Jersey?

JAX-RS Reference Implementation

<!-- pause -->

### Key Points

- **Reference Implementation** of JAX-RS (Java API for RESTful Web Services)
- **Production-ready** framework used by many enterprises
- **Lightweight** - no heavy framework overhead
- **Standard-based** - follows JAX-RS specifications

<!-- pause -->

### History

- Developed by Oracle/Sun
- Version 2.x for JAX-RS 2.0+ (Java 8 compatible)
- Version 3.x for Jakarta EE (requires Java 11+)

<!--
speaker_note: |
  WHAT IS JERSEY (5 minutes)

  • Historical context:
    - Sun created JAX-RS spec
    - Jersey = reference implementation
    - Like Hibernate for JPA
    - Industry standard since 2008

  • Version clarity:
    - 2.x = Java 8 compatible (us)
    - 3.x = Jakarta EE, Java 11+
    - Most enterprises on 2.x

  • Who uses Jersey:
    - Netflix (parts of)
    - LinkedIn
    - Many banks (can't name)
    - Government systems

  • JAX-RS compliance:
    - Portable code
    - Standard annotations
    - Switch implementations easily
    - RESTEasy, Apache CXF alternatives

  • Ask audience:
    - "Anyone used Jersey before?"
    - "Currently using Spring Boot?"
-->

<!-- end_slide -->

## Why Jersey without Spring Boot?

Understanding the fundamentals

<!-- pause -->

### Benefits of Plain Jersey

- **Lighter footprint** - only what you need
- **Faster startup** - no auto-configuration scanning
- **Full control** - explicit configuration
- **Better understanding** - see what's actually happening

<!-- pause -->

### When to Use

- Microservices with minimal dependencies
- Legacy Java 8 environments
- Learning JAX-RS standards
- Performance-critical applications

<!--
speaker_note: |
  WHY PLAIN JERSEY (4 minutes)

  • The reality check:
    - "Spring Boot is great, but..."
    - "Sometimes too much magic"
    - "Hard to debug when fails"
    - "Heavy for simple services"

  • Concrete benefits:
    - Startup: 1s vs 10s
    - Memory: 50MB vs 200MB
    - JAR size: 10MB vs 50MB
    - Docker images smaller

  • When it makes sense:
    - Microservices (many instances)
    - Lambdas/Functions
    - Embedded devices
    - Learning fundamentals

  • Java 8 reality:
    - "Many enterprises stuck on Java 8"
    - "Banking, insurance especially"
    - "Security validation takes years"
    - "If it works, don't upgrade"

  • Transition:
    - "Let's see the code difference"
-->

<!-- end_slide -->

## Jersey vs Spring Boot

Side-by-side comparison

<!--
speaker_note: |
  CODE COMPARISON (5 minutes)

  • Spring Boot side:
    - 10 lines of code
    - Lots of magic
    - @SpringBootApplication does 100 things
    - Great for fast start

  • Jersey side:
    - More explicit
    - See everything happening
    - No hidden behavior
    - Better for learning

  • Trade-offs:
    - Spring: Faster development
    - Jersey: Better understanding
    - Spring: More features
    - Jersey: More control

  • Real-world:
    - "Both are production-ready"
    - "Choice depends on needs"
    - "Can migrate later"
-->

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### Spring Boot

```java
@SpringBootApplication
@RestController
@RequestMapping("/api")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring";
    }
}
```

<!-- column: 1 -->

### Plain Jersey

```java
public class App {
    public static void main(String[] args) {
        Server server = new Server(8080);
        ServletContextHandler ctx = 
            new ServletContextHandler();
        ctx.addServlet(
            ServletContainer.class, "/*")
            .setInitParameter(
                "jersey.config.server.provider.packages",
                "com.example.resources");
        server.start();
    }
}
```

<!-- reset_layout -->

<!-- end_slide -->

## Core Dependencies

Essential libraries for Jersey

```gradle
dependencies {
    // Jersey Core
    implementation 'org.glassfish.jersey.core:jersey-server:2.35'
    implementation 'org.glassfish.jersey.containers:jersey-container-servlet:2.35'
    
    // JSON Support
    implementation 'org.glassfish.jersey.media:jersey-media-json-jackson:2.35'
    
    // Embedded Server (choose one)
    implementation 'org.glassfish.jersey.containers:jersey-container-jetty-http:2.35'
    // OR
    implementation 'org.glassfish.jersey.containers:jersey-container-grizzly2-http:2.35'
    
    // Bean Validation
    implementation 'org.glassfish.jersey.ext:jersey-bean-validation:2.35'
}
```

<!--
speaker_note: |
  CORE DEPENDENCIES (5 minutes)

  • Walk through each:

  • jersey-server:
    - Core JAX-RS implementation
    - Annotations, resources
    - Required always

  • jersey-container-servlet:
    - Servlet integration
    - Works with any servlet container
    - Bridge to HTTP

  • jersey-media-json-jackson:
    - JSON serialization
    - Jackson integration
    - Alternative: MOXy (but Jackson better)

  • Embedded server choice:
    - Jetty: More mature, more features
    - Grizzly: Simpler, lighter
    - Jetty recommended for production

  • Bean Validation:
    - JSR-303 support
    - @NotNull, @Size, etc.
    - We'll use in exercise

  • Version note:
    - "2.35 last for Java 8"
    - "2.39 also works"
    - "Don't use 3.x!"
-->

<!-- end_slide -->

## Server Options

Embedded servers for Jersey

<!-- pause -->

### Jetty

```java
Server server = new Server(8080);
ServletContextHandler context = new ServletContextHandler();
context.setContextPath("/");

ServletHolder jerseyServlet = context.addServlet(
    org.glassfish.jersey.servlet.ServletContainer.class, "/*");
jerseyServlet.setInitOrder(0);
jerseyServlet.setInitParameter(
    "jersey.config.server.provider.packages",
    "com.dbh.training.rest");

server.setHandler(context);
server.start();
```

<!-- pause -->

### Grizzly

```java
ResourceConfig config = new ResourceConfig()
    .packages("com.dbh.training.rest");

HttpServer server = GrizzlyHttpServerFactory
    .createHttpServer(URI.create("http://localhost:8080/"), config);
```

<!--
speaker_note: |
  SERVER OPTIONS (6 minutes)

  • Jetty deep dive:
    - "I'm a Jetty committer"
    - Used by Google App Engine
    - Eclipse Foundation project
    - Very stable, mature

  • Jetty setup explained:
    - Server(8080) - port binding
    - ServletContextHandler - web context
    - ServletHolder - Jersey bridge
    - provider.packages - where to scan

  • Grizzly alternative:
    - Oracle/GlassFish project
    - Simpler API
    - Good for simple cases
    - Less configuration options

  • Production considerations:
    - Jetty: thread pools, connectors
    - Grizzly: built-in NIO
    - Both production-ready

  • Recommendation:
    - "We'll use Jetty"
    - "More real-world"
    - "Better documentation"
-->

<!-- end_slide -->

## Project Structure

Standard layout for Jersey projects

<!--
speaker_note: |
  PROJECT STRUCTURE (4 minutes)

  • Explain each folder:

  • Application.java:
    - Main entry point
    - Server lifecycle
    - Keep minimal

  • config/:
    - JerseyConfig (ResourceConfig)
    - Jackson configuration
    - Any app config

  • resources/:
    - REST endpoints
    - One class per resource
    - UserResource, OrderResource

  • models/:
    - Domain entities
    - DTOs
    - Keep separate from DB

  • filters/:
    - Cross-cutting concerns
    - CORS, Logging, Auth
    - Applied to all requests

  • exceptions/:
    - Custom exceptions
    - Exception mappers
    - Error responses

  • This is standard:
    - "Used in many companies"
    - "Easy to navigate"
    - "Separation of concerns"
-->

```
project/
├── src/main/java/
│   └── com/dbh/training/rest/
│       ├── Application.java           # Main class with server
│       ├── config/
│       │   └── JerseyConfig.java     # Jersey configuration
│       ├── resources/                 # REST endpoints
│       │   ├── UserResource.java
│       │   └── ProductResource.java
│       ├── models/                    # Domain models
│       │   ├── User.java
│       │   └── Product.java
│       ├── filters/                   # Cross-cutting concerns
│       │   ├── CORSFilter.java
│       │   └── LoggingFilter.java
│       └── exceptions/                # Custom exceptions
│           └── NotFoundException.java
└── src/main/resources/
    └── application.properties
```

<!-- end_slide -->

## Jersey Configuration

ResourceConfig setup

<!--
speaker_note: |
  JERSEY CONFIGURATION (5 minutes)

  • ResourceConfig explained:
    - Central configuration point
    - Extends Jersey Application
    - Replaces web.xml

  • Package scanning:
    - packages() method
    - Finds @Path annotations
    - Recursive scanning
    - Keep packages focused

  • Component registration:
    - register() for specific classes
    - Filters, features, mappers
    - Order matters for filters

  • Jackson integration:
    - JacksonFeature.class
    - Auto-configures JSON
    - Can customize ObjectMapper

  • Validation:
    - ValidationFeature.class
    - Enables @Valid
    - Bean Validation support

  • Exception mappers:
    - Convert exceptions to HTTP
    - Custom error responses
    - Chain of responsibility

  • Best practice:
    - "One config class"
    - "Keep it simple"
    - "Document choices"
-->

```java
public class JerseyConfig extends ResourceConfig {
    
    public JerseyConfig() {
        // Package scanning
        packages("com.dbh.training.rest.resources");
        
        // Register individual components
        register(LoggingFilter.class);
        register(CORSFilter.class);
        
        // Enable JSON via Jackson
        register(JacksonFeature.class);
        
        // Enable Bean Validation
        register(ValidationFeature.class);
        
        // Custom exception mappers
        register(NotFoundExceptionMapper.class);
        register(ValidationExceptionMapper.class);
    }
}
```

<!-- speaker_note: ResourceConfig is central configuration point -->

<!-- end_slide -->

## Application Bootstrap

Main class structure

```java
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int PORT = 8080;
    
    public static void main(String[] args) throws Exception {
        Server server = createServer(PORT);
        
        try {
            server.start();
            logger.info("Jersey server started on port {}", PORT);
            logger.info("API available at http://localhost:{}/api", PORT);
            server.join();
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            server.stop();
        }
    }
    
    private static Server createServer(int port) {
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        
        ServletHolder jerseyServlet = new ServletHolder(ServletContainer.class);
        jerseyServlet.setInitParameter(
            "javax.ws.rs.Application", 
            "com.dbh.training.rest.config.JerseyConfig");
        
        context.addServlet(jerseyServlet, "/api/*");
        server.setHandler(context);
        
        return server;
    }
}
```

<!-- end_slide -->

## Configuration Management

Properties without Spring

<!-- pause -->

### Simple Properties Loading

```java
public class AppConfig {
    private static final Properties props = new Properties();
    
    static {
        try (InputStream is = AppConfig.class
                .getResourceAsStream("/application.properties")) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
    
    public static String get(String key) {
        return props.getProperty(key);
    }
    
    public static int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}
```

<!-- pause -->

### Usage

```java
int port = AppConfig.getInt("server.port", 8080);
String dbUrl = AppConfig.get("database.url");
```

<!-- end_slide -->

## Dependency Injection

CDI without Spring

<!-- pause -->

### Option 1: Manual Injection

```java
@Path("/users")
public class UserResource {
    private final UserService userService;
    
    public UserResource() {
        // Manual instantiation
        this.userService = new UserService(new UserRepository());
    }
}
```

<!-- pause -->

### Option 2: HK2 (Jersey's DI)

```java
public class AppBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(UserRepository.class);
        bind(UserService.class).to(UserService.class);
    }
}

// Register in JerseyConfig
register(new AppBinder());
```

<!-- speaker_note: HK2 is Jersey's built-in DI, simpler than Spring -->

<!-- end_slide -->

## Logging Configuration

SLF4J with Logback

<!-- pause -->

### Dependencies

```gradle
implementation 'org.slf4j:slf4j-api:1.7.36'
implementation 'ch.qos.logback:logback-classic:1.2.11'
```

<!-- pause -->

### logback.xml

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/application.log</file>
        <encoder>
            <pattern>%d{ISO8601} [%thread] %-5level %logger - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
    
    <logger name="com.dbh.training" level="DEBUG" />
    <logger name="org.glassfish.jersey" level="INFO" />
</configuration>
```

<!-- end_slide -->

## Environment Variables

Configuration best practices

<!-- pause -->

### Reading Environment Variables

```java
public class EnvConfig {
    
    public static String get(String key, String defaultValue) {
        // First check system property
        String value = System.getProperty(key);
        
        // Then environment variable
        if (value == null) {
            value = System.getenv(key.replace(".", "_").toUpperCase());
        }
        
        // Finally default
        return value != null ? value : defaultValue;
    }
}
```

<!-- pause -->

### Usage

```java
// Can be set via -Dserver.port=9090 or SERVER_PORT=9090
int port = Integer.parseInt(EnvConfig.get("server.port", "8080"));
```

<!-- speaker_note: Follows 12-factor app principles -->

<!-- end_slide -->

## Testing Setup

Integration testing without Spring Boot

```java
public abstract class BaseIntegrationTest {
    protected static Server server;
    protected static final int TEST_PORT = 8888;
    protected static final String BASE_URI = "http://localhost:" + TEST_PORT;
    
    @BeforeAll
    public static void startServer() throws Exception {
        server = Application.createServer(TEST_PORT);
        server.start();
        
        // Wait for server to be ready
        RestAssured.baseURI = BASE_URI;
        RestAssured.basePath = "/api";
    }
    
    @AfterAll
    public static void stopServer() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
    
    @BeforeEach
    public void setup() {
        RestAssured.reset();
    }
}
```

<!-- end_slide -->

## Common Pitfalls

What to watch out for

<!--
speaker_note: |
  COMMON PITFALLS (5 minutes)

  • These waste hours:

  • Missing Jackson:
    - "404 on all endpoints"
    - "No JSON produced"
    - Always register JacksonFeature
    - Show error in console

  • Wrong package scanning:
    - Too broad = slow startup
    - Scan unintended classes
    - Security risk
    - Be specific!

  • Blocking server:
    - server.join() waits forever
    - Blocks test execution
    - Use start() for tests
    - join() only in main

  • Real story:
    - "Customer scanned entire classpath"
    - "30 second startup"
    - "Found Spring classes!"
    - "Chaos ensued"

  • Prevention:
    - Always test startup time
    - Log what's scanned
    - Monitor startup
-->

<!-- pause -->

### 1. ❌ Missing Jackson Feature

```java
// Won't work without registration
config.packages("com.example.resources");

// ✅ Must register Jackson
config.packages("com.example.resources");
config.register(JacksonFeature.class);
```

<!-- pause -->

### 2. ❌ Wrong Package Scanning

```java
// ❌ Too broad - scans everything
packages("");

// ✅ Specific package
packages("com.dbh.training.rest.resources");
```

<!-- pause -->

### 3. ❌ Blocking Server Start

```java
// ❌ Blocks other initialization
server.join();  // Waits forever

// ✅ Start in background for testing
server.start();  // Non-blocking
```

<!-- end_slide -->

## Production Considerations

Beyond development

<!-- pause -->

### Thread Pool Configuration

```java
QueuedThreadPool threadPool = new QueuedThreadPool();
threadPool.setMaxThreads(200);
threadPool.setMinThreads(10);
threadPool.setIdleTimeout(60000);

Server server = new Server(threadPool);
```

<!-- pause -->

### Graceful Shutdown

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    logger.info("Shutting down server...");
    try {
        server.stop();
    } catch (Exception e) {
        logger.error("Error during shutdown", e);
    }
}));
```

<!-- pause -->

### Health Checks

```java
@Path("/health")
public class HealthResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        return Response.ok(Map.of("status", "UP")).build();
    }
}
```

<!-- end_slide -->

## 💡 Quick Reference

Jersey annotations cheat sheet

```java
@Path("/users")                    // Resource path
@GET, @POST, @PUT, @DELETE         // HTTP methods
@PathParam("id")                   // Path parameters
@QueryParam("name")                // Query parameters
@HeaderParam("Authorization")      // Header parameters
@FormParam("username")             // Form parameters
@CookieParam("session")            // Cookie parameters
@DefaultValue("10")                // Default values
@Produces(MediaType.APPLICATION_JSON)  // Response type
@Consumes(MediaType.APPLICATION_JSON)  // Request type
```

<!-- speaker_note: Keep this slide up during exercises -->

<!-- end_slide -->

## Exercise Preview

What we'll build next

<!--
speaker_note: |
  EXERCISE PREVIEW (3 minutes)

  • Set expectations:
    - "45-50 minutes"
    - "Test-driven approach"
    - "I'll help if stuck"

  • What you'll build:
    - Complete User CRUD
    - In-memory storage
    - All HTTP methods
    - Error handling

  • TDD approach:
    - Tests are provided
    - Make them pass
    - Red-green cycle

  • Success criteria:
    - All tests green
    - Postman/curl works
    - Proper status codes

  • Tips:
    - "Start with GET"
    - "Then POST"
    - "PUT/DELETE last"
    - "Ask questions!"

  • Transition:
    - "Let's recap key points"
    - "Then start coding!"
-->

<!-- pause -->

### Exercise 02: Jersey CRUD

You will implement:

1. Complete User model with all fields
1. GET /users - List all users
1. GET /users/{id} - Get specific user
1. POST /users - Create new user
1. PUT /users/{id} - Update user
1. DELETE /users/{id} - Delete user
1. Proper error handling for 404

<!-- pause -->

### Time: 45-50 minutes

Test-driven development approach

<!-- speaker_note: Transition to exercise -->

<!-- end_slide -->

## Key Takeaways

<!--
speaker_note: |
  KEY TAKEAWAYS (2 minutes)

  • Reinforce main points:

  • Point 1: JAX-RS standard
    - Portable code
    - Industry standard
    - Not proprietary

  • Point 2: Full control
    - See everything
    - Debug easier
    - Understand flow

  • Point 3: Lightweight
    - Fast startup
    - Small footprint
    - Cloud-friendly

  • Point 4: ResourceConfig
    - Central config
    - All setup here
    - Easy to test

  • Point 5: No Spring needed
    - DI alternatives exist
    - Config manageable
    - Testing works

  • Final message:
    - "You can build production APIs"
    - "Without framework magic"
    - "Let's prove it in exercise"
-->

<!-- pause -->

✅ Jersey is the JAX-RS reference implementation

<!-- pause -->

✅ Plain Jersey gives you full control vs Spring Boot's magic

<!-- pause -->

✅ Embedded servers (Jetty/Grizzly) make deployment simple

<!-- pause -->

✅ ResourceConfig is your central configuration point

<!-- pause -->

✅ You can achieve DI, configuration, and testing without Spring

<!-- end_slide -->

## Questions?

Before we start the exercise...

<!--
speaker_note: |
  QUESTIONS & EXERCISE TRANSITION (5 minutes)

  • Wrap up key points:
    - Jersey = JAX-RS standard
    - Less magic than Spring Boot
    - Full control
    - Better for microservices

  • Check understanding:
    - "Clear on Jersey vs Spring Boot?"
    - "Understand the setup?"
    - "Ready to code?"

  • Common questions:
    - "Can we use Spring beans?"
      Answer: No, use HK2 or CDI
    - "How about JPA?"
      Answer: Yes, manually configure
    - "Docker deployment?"
      Answer: Even simpler, smaller images

  • Exercise logistics:
    - "Open starter-project"
    - "Find UserResource.java"
    - "Run existing tests"
    - "Make them pass"

  • Pairing:
    - "Feel free to pair up"
    - "Help each other"
    - "I'll circulate"

  • Start exercise:
    - "Let's code!"
    - "45 minutes"
    - "Break at 14:45"
-->

<!-- end_slide -->