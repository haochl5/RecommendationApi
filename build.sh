# Clean and build
./mvnw clean package -DskipTests

# List target contents
ls -la target/

# Copy JAR
cp target/*.jar app.jar

# Verify
ls -la app.jar

# Run the application
java -jar app.jar