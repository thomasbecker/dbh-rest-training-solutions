---
title: Spring Boot Overview
sub_title: Understanding the Magic Behind the Framework
author: Thomas Becker
date: August 2025
---

# Spring Boot Overview

Understanding the Magic Behind the Framework

Duration: 30 minutes

<!--
speaker_note: |
  MODULE INTRODUCTION (2 minutes)

  • Context setting:
    - "Day 1 almost done!"
    - "Last content module"
    - "Why learn about Spring Boot?"

  • Purpose of this module:
    - Understand what we avoided
    - Appreciate Jersey simplicity
    - Know when to use which
    - Not promoting Spring Boot!

  • Key message:
    - "Knowledge is power"
    - "Understand trade-offs"
    - "Make informed decisions"

  • Energy check:
    - End of day fatigue?
    - Keep it light
    - Interactive discussion
-->

<!-- end_slide -->

## Agenda

1. What Spring Boot Abstracts
1. Auto-Configuration Magic
1. Jersey with vs without Spring Boot
1. Embedded Servers Comparison
1. Configuration Management
1. When to Use Each Approach

<!--
speaker_note: |
  AGENDA OVERVIEW (1 minute)

  • Quick overview:
    - What Spring Boot does
    - How it works
    - Comparison with Jersey
    - Decision framework

  • Not a Spring Boot tutorial:
    - Focus on understanding
    - Not implementation
    - Conceptual level

  • Interactive:
    - "Who uses Spring Boot?"
    - "What do you like/dislike?"
    - Share experiences
-->

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

<!--
speaker_note: |
  WHAT IS SPRING BOOT (3 minutes)

  • Historical context:
    - Spring Framework since 2003
    - Configuration hell by 2010
    - Spring Boot born 2014
    - Solution to complexity

  • Core philosophy explained:
    - Convention: sensible defaults
    - Opinionated: one way to do things
    - Production: metrics, health built-in
    - Dependencies: starter POMs

  • Key components:
    - Spring Framework: still there!
    - Auto-config: the magic
    - Embedded servers: Tomcat default
    - Actuator: ops features
    - DevTools: developer experience

  • Important clarification:
    - "NOT a new framework"
    - "Spring + conventions"
    - "Simplification layer"

  • Ask audience:
    - "Who struggled with old Spring?"
    - "Remember XML configs?"
-->

<!-- end_slide -->

## What Spring Boot Abstracts

The magic revealed

<!--
speaker_note: |
  ABSTRACTION COMPARISON (4 minutes)

  • Side-by-side code:
    - Jersey: 10 lines setup
    - Spring Boot: 3 lines
    - Where did code go?

  • What's hidden:
    - Server creation
    - Port binding
    - Context setup
    - Component scanning
    - Error handling

  • The cost:
    - Black box when fails
    - Hard to debug
    - Magic confusion
    - Larger artifact

  • The benefit:
    - Faster development
    - Less boilerplate
    - Standard patterns
    - Team consistency

  • Key point:
    - "Both approaches work"
    - "Trade-off decision"
    - "No right answer"
-->

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

<!--
speaker_note: |
  DEPENDENCY INJECTION (4 minutes)

  • HK2 explanation:
    - Jersey's built-in DI
    - Lightweight
    - Manual binding
    - Good enough for most

  • Spring DI advantages:
    - 20 years mature
    - AOP support
    - Scopes (singleton, prototype, request)
    - Conditional beans
    - Profiles

  • Trade-offs:
    - HK2: Simple, explicit
    - Spring: Powerful, magic

  • Real impact:
    - Small project: HK2 fine
    - Large team: Spring better
    - Learning: HK2 clearer

  • Common question:
    - "Can we use CDI instead?"
    - Yes! Weld works with Jersey
-->

<!-- end_slide -->

## Auto-Configuration Magic

How Spring Boot "just works"

<!--
speaker_note: |
  AUTO-CONFIGURATION (4 minutes)

  • The magic explained:

  • Step 1: Classpath scan
    - What JARs present?
    - Jackson? → JSON config
    - H2? → In-memory DB
    - Tomcat? → Web server

  • Step 2: Conditional config
    - @ConditionalOnClass
    - @ConditionalOnMissingBean
    - @ConditionalOnProperty
    - Smart defaults

  • Step 3: Properties
    - application.properties
    - application.yml
    - Environment variables
    - Override defaults

  • Step 4: Bean creation
    - Automatic wiring
    - Dependency injection
    - Ready to use

  • Jackson example:
    - Sensible defaults
    - Java 8 time support
    - No timestamp dates
    - Pretty printing dev

  • The danger:
    - "What if wrong defaults?"
    - "How to override?"
    - "Debugging nightmare?"
-->

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

<!--
speaker_note: |
  WHEN PLAIN JERSEY (4 minutes)

  • Good fit scenarios:

  • Microservices:
    - 50MB vs 200MB RAM
    - 1s vs 5s startup
    - Multiplied by 100 instances!

  • Simple APIs:
    - CRUD only
    - No complex requirements
    - Overkill to use Spring

  • Learning:
    - Understand fundamentals
    - No magic hiding concepts
    - Better foundation

  • Legacy Java 8:
    - Real constraint
    - Many enterprises
    - Jersey 2.x perfect

  • Performance:
    - Every millisecond counts
    - High-frequency trading
    - IoT devices

  • Container native:
    - Kubernetes loves small
    - Faster scaling
    - Lower costs

  • Show metrics:
    - Real numbers matter
    - Not hypothetical
-->

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

<!--
speaker_note: |
  WHEN SPRING BOOT (4 minutes)

  • Good fit scenarios:

  • Enterprise apps:
    - Complex requirements
    - Multiple integrations
    - Large teams

  • Rapid development:
    - Prototypes
    - MVPs
    - Tight deadlines

  • Complex requirements:
    - Security (OAuth2, JWT)
    - Transactions
    - Messaging (JMS, AMQP)
    - Batch processing

  • Team familiarity:
    - Most know Spring
    - Hiring easier
    - Training available

  • Integration heavy:
    - Databases (multiple)
    - Message queues
    - Cache systems
    - Cloud services

  • Ecosystem benefits:
    - Spring Data magic
    - Spring Security mature
    - Spring Cloud patterns
    - Community huge

  • Reality check:
    - "Most enterprises use it"
    - "De facto standard"
    - "Can't go wrong"
-->

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

<!--
speaker_note: |
  MIGRATION STRATEGIES (3 minutes)

  • Jersey to Spring Boot:

  • Step 1: Add Spring Boot
    - Keep Jersey resources
    - Add @Component
    - Works immediately!

  • Step 2: Gradual adoption
    - Use Spring DI
    - Add Spring Data
    - Add Security

  • Step 3: Full migration
    - Optional!
    - Maybe never
    - Jersey in Spring works

  • Spring Boot to Jersey:
    - Rarely happens
    - Extract business logic
    - Remove annotations
    - Manual wiring

  • Real-world approach:
    - "Start with Jersey"
    - "Learn fundamentals"
    - "Add Spring if needed"
    - "Incremental growth"

  • Success story:
    - "Client started Jersey"
    - "Grew to Spring Boot"
    - "Smooth transition"
    - "No rewrite needed"
-->

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

<!--
speaker_note: |
  DECISION FRAMEWORK (3 minutes)

  • Walk through each question:

  • Team experience:
    - Spring experts? Use it
    - Java EE background? Jersey
    - New team? Either works

  • Timeline:
    - Tomorrow? Spring Boot
    - Next month? Either
    - Learning project? Jersey

  • Constraints:
    - Memory limited? Jersey
    - Complex integrations? Spring
    - Performance critical? Jersey
    - Feature rich? Spring

  • Deployment:
    - Kubernetes? Jersey lighter
    - Traditional server? Either
    - Serverless? Jersey faster
    - Cloud PaaS? Spring better

  • Real advice:
    - "Start simple"
    - "Migrate if needed"
    - "Don't over-engineer"

  • Ask audience:
    - "What's your scenario?"
    - Let's categorize
-->

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

<!--
speaker_note: |
  KEY TAKEAWAYS (3 minutes)

  • Reinforce main points:

  • Point 1: Abstracts complexity
    - Good and bad
    - Power vs understanding
    - Choose wisely

  • Point 2: Jersey = control
    - See everything
    - Understand flow
    - Debug easier

  • Point 3: Spring Boot = productivity
    - Faster development
    - More features
    - Bigger ecosystem

  • Point 4: Jersey = lightweight
    - Smaller footprint
    - Faster startup
    - Cloud-friendly

  • Point 5: Both production ready
    - No wrong choice
    - Both work at scale
    - Many success stories

  • Point 6: Requirements matter
    - Not resume-driven
    - Not trend-driven
    - Problem-driven

  • Final thought:
    - "You learned Jersey today"
    - "Foundation for both"
    - "Can learn Spring Boot easily now"
-->

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

<!--
speaker_note: |
  WRAP UP & DAY 1 CLOSE (5 minutes)

  • Open discussion:
    - "Spring Boot experiences?"
    - "Jersey thoughts?"
    - "Which would you choose?"

  • Common questions:
    - "Can we migrate later?"
      Yes, Jersey → Spring easier
    - "Job market?"
      Spring Boot dominates
    - "Your preference?"
      Depends on project

  • Day 1 summary:
    - REST fundamentals ✓
    - Resource design ✓
    - Jersey setup ✓
    - CRUD implementation ✓
    - Idempotency ✓
    - Alternatives ✓

  • Day 2 preview:
    - Jackson deep dive
    - Security implementation
    - Advanced patterns
    - Comprehensive exercise

  • Closing:
    - "Great first day!"
    - "See you tomorrow 9 AM"
    - "Questions via email/Slack"
    - "Code in GitHub"

  • Homework (optional):
    - "Review today's code"
    - "Try bonus exercises"
    - "Think about your APIs"
-->

<!-- end_slide -->