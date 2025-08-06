FROM openjdk:17-jdk-slim

# Install necessary tools
RUN apt-get update && apt-get install -y \
    curl \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml for dependency caching
COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml ./

# Make maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached)
RUN ./mvnw dependency:go-offline -B || true

# Expose application port
EXPOSE 8080

# Default command (will be overridden)
CMD ["tail", "-f", "/dev/null"]