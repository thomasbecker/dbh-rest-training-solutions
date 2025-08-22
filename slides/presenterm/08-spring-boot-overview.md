---
title: Spring Boot Overview
sub_title: Understanding the Magic Behind the Framework
author: Thomas Becker
date: August 2025
---

# Spring Boot Overview

Understanding the Magic Behind the Framework

Duration: 30 minutes

<!-- end_slide -->

## Agenda

1. What Spring Boot Abstracts
1. Auto-Configuration Magic
1. Jersey with vs without Spring Boot
1. Embedded Servers Comparison
1. Configuration Management
1. When to Use Each Approach

<!-- end_slide -->

## What is Spring Boot?

Opinionated view of the Spring platform

<!-- pause -->

### Core Philosophy

- **Convention over Configuration**
- **Opinionated Defaults**
- **Production-Ready Features**
- **Simplified Dependency Management**

<!-- pause -->

### Key Components

- Spring Framework (DI, AOP)
- Auto-configuration
- Embedded servers
- Actuator (monitoring)
- DevTools

<!-- speaker_note: Spring Boot is NOT a new framework, it's Spring made easier -->

<!-- end_slide -->

## What Spring Boot Abstracts

The magic revealed

<!-- pause -->

### 1. Server Setup

**Without Spring Boot:**
```java
Server server = new Server(8080);
ServletContextHandler context = new ServletContextHandler();
ServletHolder jerseyServlet = new ServletHolder(ServletContainer.class);
jerseyServlet.setInitParameter(...);
context.addServlet(jerseyServlet, "/*");
server.setHandler(context);
server.start();
```

<!-- pause -->

**With Spring Boot:**
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

<!-- end_slide -->

## Configuration Abstraction

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### Plain Java

```java
Properties props = new Properties();
try (InputStream is = 
    getClass().getResourceAsStream(
        "/application.properties")) {
    props.load(is);
}
String value = props.getProperty("key");

// Manual type conversion
int port = Integer.parseInt(
    props.getProperty("server.port", "8080"));
```

<!-- column: 1 -->

### Spring Boot

```java
@Value("${server.port:8080}")
private int port;

// Or with type-safe config
@ConfigurationProperties("app")
public class AppConfig {
    private int port;
    private String name;
    // Auto-binding with validation
}
```

<!-- reset_layout -->

<!-- end_slide -->

## Dependency Injection

The IoC container difference

<!-- pause -->

### Plain Jersey (HK2)

```java
public class AppBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(UserRepository.class);
        bind(UserService.class).to(UserService.class);
    }
}
// Manual registration in JerseyConfig
```

<!-- pause -->

### Spring Boot

```java
@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    // Auto-wired, auto-scanned, auto-registered
}
```

<!-- speaker_note: Spring's DI is more mature and feature-rich -->

<!-- end_slide -->

## Auto-Configuration Magic

How Spring Boot "just works"

<!-- pause -->

### The Process

1. **Classpath Scanning** - What libraries are present?
1. **Conditional Configuration** - @ConditionalOnClass
1. **Property Binding** - application.properties
1. **Bean Creation** - Automatic component creation

<!-- pause -->

### Example: Jackson Auto-Config

```java
// If Jackson is on classpath:
@ConditionalOnClass(ObjectMapper.class)
@EnableConfigurationProperties(JacksonProperties.class)
public class JacksonAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        // Pre-configured with sensible defaults
        return Jackson2ObjectMapperBuilder
            .json()
            .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
            .modules(new JavaTimeModule())
            .build();
    }
}
```

<!-- end_slide -->

## Jersey Integration Comparison

<!-- pause -->

### Jersey with Spring Boot

```java
@Component
@Path("/users")
public class UserResource {
    
    @Autowired  // Spring DI
    private UserService userService;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
        return userService.findAll();
    }
}
```

```yaml
# application.yml
spring:
  jersey:
    application-path: /api
    type: filter  # or servlet
```

<!-- pause -->

### Benefits

- Spring ecosystem integration
- Actuator endpoints
- Spring Security
- Spring Data
- Transaction management

<!-- end_slide -->

## Database Integration

A key difference

<!-- pause -->

### Plain Jersey

```java
// Manual connection management
public class UserRepository {
    private static final String URL = "jdbc:postgresql://...";
    
    public List<User> findAll() {
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = ps.executeQuery()) {
            // Manual mapping
        }
    }
}
```

<!-- pause -->

### Spring Boot + Spring Data

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Methods auto-implemented
    List<User> findByEmailContaining(String email);
}
```

<!-- speaker_note: Spring Data is a huge productivity boost -->

<!-- end_slide -->

## Testing Comparison

<!-- column_layout: [1, 1] -->

<!-- column: 0 -->

### Plain Jersey

```java
public class UserResourceTest {
    private static Server server;
    
    @BeforeAll
    static void startServer() {
        server = createTestServer();
        server.start();
    }
    
    @AfterAll
    static void stopServer() {
        server.stop();
    }
    
    @Test
    void testGetUser() {
        // RestAssured or HttpClient
    }
}
```

<!-- column: 1 -->

### Spring Boot

```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserResourceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void testGetUser() {
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk());
    }
}
```

<!-- reset_layout -->

<!-- end_slide -->

## Production Features

What Spring Boot adds

<!-- pause -->

### Actuator Endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

Automatic endpoints:
- `/actuator/health` - Health checks
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application info
- `/actuator/env` - Environment properties

<!-- pause -->

### DevTools

- Automatic restart
- LiveReload
- Remote debugging
- H2 console

<!-- end_slide -->

## When to Use Plain Jersey

Making the right choice

<!-- pause -->

### ✅ Good Fit

- **Microservices** - Minimal footprint
- **Simple APIs** - CRUD without complexity
- **Learning** - Understanding fundamentals
- **Java 8 Legacy** - Older environments
- **Performance Critical** - Every millisecond counts
- **Container Native** - Building for Kubernetes

<!-- pause -->

### Example Metrics

| Metric | Plain Jersey | Spring Boot |
|--------|-------------|-------------|
| Startup Time | ~1 second | 3-10 seconds |
| Memory (idle) | ~50 MB | ~150 MB |
| JAR Size | ~10 MB | ~30 MB |

<!-- end_slide -->

## When to Use Spring Boot

Leveraging the ecosystem

<!-- pause -->

### ✅ Good Fit

- **Enterprise Applications** - Full feature set
- **Rapid Development** - Productivity matters
- **Complex Requirements** - Security, transactions
- **Team Familiarity** - Existing Spring knowledge
- **Integration Heavy** - Multiple databases, queues
- **Full-Stack** - Including web UI

<!-- pause -->

### Ecosystem Benefits

- Spring Security (OAuth2, JWT)
- Spring Data (JPA, MongoDB, Redis)
- Spring Cloud (Microservices patterns)
- Spring Batch (Batch processing)

<!-- end_slide -->

## Migration Path

From one to the other

<!-- pause -->

### Jersey → Spring Boot

```java
// Step 1: Add Spring Boot dependencies
// Step 2: Keep Jersey resources as-is
@Component  // Add Spring annotation
@Path("/users")
public class UserResource {
    // Existing Jersey code works
}

// Step 3: Gradually adopt Spring features
@Autowired
private UserService service;
```

<!-- pause -->

### Spring Boot → Plain Jersey

- Extract business logic from Spring beans
- Replace @Autowired with constructor injection
- Implement configuration management
- Set up embedded server manually

<!-- speaker_note: Migration is usually Jersey → Spring Boot -->

<!-- end_slide -->

## Real-World Architecture

Pragmatic choices

<!-- pause -->

### Hybrid Approach

```
┌─────────────────────────────────┐
│   API Gateway (Spring Cloud)     │
└─────────────┬───────────────────┘
              │
    ┌─────────┴─────────┬─────────┐
    ▼                   ▼         ▼
┌────────┐      ┌────────┐  ┌────────┐
│ User    │      │ Order   │  │ Payment │
│ Service │      │ Service │  │ Service │
│ (Jersey)│      │ (Spring) │  │ (Jersey)│
└────────┘      └────────┘  └────────┘
```

<!-- pause -->

Choose per service based on:
- Complexity
- Team expertise
- Performance requirements

<!-- end_slide -->

## Code Comparison

Same API, different approaches

<!-- pause -->

### Task: Create User API with validation

**Plain Jersey**: ~200 lines
- Server setup
- Jersey config
- Validation setup
- Exception mapping
- JSON configuration

<!-- pause -->

**Spring Boot**: ~50 lines
- @SpringBootApplication
- @RestController
- @Valid annotation
- Done!

<!-- pause -->

**But**: Spring Boot adds ~100MB of dependencies

<!-- end_slide -->

## Decision Framework

Questions to ask

<!-- pause -->

1. **What's your team's experience?**
   - Spring experts? → Spring Boot
   - Java EE background? → Jersey

<!-- pause -->

2. **What's your timeline?**
   - Tight deadline? → Spring Boot
   - Time to learn? → Either

<!-- pause -->

3. **What are your constraints?**
   - Memory limited? → Jersey
   - Complex requirements? → Spring Boot

<!-- pause -->

4. **What's your deployment target?**
   - Kubernetes? → Consider Jersey
   - Traditional? → Either works

<!-- end_slide -->

## 💡 Migration Tips

If moving to Spring Boot

<!-- pause -->

### 1. Start with Spring Boot + Jersey

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jersey</artifactId>
</dependency>
```

<!-- pause -->

### 2. Gradually Adopt Features

- Week 1: Just run Jersey in Spring Boot
- Week 2: Add Spring configuration
- Week 3: Integrate Spring Security
- Week 4: Add Spring Data

<!-- pause -->

### 3. Keep What Works

Don't rewrite working Jersey resources just to use @RestController

<!-- end_slide -->

## Key Takeaways

<!-- pause -->

✅ Spring Boot abstracts complexity through auto-configuration

<!-- pause -->

✅ Plain Jersey gives you full control and minimal footprint

<!-- pause -->

✅ Spring Boot excels at enterprise features and developer productivity

<!-- pause -->

✅ Plain Jersey is better for microservices and learning

<!-- pause -->

✅ You can use Jersey within Spring Boot for gradual migration

<!-- pause -->

✅ Choose based on requirements, not resume-driven development

<!-- end_slide -->

## Questions?

Let's discuss your Spring Boot experiences

<!-- speaker_note: 
- Ask who has used Spring Boot
- What challenges they've faced
- What they like/dislike about it
- Transition to afternoon exercises
-->

<!-- end_slide -->