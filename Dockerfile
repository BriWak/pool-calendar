# Stage 1: Build Scala Play project
FROM docker.io/library/adoptopenjdk:11-jdk-hotspot AS build-stage

# Set the working directory inside the container
WORKDIR /app

# Copy the project files to the container
COPY . /app

# Install Scala Build Tool (sbt)
RUN apt-get update && apt-get install -y curl && \
    curl -L -o sbt-1.5.5.deb https://dl.bintray.com/sbt/debian/sbt-1.5.5.deb && \
    dpkg -i sbt-1.5.5.deb && \
    apt-get update && apt-get install -y sbt

# Compile the Scala Play project and package it into a .jar file
RUN sbt dist

# Stage 2: Create the final production image
FROM docker.io/library/adoptopenjdk:11-jre-hotspot

# Set the working directory inside the container
WORKDIR /app

# Copy the .jar file from the build-stage to the production image
COPY --from=build-stage /app/target/universal/pool-calendar-*.zip /app/

# Unzip the distribution package
RUN unzip -q pool-calendar-*.zip && mv pool-calendar-*/lib/* /app/ && mv pool-calendar-*/bin/* /app/ && rm -r pool-calendar-*

# Expose the port that your Scala Play application listens to
EXPOSE 9000

# Start the Scala Play application
CMD ["./bin/pool-calendar", "-Dhttp.port=9000"]
