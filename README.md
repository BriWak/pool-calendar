# pool-calendar

A web application to download fixtures for the BDPL Pool league.

For local usage it will require mongodb running on your machine.

After any updates run ```sbt clean assembly``` before creating the docker container from the Dockerfile.

The jar file can then be tested locally with the command ```java -jar pool-calendar.jar```

It can also be tested inside the docker container using the following commands locally:

```docker build -t pool-calendar .```

```docker run -p 9000:9000 pool-calendar```

To stop the docker container, run ```docker ps``` to get the Container ID, and then run ```docker stop <CONTAINER ID>``` where ```<CONTAINER ID>``` is the actual ID of the container.

The service should then be accessible on ```localhost:9000``` in a web browser.
