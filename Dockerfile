# Stage 1: Build Scala Play project
FROM hseeberger/scala-sbt:11.0.11_1.5.5_2.13.6 AS build-stage

# Set the working directory inside the container
WORKDIR /app

# Copy the project files to the container
COPY . /app

# Compile the Scala Play project and package it into a .jar file
RUN sbt dist

# Stage 2: Create the final image with the compiled Scala Play application
FROM adoptopenjdk:11-jre-hotspot

# Set the working directory inside the container
WORKDIR /app

# Copy the .jar file from the build-stage to the final image
COPY --from=build-stage /app/target/universal/pool-calendar-*.zip /app/

# Unzip the .zip file
RUN unzip pool-calendar-*.zip && rm pool-calendar-*.zip

# Set the entry point to start the Scala Play application
ENTRYPOINT ["pool-calendar-*/bin/pool-calendar"]

# Expose the port your Scala Play application listens on (replace 9000 with your actual port if needed)
EXPOSE 9000
