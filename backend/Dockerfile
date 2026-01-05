# Use Gradle image for building
FROM gradle:8-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy Gradle files first for better caching
COPY build.gradle settings.gradle ./
COPY gradle.properties ./

# Download dependencies separately (cached layer)
# This step will be cached if dependencies don't change
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build the application (creates executable JAR)
# Add retries and better network handling
RUN gradle bootJar -x test --no-daemon \
    --refresh-dependencies \
    --max-workers=2 \
    -Dorg.gradle.daemon=false \
    -Dorg.gradle.parallel=false

# Production stage
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/profiling-service-*.jar app.jar

# Expose port 9090
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]

