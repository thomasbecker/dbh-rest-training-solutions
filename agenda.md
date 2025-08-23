# DBH REST with Jersey Training Agenda

**Duration**: 2 Days  
**Date**: August 26, 2025  
**Target**: Java 8 Environment

## Day 1: REST Foundations & Jersey Framework

### Morning Session (09:00 - 12:00)

#### 1. HTTP/REST Grundlagen (75 min)

**Slide**: `01-rest-fundamentals.md`

- HTTP basics recap
- REST architectural constraints
- REST maturity model (Richardson)
- **Exercise 01**: REST Basics (exercises/01-rest-basics/)

#### 2. Resource Orientation & REST Principles (60 min)

**Slide**: `02-resource-design.md`

- Resource identification (URIs)
- Uniform interface (HTTP methods)
- Stateless communication
- HATEOAS principles
- **Exercise**: Design REST resources for library system

#### 3. Idempotency & REST Alternatives (45 min)

**Slide**: `03-idempotency-alternatives.md`

- Safe vs idempotent operations
- REST vs GraphQL, gRPC, SOAP
- When to use which approach
- Trade-offs and decision factors

### Lunch Break (12:00 - 13:00)

### Afternoon Session (13:00 - 17:00)

#### 1. Plain Java with Jersey Setup (60 min)

**Slide**: `04-jersey-setup.md`

- Jersey framework introduction
- JAX-RS specification overview
- Project setup without Spring Boot
- Embedded Jetty server configuration
- **Exercise 02**: Jersey Setup (exercises/02-jersey-setup/)

#### 2. Building REST Controllers with Jersey (75 min)

**Slide**: `05-jersey-crud-exercise.md`

- JAX-RS annotations (@Path, @GET, @POST, etc.)
- Request/Response handling
- Content negotiation
- **Exercise 03**: Implement CRUD operations (exercises/03-jersey-crud/)
- Test-driven development with REST Assured

#### 3. Bean Validation with Jersey (30 min)

**Slides**: `06-bean-validation.md` & `07-bean-validation-exercise.md`

- JSR-303/Bean Validation basics
- Common annotations (@NotNull, @Size, @Email, @Pattern)
- Integration with Jersey (@Valid parameter)
- ValidationExceptionMapper implementation
- **Exercise 04**: Add validation to REST endpoints (exercises/04-bean-validation/)

#### 4. API Versioning Strategies (45 min)

**Slides**: `08-api-versioning.md` & `09-api-versioning-exercise.md`

- URI path versioning (/v1, /v2)
- Header versioning (Api-Version header)
- Media type versioning (application/vnd.company.v1+json)
- Deprecation strategies (Sunset headers)
- **Exercise 05**: Implement versioned endpoints (exercises/05-api-versioning/)

#### 5. Spring Boot Overview (30 min)

**Slide**: To be reviewed/created

- What Spring Boot abstracts
- Jersey with vs without Spring Boot
- When to use each approach
- Migration considerations

## Day 2: Advanced Topics, Security & Integration

### Morning Session (09:00 - 12:00)

#### 1. Jackson Integration with Jersey (90 min)

- Jackson configuration in Jersey
- Object mapping basics
- Handling complex types
- Exercise: JSON processing

#### 2. Advanced Jackson Features (60 min)

- Custom serializers/deserializers
- Annotations overview
- Performance considerations
- Exercise: Custom JSON handling

#### 3. Security Fundamentals (30 min)

- Basic Authentication implementation
- JWT Token handling
- API Key management

### Lunch Break (12:00 - 13:00)

### Afternoon Session (13:00 - 17:00)

#### 1. Security Implementation (60 min)

- Exercise: Secure REST endpoints
- Hands-on security configuration

#### 2. Showcases (45 min)

- Client Certificate Authentication (Showcase)
- OpenAPI/Swagger Documentation (Showcase)
- Container/Kubernetes Outlook (brief)

#### 3. Comprehensive Exercise (90 min)

- Build complete REST API
- Implement security
- Add versioning
- Document with OpenAPI

#### 4. Q&A Session (45 min)

- Open discussion
- Advanced topics
- Real-world scenarios

## Important Notes

- All exercises and examples are Java 8 compatible
- Uses Jersey 2.35 (not 3.x which requires Java 11+)
- Plain Java approach without Spring Boot
- Emphasis on hands-on exercises throughout
- Client Certificate Authentication is showcase only (no exercise)

## Completed Materials (Day 1)

### Slides Created

- ✅ 01-rest-fundamentals.md (75 min content)
- ✅ 02-resource-design.md (60 min content)
- ✅ 03-idempotency-alternatives.md (45 min content)
- ✅ 04-jersey-setup.md (60 min content)
- ✅ 05-jersey-crud-exercise.md (Exercise slides)
- ✅ 06-bean-validation.md (30 min content)
- ✅ 07-bean-validation-exercise.md (Exercise slides)
- ✅ 08-api-versioning.md (45 min content)
- ✅ 09-api-versioning-exercise.md (Exercise slides)

### Exercises Created

- ✅ Exercise 01: REST Basics (01-rest-basics)
- ✅ Exercise 02: Jersey Setup (02-jersey-setup)
- ✅ Exercise 03: Jersey CRUD (03-jersey-crud)
- ✅ Exercise 04: Bean Validation (04-bean-validation)
- ✅ Exercise 05: API Versioning (05-api-versioning)

### Solution Branches

- ✅ main: Base implementation
- ✅ solution/03-validation: Basic validation
- ✅ solution/04-bean-validation: Complete validation
- ✅ solution/05-api-versioning: Versioned API (V1/V2)
