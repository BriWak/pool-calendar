# Use a lightweight base image with JRE
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY pool-calendar.jar /app/pool-calendar.jar

# Set the JVM options to limit memory usage (optional)
ENV JVM_OPTIONS="-Xmx512m -Xms512m"

# Run the JAR file with JVM_OPTIONS properly expanded
CMD java $JVM_OPTIONS -jar pool-calendar.jar
