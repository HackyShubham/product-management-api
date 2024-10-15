# Product Management API

## Overview

The Product Management API is a Quarkus-based application designed to perform CRUD operations on products in a product management system. It leverages reactive programming with Mutiny and includes endpoints for stock availability checks and sorting products by price. The application uses an in-memory H2 database for persistence.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Exception Handling](#exception-handling)
- [License](#license)

## Features

- Create, Read, Update, and Delete (CRUD) operations for products.
- Check stock availability for a specific product.
- Retrieve products sorted by price.
- Implements immutability using Java Records for data transfer objects.
- Unit testing for API endpoints using Quarkus testing framework.
- Basic validation for product attributes.
- Exception handling for common errors.

## Technologies

- **Quarkus**: A Kubernetes-native Java framework tailored for GraalVM and OpenJDK HotSpot.
- **JPA**: Java Persistence API for managing relational data.
- **Mutiny**: A reactive programming library used for building asynchronous applications.
- **RESTEasy**: JAX-RS implementation for building RESTful web services.
- **H2 Database**: In-memory database for data persistence during development.
- **Lombok**: A Java library to reduce boilerplate code.

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle
- An IDE of your choice (e.g., IntelliJ, Eclipse)

### Clone the Repository

```bash
git clone https://github.com/yourusername/product-management-api.git
cd product-management-api
```

### Running the Application

```bash
./gradlew quarkusDev
```
This will start the application with live coding support. You can access the API at http://localhost:8080.

### Running the Tests
To run unit tests, use the following command:

```bash
./gradlew test
```
### API Endpoints

GET	    /products	    Retrieve all products
GET	    /products/{id}	Get a product by ID
POST    /products	    Create a new product
PUT	    /products/{id}	Update an existing product
DELETE	/products/{id}	Delete a product by ID
GET	    /products/{id}/check-stock	Check stock availability for a specific product
GET	    /products/sorted-by-price	Get all products sorted by price

### Exception Handling

The API uses standard HTTP status codes to indicate the outcome of operations:

200 OK: Successful retrieval or update.
201 Created: Successful creation of a new resource.
204 No Content: Successful deletion of a resource.
400 Bad Request: Invalid input or data validation issues.
404 Not Found: Resource not found for the specified ID.

### License
This project is licensed under the MIT License.
