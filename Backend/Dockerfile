FROM eclipse-temurin:17-jdk-alpine
# Set the
WORKDIR /app
# Copy the jar file into the container
COPY target/hrms-0.0.1-SNAPSHOT.jar hrms-0.0.1-SNAPSHOT.jar
# Make the port available to the outside world
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "hrms-0.0.1-SNAPSHOT.jar"]
