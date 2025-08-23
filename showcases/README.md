# Showcases - Instructor Demonstrations

This directory contains demo materials for the Day 2 showcase presentations.
These are **instructor-only demonstrations** - attendees observe but don't implement.

## Showcase Topics (45 minutes total)

### 1. Client Certificate Authentication (15 min)
- Slides: `/slides/presenterm/17-showcase-client-certificates.md`
- Demo: Mutual TLS setup and testing
- Focus: When and why to use client certificates

### 2. OpenAPI/Swagger Documentation (15 min)
- Slides: `/slides/presenterm/18-showcase-openapi.md`
- Demo: Live Swagger UI with our API
- Focus: Auto-generated interactive documentation

### 3. Container/Kubernetes Deployment (15 min)
- Slides: `/slides/presenterm/19-showcase-containers.md`
- Demo: Docker build and run
- Focus: Journey from JAR to cloud

## Quick Setup for Demos

### Client Certificates Demo

```bash
# Generate test certificates (for demo only)
cd showcases/client-certs
./generate-certs.sh

# Test with curl
curl --cert client.pem --key client-key.pem https://localhost:8443/api/secure
```

### OpenAPI Demo

The main application already has Swagger annotations. To show Swagger UI:

```bash
# Start the application
cd instructor-solution
./gradlew run

# Open browser
open http://localhost:8080/swagger-ui/
```

### Docker Demo

```bash
# Build image
cd showcases/docker
docker build -t dbh-api:demo .

# Run container
docker run -p 8080:8080 dbh-api:demo

# Show docker-compose
docker-compose up -d
```

## Demo Tips

1. **Keep it simple** - These are showcases, not tutorials
2. **Have backups** - Screenshots in case live demo fails
3. **Time management** - 15 minutes each, strictly
4. **Focus on "why"** - When would they use this?
5. **Provide resources** - Links for self-study

## Materials Included

- `/client-certs/` - Certificate generation scripts
- `/docker/` - Dockerfile and docker-compose examples
- `/openapi/` - Sample OpenAPI annotations

## Important Notes

- These are **demonstration only** - no hands-on exercises
- Focus on showing what's possible, not implementation details
- Encourage attendees to explore these topics after training
- Provide links to documentation for self-study

## Running the Showcases

```bash
# Present each showcase
presenterm slides/presenterm/17-showcase-client-certificates.md
presenterm slides/presenterm/18-showcase-openapi.md
presenterm slides/presenterm/19-showcase-containers.md
```

Each showcase should take approximately 15 minutes including demo time.