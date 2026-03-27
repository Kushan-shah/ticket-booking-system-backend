# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Use a volume to store temp files (needed by Tomcat)
VOLUME /tmp

# Build arguments
ARG JAR_FILE=target/*.jar

# Copy the artifact into the image
COPY ${JAR_FILE} app.jar

# Expose port
EXPOSE 8080

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
