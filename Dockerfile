# Use a lightweight base image with JRE
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR and production.conf file into the container
COPY pool-calendar.jar /app/pool-calendar.jar
COPY conf/application.conf /app/application.conf
COPY conf/production.conf /app/production.conf

# Set the JVM options to limit memory usage (optional)
ENV JVM_OPTIONS="-Xmx512m -Xms512m"

# Expose port 9000
EXPOSE 9000

# Run the JAR file with JVM_OPTIONS properly expanded and bind to 0.0.0.0
CMD java $JVM_OPTIONS -Dhttp.address=0.0.0.0 -Dconfig.resource=production.conf -jar pool-calendar.jar
