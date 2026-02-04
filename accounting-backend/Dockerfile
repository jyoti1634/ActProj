# Multi-stage Dockerfile for Spring Boot application
# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# copy pom first for dependency resolution caching
COPY pom.xml ./
# copy source and build (no mvnw/.mvn assumed)
COPY src/ src/
# Build the application (skip tests for speed; remove -DskipTests for CI with tests)
RUN mvn -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# copy jar built in previous stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Recommended: supply JVM options via env variable (e.g. -Xmx512m)
ENV JAVA_OPTS="-Xms128m -Xmx512m"
# Use the PORT provided by the platform at runtime (default 8080 if missing)
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
