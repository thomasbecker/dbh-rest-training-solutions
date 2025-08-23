---
title: Spring Boot Overview - What Changes?
author: Thomas Becker
theme:
  name: dark
---

# Spring Boot Overview

What Spring Boot brings to Jersey development

⏱️ **Duration**: 35 minutes  
🎯 **Goal**: Understand Spring Boot and the Java REST ecosystem

<!-- end_slide -->

---

## Our Journey So Far

### Plain Jersey (What We've Built)

- Manual server configuration (Jetty)
- Explicit Jersey ResourceConfig
- Direct dependency management
- Manual JSON provider setup
- Custom exception mappers

<!-- pause -->

### The Question

**Is there an easier way?**

<!--
speaker_note: |
  INTRODUCTION (2 minutes)

  • Context setting:
    - "We've built everything manually"
    - "You understand the foundations"
    - "Now let's see the alternative"

  • Key message:
    - Not "better", just different
    - Trade-offs exist
    - Right tool for right job

  • Audience check:
    - "Who has used Spring Boot?"
    - "Who prefers plain Java?"
    - "Let's explore objectively"
-->

<!-- end_slide -->

---

## What is Spring Boot?

### The Elevator Pitch

"Spring Boot makes it easy to create stand-alone, production-grade Spring-based applications that you can 'just run'"

<!-- pause -->

### Key Features

- **Opinionated defaults** - Works out of the box
- **Auto-configuration** - Detects and configures automatically
- **Embedded servers** - No WAR deployment needed
- **Production ready** - Metrics, health checks, externalized config

<!-- pause -->

### Philosophy

**Convention over Configuration**

<!--
speaker_note: |
  SPRING BOOT INTRO (3 minutes)

  • Elevator pitch:
    - Emphasize "just run"
    - Production-grade important
    - Stand-alone applications

  • Opinionated defaults:
    - Tomcat by default
    - Jackson for JSON
    - Sensible security
    - Works immediately

  • Auto-configuration:
    - Classpath scanning
    - Conditional beans
    - Smart defaults

  • Production features:
    - Actuator endpoints
    - Metrics with Micrometer
    - External config (12-factor)
-->

<!-- end_slide -->

---

## Plain Jersey Setup (Our Approach)

### What We Did Manually

```java
// Main.java - We configured everything
public class Main {
    public static void main(String[] args) {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/api");

        ServletHolder jerseyServlet = new ServletHolder(
            new ServletContainer(new JerseyConfig())
        );
        context.addServlet(jerseyServlet, "/*");

        server.setHandler(context);
        server.start();
    }
}
```

<!-- pause -->

**40+ lines of boilerplate** ⚙️

<!--
speaker_note: |
  MANUAL SETUP REVIEW (3 minutes)

  • What we wrote:
    - Server initialization
    - Port configuration
    - Context path setup
    - Jersey servlet registration
    - Handler configuration

  • Pain points:
    - Lots of boilerplate
    - Easy to misconfigure
    - No standard structure
    - Manual dependency management

  • But benefits:
    - Full control
    - Understand everything
    - No magic
    - Smaller footprint
-->

<!-- end_slide -->

---

## Spring Boot Jersey Setup

### The Same App with Spring Boot

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

<!-- pause -->

### Jersey Configuration

```java
@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(UserResource.class);
        register(ValidationExceptionMapper.class);
    }
}
```

<!-- pause -->

**That's it!** ✨

<!--
speaker_note: |
  SPRING BOOT SETUP (3 minutes)

  • Dramatic reduction:
    - 40 lines → 5 lines
    - No server code
    - No servlet setup
    - Just annotations

  • What happens:
    - @SpringBootApplication scans
    - Finds ResourceConfig
    - Auto-configures Jersey
    - Starts embedded Tomcat

  • Magic explained:
    - spring-boot-starter-jersey
    - Auto-configuration classes
    - Conditional beans
    - Smart defaults
-->

<!-- end_slide -->

---

## What Spring Boot Abstracts

### 1. Server Management

**Plain Jersey:**

- Choose server (Jetty, Tomcat, Grizzly)
- Configure manually
- Handle lifecycle

<!-- pause -->

**Spring Boot:**

- Embedded Tomcat by default
- Zero configuration
- Managed lifecycle

<!-- end_slide -->

---

## What Spring Boot Abstracts

### 2. Dependency Management

**Plain Jersey:**

```gradle
dependencies {
    implementation 'org.glassfish.jersey.core:jersey-server:2.35'
    implementation 'org.glassfish.jersey.containers:jersey-container-servlet:2.35'
    implementation 'org.glassfish.jersey.media:jersey-media-json-jackson:2.35'
    implementation 'org.eclipse.jetty:jetty-server:9.4.44.v20210927'
    // ... many more
}
```

<!-- pause -->

**Spring Boot:**

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-jersey'
}
```

<!--
speaker_note: |
  DEPENDENCY MANAGEMENT (3 minutes)

  • Plain Jersey challenges:
    - Version compatibility
    - Transitive dependencies
    - Manual updates
    - Potential conflicts

  • Spring Boot solution:
    - BOM (Bill of Materials)
    - Curated versions
    - Tested combinations
    - Single version property

  • Trade-off:
    - Less control
    - Bigger size
    - More dependencies
    - But guaranteed compatibility
-->

<!-- end_slide -->

---

## What Spring Boot Abstracts

### 3. Configuration Management

**Plain Jersey:**

```java
// Hardcoded or custom properties
int port = Integer.parseInt(System.getProperty("port", "8080"));
String dbUrl = System.getProperty("db.url", "jdbc:...");
```

<!-- pause -->

**Spring Boot:**

```yaml
# application.yml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:postgresql://localhost/mydb
```

<!-- pause -->

With profiles: `application-dev.yml`, `application-prod.yml`

<!--
speaker_note: |
  CONFIGURATION (2 minutes)

  • Configuration sources:
    - application.properties/yml
    - Environment variables
    - Command line args
    - Cloud config server

  • Profile support:
    - Dev/test/prod
    - Feature toggles
    - Environment specific

  • Type-safe:
    - @ConfigurationProperties
    - Validation
    - IDE support
-->

<!-- end_slide -->

---

## Production Features Comparison

### Plain Jersey

**You Build:**

- Health checks
- Metrics collection
- Logging configuration
- Environment management

<!-- pause -->

### Spring Boot

**You Get:**

- `/actuator/health` - Health endpoint
- `/actuator/metrics` - Metrics endpoint
- Logback auto-configured
- Profile-based environments

<!--
speaker_note: |
  PRODUCTION FEATURES (3 minutes)

  • Health checks:
    - Database connectivity
    - Disk space
    - Custom indicators
    - Kubernetes ready

  • Metrics:
    - JVM metrics
    - HTTP metrics
    - Custom metrics
    - Prometheus compatible

  • Observability:
    - Distributed tracing
    - Structured logging
    - Correlation IDs
-->

<!-- end_slide -->

---

## Testing Comparison

### Plain Jersey Testing

```java
public class UserResourceTest {
    private static Server server;

    @BeforeAll
    public static void startServer() {
        server = new Server(8080);
        // ... 20 lines of setup
    }
}
```

<!-- pause -->

### Spring Boot Testing

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserResourceTest {
    @LocalServerPort
    private int port;
    // Ready to test!
}
```

<!--
speaker_note: |
  TESTING (2 minutes)

  • Spring Boot testing:
    - Random port allocation
    - Test slices (@WebMvcTest)
    - MockBean support
    - Test properties

  • Benefits:
    - Less boilerplate
    - Better isolation
    - Faster tests
    - Built-in mocking
-->

<!-- end_slide -->

---

## Dependency Injection

### Plain Jersey

```java
public class UserResource {
    private UserService userService = new UserService();
    private UserRepository repository = new UserRepository();

    // Manual instantiation
}
```

<!-- pause -->

### Spring Boot

```java
@RestController
public class UserResource {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    // Automatic injection
}
```

<!--
speaker_note: |
  DEPENDENCY INJECTION (3 minutes)

  • DI benefits:
    - Loose coupling
    - Easy testing
    - Interface programming
    - Lifecycle management

  • Spring features:
    - Constructor injection
    - Field injection
    - Qualifier support
    - Scope management

  • Trade-offs:
    - More complexity
    - Runtime errors
    - Debugging harder
    - But better architecture
-->

<!-- end_slide -->

---

## Database Integration

### Plain Jersey

```java
// Manual JDBC
Connection conn = DriverManager.getConnection(url, user, pass);
PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
ResultSet rs = ps.executeQuery();
// Manual mapping
```

<!-- pause -->

### Spring Boot

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // That's it! CRUD operations included

    List<User> findByEmail(String email);
    // Custom queries auto-implemented
}
```

<!--
speaker_note: |
  DATABASE INTEGRATION (3 minutes)

  • Spring Data JPA:
    - Repository pattern
    - Query derivation
    - Pagination support
    - Transaction management

  • Auto-configuration:
    - DataSource setup
    - JPA/Hibernate config
    - Connection pooling
    - Schema generation

  • Plain alternative:
    - More control
    - Less magic
    - Better performance tuning
    - But more code
-->

<!-- end_slide -->

---

## When to Use Plain Jersey

### ✅ Good For:

- **Microservices** - Minimal footprint
- **Learning** - Understand the basics
- **Simple APIs** - Few endpoints, no DB
- **Embedded systems** - Resource constraints
- **Full control** - No magic, no surprises

<!-- pause -->

### Example Scenarios:

- Lambda functions
- IoT gateways
- Teaching REST concepts
- Proof of concepts

<!--
speaker_note: |
  PLAIN JERSEY USE CASES (3 minutes)

  • Microservices:
    - 10MB vs 50MB+
    - Faster startup
    - Less memory
    - Cloud cost savings

  • Learning value:
    - No hidden behavior
    - Understand HTTP
    - See the plumbing
    - Better debugging skills

  • Performance:
    - Less overhead
    - Predictable behavior
    - Fine-tuned control
-->

<!-- end_slide -->

---

## When to Use Spring Boot

### ✅ Good For:

- **Enterprise apps** - Full feature set
- **Rapid development** - Fast prototyping
- **Standard stack** - Team familiarity
- **Complex requirements** - DB, security, messaging
- **Production features** - Monitoring, metrics

<!-- pause -->

### Example Scenarios:

- Corporate APIs
- Full applications
- Multi-module projects
- Microservice platforms

<!--
speaker_note: |
  SPRING BOOT USE CASES (3 minutes)

  • Enterprise benefits:
    - Industry standard
    - Huge ecosystem
    - Commercial support
    - Talent availability

  • Rapid development:
    - Starters for everything
    - Code generation
    - Convention based
    - Less boilerplate

  • Ecosystem:
    - Spring Security
    - Spring Data
    - Spring Cloud
    - Spring Batch
-->

<!-- end_slide -->

---

## Migration Path

### From Plain Jersey to Spring Boot

**Step 1:** Add Spring Boot dependencies

```gradle
implementation 'org.springframework.boot:spring-boot-starter-jersey'
```

<!-- pause -->

**Step 2:** Add @SpringBootApplication

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

<!-- pause -->

**Step 3:** Your Jersey resources work as-is! ✅

<!--
speaker_note: |
  MIGRATION (2 minutes)

  • Gradual migration:
    - Start with starter
    - Keep existing code
    - Add features gradually
    - Refactor as needed

  • What changes:
    - Main class
    - Configuration files
    - Testing approach
    - Build configuration

  • What stays:
    - Resource classes
    - Business logic
    - JAX-RS annotations
    - REST contracts
-->

<!-- end_slide -->

---

## Beyond Spring Boot: Other Alternatives

### The Modern Java REST Ecosystem

**Quarkus** - "Supersonic Subatomic Java"

- Native compilation (GraalVM)
- ~10ms startup, ~10MB memory
- Container-first design
- Live reload development

<!-- pause -->

**MicroProfile** - Enterprise Java for Microservices

- Jakarta EE subset + microservice patterns
- Vendor-neutral specification
- Includes: Health, Metrics, Config, Fault Tolerance
- Implementations: Open Liberty, Payara, WildFly, Quarkus (Microprofile 4.1)

<!-- pause -->

**Others Worth Knowing**

- **Micronaut** - Compile-time DI, low memory
- **Helidon** - Oracle's microservice framework
- **Vert.x** - Reactive, polyglot toolkit

<!--
speaker_note: |
  OTHER ALTERNATIVES (3 minutes)

  • Quarkus highlights:
    - Red Hat's answer to Spring Boot
    - Native compilation game-changer
    - Kubernetes-native
    - Developer joy focus
    - "Best of both worlds"

  • MicroProfile explanation:
    - NOT a framework, a spec
    - Like JAX-RS but broader
    - Multiple vendors implement
    - Portability between servers
    - Enterprise-focused

  • Why mention these:
    - Complete picture
    - Industry awareness
    - Different use cases
    - Not just Spring vs Jersey

  • Quick comparison:
    - Spring Boot: Most popular, huge ecosystem
    - Quarkus: Best performance, modern
    - MicroProfile: Standards-based, portable
    - Jersey: Simple, lightweight
    - Each has its place!
-->

<!-- end_slide -->

---

## Choosing Your Framework

### Decision Matrix

| Framework        | Best For                     | Trade-offs      |
| ---------------- | ---------------------------- | --------------- |
| **Plain Jersey** | Learning, small services     | Manual setup    |
| **Spring Boot**  | Enterprise, fast development | Resource usage  |
| **Quarkus**      | Cloud-native, performance    | Newer ecosystem |
| **MicroProfile** | Vendor flexibility           | Less tooling    |

<!-- pause -->

### Key Questions

1. **Performance critical?** → Quarkus or Plain Jersey
2. **Enterprise standard?** → Spring Boot or MicroProfile
3. **Team experience?** → Spring Boot (most common)
4. **Cloud-native focus?** → Quarkus or Spring Boot
5. **Simplicity needed?** → Plain Jersey

<!--
speaker_note: |
  FRAMEWORK SELECTION (2 minutes)

  • No perfect answer:
    - Context matters most
    - Team skills crucial
    - Existing infrastructure
    - Performance requirements

  • Current market (2024):
    - Spring Boot ~60% market
    - Quarkus growing fast
    - MicroProfile in enterprises
    - Jersey for simplicity

  • Recommendation:
    - Learn fundamentals (Jersey)
    - Use what team knows
    - Consider Quarkus for new
    - Spring Boot safe choice

  • Future trends:
    - Native compilation growing
    - Serverless driving change
    - Reactive becoming standard
    - Standards still matter
-->

<!-- end_slide -->

---

## Real-World Considerations

### Performance Comparison

| Metric       | Plain Jersey | Spring Boot |
| ------------ | ------------ | ----------- |
| Startup time | ~1 second    | 3-5 seconds |
| Memory usage | ~50 MB       | 150-300 MB  |
| JAR size     | ~10 MB       | 50-80 MB    |
| Throughput   | Similar      | Similar     |

<!-- pause -->

### Development Speed

| Task           | Plain Jersey | Spring Boot |
| -------------- | ------------ | ----------- |
| Initial setup  | Hours        | Minutes     |
| Add database   | Days         | Hours       |
| Add security   | Days         | Hours       |
| Add monitoring | Days         | Minutes     |

<!--
speaker_note: |
  REAL-WORLD METRICS (3 minutes)

  • Performance notes:
    - Startup matters for serverless
    - Memory matters for containers
    - JAR size affects deployment
    - Throughput usually not issue

  • Development speed:
    - Spring wins initially
    - Plain catches up later
    - Debugging time varies
    - Maintenance considerations

  • Team factors:
    - Existing knowledge
    - Project timeline
    - Long-term support
-->

<!-- end_slide -->

---

## Our Training Choice

### Why Plain Jersey?

1. **Understand foundations** - No hidden magic
2. **See the mechanics** - How REST really works
3. **Appreciate abstractions** - Know what you're giving up
4. **Minimal prerequisites** - Just Java knowledge needed

<!-- pause -->

### The Learning Path

```
Fundamentals → Implementation → Abstractions → Productivity
   (Day 1)        (Day 1)        (Now)        (Your choice)
```

<!--
speaker_note: |
  TRAINING RATIONALE (2 minutes)

  • Educational value:
    - Better developers
    - Debugging skills
    - Problem solving
    - Framework agnostic

  • After this training:
    - Can use any framework
    - Understand trade-offs
    - Make informed choices
    - Debug effectively

  • Message to class:
    - "You earned this knowledge"
    - "Now you can choose"
    - "Both are valid"
-->

<!-- end_slide -->

---

## Decision Framework

### Ask Yourself:

1. **Team size?** Small → Plain, Large → Spring Boot
2. **Project scope?** Simple → Plain, Complex → Spring Boot
3. **Time to market?** Months → Plain, Weeks → Spring Boot
4. **Performance critical?** Yes → Plain, No → Spring Boot
5. **Existing infrastructure?** Minimal → Plain, Enterprise → Spring Boot

<!-- pause -->

### The Answer:

**It depends!** And now you can make an informed choice 🎯

<!--
speaker_note: |
  DECISION FRAMEWORK (2 minutes)

  • No absolute answer:
    - Context matters
    - Requirements vary
    - Teams differ
    - Constraints exist

  • Key factors:
    - Team expertise
    - Timeline pressure
    - Maintenance burden
    - Operational requirements

  • Final advice:
    - Start simple
    - Add complexity as needed
    - Measure and validate
    - Be pragmatic
-->

<!-- end_slide -->

---

## Summary

### What We Learned

✅ **Spring Boot** = Productivity through convention
✅ **Plain Jersey** = Control through simplicity
✅ **Both are valid** = Choose based on context
✅ **Foundation matters** = You understand both now

<!-- pause -->

### Your Toolkit

- **Need speed?** → Spring Boot
- **Need control?** → Plain Jersey
- **Need both?** → Start plain, migrate later
- **Need to decide?** → You have the knowledge!

<!--
speaker_note: |
  SUMMARY (1 minute)

  • Key takeaways:
    - No silver bullet
    - Trade-offs exist
    - Context is king
    - Knowledge is power

  • Student empowerment:
    - You can choose
    - You can implement
    - You can migrate
    - You can succeed

  • Final message:
    - "Great job today!"
    - "You built it all!"
    - "Tomorrow: Advanced topics"
-->

<!-- end_slide -->

---

## Day 1 Complete! 🎉

### You've Built:

- ✅ REST API from scratch
- ✅ CRUD operations
- ✅ Bean validation
- ✅ API versioning
- ✅ Understanding of the full stack

<!-- pause -->

### Tomorrow: Day 2

- Jackson deep dive
- Security implementation
- OpenAPI documentation
- Production considerations

<!-- pause -->

**See you tomorrow at 9:00!** ☕

<!--
speaker_note: |
  DAY 1 WRAP-UP (2 minutes)

  • Congratulations:
    - "Impressive progress!"
    - "Built real API"
    - "Production patterns"
    - "No shortcuts"

  • Homework (optional):
    - Review today's code
    - Try bonus exercises
    - Think about questions
    - Rest well!

  • Tomorrow preview:
    - More advanced topics
    - Security focus
    - Real-world scenarios
    - Hands-on continues

  • Logistics:
    - Start time confirmation
    - Break reminder
    - Materials available
    - "Have a great evening!"
-->

<!-- end_slide -->
