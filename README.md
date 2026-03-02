# pool-calendar

A web application to download fixtures for the BDPL Pool league.

For local usage it will require mongodb running on your machine, and is recommended to just use `sbt run`

The service should then be accessible on ```localhost:9000``` in a web browser.

After any updates, test the docker container builds using the following command locally:

```docker build -t pool-calendar .```

To run the docker container, use the following command:

```docker run -p 9000:9000 pool-calendar```

The service should then be accessible on ```localhost:9000``` in a web browser. It won't be able to access mongo but should start up.

To stop the docker container, run ```docker ps``` to get the Container ID, and then run ```docker stop <CONTAINER ID>``` where ```<CONTAINER ID>``` is the actual ID of the container.


