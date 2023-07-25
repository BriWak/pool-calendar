# Use a lightweight base image with JRE
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY pool-calendar-assembly-1.1.jar /app/pool-calendar.jar

# Set the JVM options to limit memory usage (optional)
ENV JVM_OPTIONS="-Xmx512m -Xms512m"

# Expose port 10000
EXPOSE 10000

# Run the JAR file with JVM_OPTIONS properly expanded and bind to 0.0.0.0
CMD java $JVM_OPTIONS -Dhttp.address=0.0.0.0 -jar pool-calendar.jar
