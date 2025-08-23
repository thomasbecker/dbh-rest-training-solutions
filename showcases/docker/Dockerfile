# Dockerfile for DBH REST API Training
# Optimized for Java 8 Jersey application

# Build stage - compile the application
FROM gradle:7.6-jdk8 AS builder

WORKDIR /app

# Copy gradle files first for better caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (cached if build.gradle unchanged)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN gradle build --no-daemon -x test

# Runtime stage - minimal image for production
FROM openjdk:8-jre-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy JAR from builder stage
COPY --from=builder /app/build/libs/*-all.jar app.jar

# Create directory for logs
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port (informational)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# JVM flags for container environment
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions \
  -XX:+UseCGroupMemoryLimitForHeap \
  -XX:MaxRAMFraction=2 \
  -XX:+UseG1GC \
  -Djava.security.egd=file:/dev/./urandom"

# Environment variables for configuration
ENV SERVER_PORT=8080
ENV LOG_LEVEL=INFO
ENV JWT_SECRET=""
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=restapi
ENV DB_USER=dbuser
ENV DB_PASSWORD=""

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]