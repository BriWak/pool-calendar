# Use the OpenJDK 11 base image
FROM openjdk:11

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . /app

# Install MongoDB (assuming you use MongoDB as the database)
# Add the MongoDB repository and update package lists
RUN apt-get update && \
    apt-get install -y gnupg && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 656408E390CFB1F5 && \
    echo "deb http://repo.mongodb.org/apt/debian buster/mongodb-org/4.4 main" | tee /etc/apt/sources.list.d/mongodb-org-4.4.list && \
    apt-get update && \
    apt-get install -y mongodb-org && \
    apt-get clean

# Expose the Play application port
EXPOSE 9000

# Start the Play application when the container runs
CMD ["sbt", "run"]