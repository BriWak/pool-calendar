# Use a lightweight base image with JRE
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# copy script and make it executable
COPY wait-for-mongo.sh /app/wait-for-mongo.sh
RUN chmod +x /app/wait-for-mongo.sh

# Copy the JAR and production.conf file into the container
COPY pool-calendar.jar /app/pool-calendar.jar
COPY conf/application.conf /app/application.conf
COPY conf/production.conf /app/production.conf

# Install netcat
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# Set the JVM options to limit memory usage (optional)
ENV JVM_OPTIONS="-Xmx512m -Xms512m"

# Expose port 9000
EXPOSE 9000

# Run the JAR file with JVM_OPTIONS properly expanded and bind to 0.0.0.0
CMD /app/wait-for-mongo.sh mongo 27017 java $JVM_OPTIONS -Dhttp.address=0.0.0.0 -Dconfig.resource=production.conf -jar pool-calendar.jar
