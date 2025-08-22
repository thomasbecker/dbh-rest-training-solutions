---
title: Plain Java with Jersey
sub_title: Building REST APIs without Spring Boot
author: Thomas Becker
date: August 2025
---

# Plain Java with Jersey

Building REST APIs without Spring Boot

Duration: 60 minutes

<!-- end_slide -->

## Agenda

1. What is Jersey?
1. Jersey vs Spring Boot
1. Project Setup
1. Core Dependencies
1. Server Configuration
1. Application Structure
1. Configuration Patterns

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

<!-- speaker_note: Emphasize we're using 2.x for Java 8 compatibility -->

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

<!-- speaker_note: Many enterprises still on Java 8 -->

<!-- end_slide -->

## Jersey vs Spring Boot

Side-by-side comparison

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

<!-- speaker_note: Version 2.35 is latest for Java 8 -->

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

<!-- speaker_note: Jetty more common in enterprise, Grizzly simpler setup -->

<!-- end_slide -->

## Project Structure

Standard layout for Jersey projects

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

<!-- speaker_note: 
- Check if everyone understands the setup
- Clarify Jersey vs Spring Boot if needed
- Ensure everyone has the starter project ready
-->

<!-- end_slide -->