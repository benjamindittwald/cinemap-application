# Readme

This is the repository of the `cinemap` project. It provides RESTful services
for movie CRUD operations.

This service is still under development. Things that are not finished yet:

- Entity model - The current model is not sufficient and is only used for
  testing purposes
  - Restore InputDTOs in order to carefully handle null ID
  - Replace dbIds with UUIDs in DTOs
- Security - It needs to get decided if security is a matter or not. Maybe this
  service will not be accessible from the outside.
- Encryption
- Configuration

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