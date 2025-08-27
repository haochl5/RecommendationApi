FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Debug: List what was created
RUN ls -la target/

# Copy JAR to a known name
RUN cp target/*.jar app.jar

# Verify the file exists
RUN ls -la app.jar

EXPOSE 8080

# Use the copied JAR
ENTRYPOINT ["java", "-jar", "app.jar"]