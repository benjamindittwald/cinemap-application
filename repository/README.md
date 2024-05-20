# Readme

This is the repository ui of the `cinemap` project. It provides a UI for
operating on the repository API services.

This service is still under development.

# Run the repository

Currently, the repository ui is only tested locally.

## Pre-requisites

You are required to install the following software in order to run the
repository:

- OpenJDK with at least version 22
- Maven 3.9.6
- Docker 26

A running instance of the cinemap repository must be accessible.

## Build

The repository connection is configured via env variable. The following is an
example for local development purposes.

In order to use the TMDB API features you need to create an account
at [TMDB](https://developer.themoviedb.org/docs/getting-started) and
set 'de.cinemap.repositoryui.tmdb.api.readtoken' with your TMDB API read token.

```
de.cinemap.repository.server.port=8080
de.cinemap.repository.server.url=http://localhost
de.cinemap.repositoryui.server.port=8081
de.cinemap.repositoryui.tmdb.api.readtoken=YOUR TOKEN

```

Build the application with Maven:

```

mvn clean package

```

## Run

Run the application with Maven:

```

mvn spring-boot:run

```

## Test it

You can access the API documentation by
visiting [http://localhost:8080/](http://localhost:8080/).

# Attribution

* The movie fallback
  poster: https://www.pexels.com/photo/delicious-pink-popcorns-in-close-shot-7676085/