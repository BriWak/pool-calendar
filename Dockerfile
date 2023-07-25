# Use the base image with Java and SBT pre-installed
FROM hseeberger/scala-sbt:11.0.11_1.5.5_2.13.6

# Set the working directory inside the container
WORKDIR /app

# Copy the code into the container
COPY . /app

# Set the JVM options to limit memory usage
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Set the SBT options to limit memory usage
ENV SBT_OPTS="-Xmx256m -Xss256k"

# Build your project (if necessary)
RUN sbt compile

# Run your application
CMD ["sbt", "-mem", "512", "run"]
