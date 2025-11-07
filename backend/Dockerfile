# Use Gradle image for building
FROM gradle:8-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle ./
COPY gradle.properties ./

# Copy source code
COPY src ./src

# Build the application (creates executable JAR)
RUN gradle bootJar -x test --no-daemon

# Production stage
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/profiling-service-*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]

