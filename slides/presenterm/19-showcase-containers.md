---
title: "Showcase: Containerization & Cloud Deployment"
author: Thomas Becker
theme:
  name: dark
---

# Containerization & Cloud Deployment

## From JAR to Cloud

Duration: 15 minutes

<!--
speaker_note: |
  Final showcase - keep it high-level.
  Focus on the journey from local to cloud.
  Don't get lost in Kubernetes details.
-->

<!-- end_slide -->

---

## Why Containers for REST APIs?

### The Problem

**"Works on my machine!"** 🤷

- Different Java versions
- Missing dependencies  
- Configuration drift
- Environment variables

<!-- pause -->

### The Solution

**Container = App + Dependencies + Config** 📦

```dockerfile
FROM openjdk:8-jre-slim
COPY app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Runs the same everywhere!

<!--
speaker_note: |
  Containers solve the environment problem.
  Everything the app needs is included.
  No more "works on my machine".
-->

<!-- end_slide -->

---

## Docker Basics for Jersey Apps

### Simple Dockerfile

```dockerfile
# Multi-stage build for smaller image
FROM gradle:7.6-jdk8 AS builder
WORKDIR /app
COPY build.gradle .
COPY src ./src
RUN gradle build --no-daemon

# Runtime image
FROM openjdk:8-jre-slim
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Run as non-root user
RUN useradd -m appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

<!-- pause -->

**Multi-stage build** = Smaller production images (150MB vs 600MB)

<!--
speaker_note: |
  Multi-stage builds are best practice.
  Build stage has all tools.
  Runtime stage only has what's needed.
  Always run as non-root user.
-->

<!-- end_slide -->

---

## Building and Running

### Build the Image

```bash
# Build Docker image
docker build -t dbh-rest-api:1.0 .

# List images
docker images
# REPOSITORY      TAG    SIZE
# dbh-rest-api    1.0    152MB
```

<!-- pause -->

### Run the Container

```bash
# Run with port mapping
docker run -d \
  -p 8080:8080 \
  --name api \
  dbh-rest-api:1.0

# Check logs
docker logs api

# Test the API
curl http://localhost:8080/api/users
```

<!--
speaker_note: |
  Simple Docker commands.
  -d runs in background (daemon).
  -p maps ports host:container.
  Container name makes management easier.
-->

<!-- end_slide -->

---

## Environment Configuration

### Externalize Configuration

```dockerfile
# Accept environment variables
ENV SERVER_PORT=8080
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV JWT_SECRET=""

ENTRYPOINT ["java", \
  "-Dserver.port=${SERVER_PORT}", \
  "-Ddb.host=${DB_HOST}", \
  "-jar", "app.jar"]
```

<!-- pause -->

### Run with Environment Variables

```bash
docker run -d \
  -p 8080:8080 \
  -e DB_HOST=postgres \
  -e DB_PORT=5432 \
  -e JWT_SECRET=my-secret-key \
  dbh-rest-api:1.0
```

**Best Practice**: Never hardcode secrets in images!

<!--
speaker_note: |
  Configuration through environment variables.
  12-factor app principles.
  Secrets should come from external sources.
-->

<!-- end_slide -->

---

## Docker Compose for Local Development

### Complete Stack in One File

```yaml
# docker-compose.yml
version: '3.8'

services:
  api:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_HOST: postgres
      DB_USER: ${DB_USER:-dbuser}
      DB_PASSWORD: ${DB_PASSWORD:-secret}
    depends_on:
      - postgres
    networks:
      - app-network

  postgres:
    image: postgres:13-alpine
    environment:
      POSTGRES_DB: restapi
      POSTGRES_USER: ${DB_USER:-dbuser}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-secret}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - app-network

volumes:
  postgres-data:

networks:
  app-network:
```

<!--
speaker_note: |
  Docker Compose orchestrates multiple containers.
  Perfect for local development.
  One command starts everything.
-->

<!-- end_slide -->

---

## Docker Compose Commands

### Development Workflow

```bash
# Start everything
docker-compose up -d

# See logs
docker-compose logs -f api

# Scale API instances
docker-compose up -d --scale api=3

# Stop everything
docker-compose down

# Clean up everything (including volumes)
docker-compose down -v
```

<!-- pause -->

### Health Checks

```yaml
api:
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
    interval: 30s
    timeout: 3s
    retries: 3
```

<!--
speaker_note: |
  Docker Compose simplifies multi-container apps.
  Can scale services easily.
  Health checks ensure containers are ready.
-->

<!-- end_slide -->

---

## Introduction to Kubernetes

### Container Orchestration at Scale

**Docker**: Run containers on one machine
**Kubernetes**: Run containers across many machines

<!-- pause -->

### Key Concepts

- **Pod**: One or more containers
- **Service**: Network endpoint for pods
- **Deployment**: Manages pod replicas
- **Ingress**: External traffic routing

<!-- pause -->

```
Internet → Ingress → Service → Pods → Containers
```

<!--
speaker_note: |
  Kubernetes is complex - just introduce concepts.
  Don't dive deep - this is just awareness.
  Focus on the value proposition.
-->

<!-- end_slide -->

---

## Kubernetes Deployment

### Basic Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: rest-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: rest-api
  template:
    metadata:
      labels:
        app: rest-api
    spec:
      containers:
      - name: api
        image: dbh-rest-api:1.0
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          value: postgres-service
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
```

<!--
speaker_note: |
  Kubernetes uses YAML for configuration.
  Declarative - you describe desired state.
  Kubernetes makes it happen.
-->

<!-- end_slide -->

---

## Kubernetes Service

### Expose Deployment

```yaml
apiVersion: v1
kind: Service
metadata:
  name: rest-api-service
spec:
  selector:
    app: rest-api
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

<!-- pause -->

### Deploy to Kubernetes

```bash
# Apply configuration
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# Check status
kubectl get pods
kubectl get services

# Scale up
kubectl scale deployment rest-api --replicas=5
```

<!--
speaker_note: |
  Services provide stable networking.
  LoadBalancer gets external IP.
  kubectl is the CLI tool.
-->

<!-- end_slide -->

---

## Cloud Platform Comparison

### Where to Deploy?

| Platform | Strengths | Best For |
|----------|-----------|----------|
| **AWS ECS/EKS** | Full control, scalable | Large enterprises |
| **Google Cloud Run** | Serverless, auto-scale | Variable traffic |
| **Azure Container Instances** | Simple, integrated | Microsoft shops |
| **Heroku** | Developer-friendly | Prototypes, MVPs |
| **DigitalOcean Apps** | Simple, affordable | Small teams |

<!-- pause -->

**Start simple, scale when needed!**

<!--
speaker_note: |
  Many options available.
  Don't over-engineer initially.
  Start with managed services.
-->

<!-- end_slide -->

---

## CI/CD Pipeline Integration

### Automated Deployment

```yaml
# .gitlab-ci.yml
stages:
  - build
  - test
  - package
  - deploy

package:
  stage: package
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

deploy:
  stage: deploy
  script:
    - kubectl set image deployment/rest-api 
        api=$CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
  only:
    - main
```

<!-- pause -->

**Every commit** → **New container** → **Automatic deployment**

<!--
speaker_note: |
  CI/CD is essential for containers.
  Automate everything.
  Git commit triggers entire pipeline.
-->

<!-- end_slide -->

---

## Container Best Practices

### Security & Optimization

✅ **DO:**
- Use specific base image tags (`openjdk:8-jre-slim`, not `latest`)
- Multi-stage builds for smaller images
- Run as non-root user
- Scan for vulnerabilities
- Use health checks
- Set resource limits

<!-- pause -->

❌ **DON'T:**
- Store secrets in images
- Run as root
- Use large base images
- Ignore security updates
- Skip health checks

<!--
speaker_note: |
  Security is critical with containers.
  Small images = faster deploys, less attack surface.
  Always scan for vulnerabilities.
-->

<!-- end_slide -->

---

## Monitoring Containerized APIs

### Essential Metrics

```yaml
# Prometheus metrics endpoint
@GET
@Path("/metrics")
@Produces(MediaType.TEXT_PLAIN)
public String metrics() {
    return prometheusRegistry.scrape();
}
```

<!-- pause -->

### What to Monitor

- **Container**: CPU, memory, restarts
- **Application**: Request rate, latency, errors
- **Business**: Active users, transactions

<!-- pause -->

**Tools**: Prometheus + Grafana, ELK Stack, Datadog

<!--
speaker_note: |
  Observability is crucial in containers.
  Can't SSH into containers to debug.
  Need good metrics and logging.
-->

<!-- end_slide -->

---

## Demo: Local to Cloud

### Journey of Our REST API

1. 📦 **Package** - Build JAR file
2. 🐳 **Containerize** - Create Docker image
3. 🏃 **Run locally** - Test with Docker
4. 📊 **Compose** - Add database
5. ☁️ **Deploy** - Push to cloud
6. 🔄 **Scale** - Handle more traffic

<!-- pause -->

*[Instructor shows quick demonstration]*

<!--
speaker_note: |
  Quick 3-minute demo if time permits.
  Show docker build and run.
  Explain cloud deployment conceptually.
-->

<!-- end_slide -->

---

## Cost Considerations

### Container Economics

**Traditional Server**: $500/month (always on)
**Containers**: Pay for what you use

<!-- pause -->

### Example Monthly Costs

- **Development**: $20 (1 small container)
- **Startup**: $100 (3 containers, load balancer)
- **Growth**: $500 (10 containers, auto-scaling)
- **Enterprise**: $2000+ (multi-region, HA)

<!-- pause -->

**Auto-scaling** = Handle traffic spikes without overpaying

<!--
speaker_note: |
  Containers can save money.
  Auto-scaling prevents over-provisioning.
  Start small, grow as needed.
-->

<!-- end_slide -->

---

## Migration Path

### From Monolith to Containers

**Phase 1**: Containerize as-is
```dockerfile
FROM openjdk:8
COPY monolith.jar app.jar
```

<!-- pause -->

**Phase 2**: Externalize configuration
- Environment variables
- Config maps
- Secrets management

<!-- pause -->

**Phase 3**: Break into microservices
- User service
- Auth service
- Order service

<!-- pause -->

**Take it step by step!**

<!--
speaker_note: |
  Don't try to do everything at once.
  Containerize first, then optimize.
  Microservices are optional.
-->

<!-- end_slide -->

---

## Resources for Self-Study

### 📚 Essential Learning

- **Docker Docs**: docs.docker.com
- **Kubernetes.io**: Official tutorials
- **12 Factor App**: 12factor.net
- **Container Security**: OWASP Container Top 10

### 🛠️ Tools to Try

- **Docker Desktop** - Local development
- **Kind** - Kubernetes in Docker
- **Lens** - Kubernetes IDE
- **k9s** - Terminal-based Kubernetes UI
- **Dive** - Explore image layers

### 💡 Next Steps

1. Dockerize your current project
2. Try Docker Compose locally
3. Deploy to free tier (Heroku, Fly.io)
4. Learn Kubernetes basics (later)

<!--
speaker_note: |
  Start with Docker locally.
  Kubernetes can wait.
  Free tiers are great for learning.
-->

<!-- end_slide -->

---

## Key Takeaways

### Remember:

- 🐳 **Containers ensure consistency** - Dev = Prod
- 📦 **Package everything needed** - App + dependencies
- 🔧 **Configuration via environment** - 12-factor app
- 📈 **Scale horizontally** - More containers, not bigger

<!-- pause -->

### Start Your Container Journey:

1. Learn Docker basics first
2. Use Docker Compose for local dev
3. Deploy to managed platforms
4. Consider Kubernetes when needed

<!-- pause -->

**Questions about containers?**

<!--
speaker_note: |
  Wrap up the showcases.
  Emphasize starting simple.
  Containers are the future of deployment.
  
  Transition: "Now let's move to our comprehensive exercise
  where you'll build a complete REST API!"
-->

<!-- end_slide -->