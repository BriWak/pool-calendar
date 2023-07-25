# Use a lightweight base image with JRE
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/scala-2.13/pool-calendar.jar /app/pool-calendar.jar

# Set the JVM options to limit memory usage
ENV JAVA_OPTS="-Xmx128m -Xms64m"

# Run your application
CMD ["java", "-jar", "your-project.jar"]
