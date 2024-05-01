# Readme

This is the repository of the `cinemap` project. It provides RESTful services
for movie CRUD operations.

This service is still under development.

# Run the repository

Currently, the repository is only tested locally and with Docker-PostgreSQL.

> [!NOTE]
> Due to the current setup the database is recreated with every restart.

## Pre-requisites

You are required to install the following software in order to run the repository:

- OpenJDK with at least version 22
- Maven 3.9.6
- Docker 26

## Build

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

You can access the API documentation by visiting [http://localhost:8080/](http://localhost:8080/).