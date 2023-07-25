# Use a base image with the desired Scala and sbt version
FROM openjdk:11

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . /app

# Install MongoDB (assuming you use MongoDB as the database)
# Add any other dependencies you may need here
RUN apt-get update && \
    apt-get install -y mongodb && \
    apt-get clean

# Expose the Play application port
EXPOSE 9000

# Start the Play application when the container runs
CMD ["sbt", "run"]
