# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /api

# Copy the Maven wrapper and pom.xml first to cache dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make the wrapper executable and download dependencies (improves Docker layer caching)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy the rest of the source code and build the application
COPY src ./src
# Build the app, skip tests (tests run in CI pipeline)
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the optimized production image
FROM eclipse-temurin:17-jre-alpine

# Add non-root user for security
RUN addgroup -S apigroup && adduser -S apiuser -G apigroup
USER apiuser

WORKDIR /api

# Copy the compiled jar from the build stage
COPY --from=build /api/target/*.jar library-management-api.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "library-management-api.jar"]
