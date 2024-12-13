# Dynamic Pricing Project

## Overview

The **Dynamic Pricing** project is designed to manage and calculate prices based on a set of product parameters such as
product ID, brand ID, price list, application date, etc. It integrates with a MongoDB database and exposes its
functionality through REST APIs using Spring Boot.

This repository provides a solution for managing dynamic prices for products, allowing businesses to set different
pricing rules based on various parameters and apply them at different times.

## Table of Contents

- [Technologies](#technologies)
- [Features](#features)
- [Installation](#installation)
- [Configuration](#configuration)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Technologies

This project uses the following technologies:

- **Spring Boot 3.3.5**: A Java-based framework to build the application.
- **MongoDB**: A NoSQL database to store product and pricing information.
- **Testcontainers**: For running MongoDB containers during tests.
- **Swagger/OpenAPI**: For API documentation and testing.
- **JUnit 5**: For unit testing and integration testing.

## Features

- **Price Calculation**: The system can calculate product prices based on different rules (e.g., based on product ID,
  brand ID, and application date).
- **REST API**: Exposes endpoints to interact with the system.
- **Database Integration**: MongoDB is used for storing price data.

## Installation

### Prerequisites

To run this project, you need:

- **Java 17** check with (java -version && echo $JAVA_HOME).
- **Maven 3.9.9** to build the project.
- **Docker 27.3.1** to run tests together with maven or launch the project with docker-compose.

### Steps

#### Using Docker Compose

1. Clone the repository:

    ```bash
    git clone https://github.com/larismendi/dynamic-pricing.git
    cd dynamic-pricing
    ```

2. Build the project with Maven:

    ```bash
    mvn clean install
    ```

3. Start the application with the following docker-compose command:

   ```bash
   docker-compose up --build
   ```

#### Manually

1. Clone the repository:

    ```bash
    git clone https://github.com/larismendi/dynamic-pricing.git
    cd dynamic-pricing
    ```

2. Build the project with Maven:

    ```bash
    mvn clean install
    ```

3. If you're using Docker for MongoDB, you can start a container with the following command:

    ```bash
    docker run --name mongo -d -p 27017:27017 mongo:6.0
    ```

4. To run the application with the `test` profile:

    ```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=test
    ```

## Configuration

The application requires the following properties for configuration:

- **`spring.data.mongodb.uri`**: The MongoDB URI connection string (e.g., `mongodb://localhost:27017/dynamic-pricing`).
- **`spring.profiles.active`**: Active profile for the application (e.g., `test` for testing, `dev` for development).

You can configure these in the `application.properties`, or depending on profile `application-dev.properties`
or `application-test.properties` file located in `src/main/resources`.

## Testing

To run the tests, simply use the following Maven command:

   ```bash
   mvn test
   ```

The tests use JUnit 5 and Testcontainers to run MongoDB in a Docker container during the tests. Make sure Docker is
running on your machine if you're using Testcontainers for integration tests.

### Unit Testing

The application includes unit tests to validate individual components and business logic.

### Integration Testing

The integration tests test the full flow of the application, including database interactions, API responses, and error
handling.

## Code Coverage with JaCoCo

This project uses **JaCoCo** to measure test coverage. The generated report provides insights into which parts of the
code are well-tested and which are not.

### Generate Coverage Report

To generate the JaCoCo report, run the following Maven command:

   ```bash
   mvn clean test jacoco:report
   ```

### Report Location

After running the command, the coverage report will be generated in the following directory:

```bash
target/site/jacoco/index.html
```

### Viewing the Report

1. Open the generated index.html file in a web browser.

2. Navigate through the interactive report to analyze the code coverage for classes, methods, and lines.

### Example Output

A successful run of the command might produce an output like this:

```csharp
[INFO] --- jacoco-maven-plugin:0.8.8:report (default-cli) @ dynamicpricing ---
[INFO] Analyzing structure of the code with class directories [target/classes]
[INFO] Writing JaCoCo report to file target/site/jacoco/index.html
```

### Improving Test Coverage

Review the report to identify untested areas and add additional tests to improve coverage. Maintaining high coverage
ensures better code quality and fewer bugs.

## API Documentation

The API is documented using Swagger/OpenAPI. You can view the API documentation by running the application and
navigating to the following URL:

   ```bash
   http://localhost:8080/swagger-ui.html
   ```

This provides a detailed view of all available endpoints, their parameters, and example responses.

## Example Request

### **GET** `/api/price`

**Query Parameters:**

| Parameter       | Type    | Description                                |
|------------------|---------|--------------------------------------------|
| `productId`      | `int`   | ID of the product (required).              |
| `brandId`        | `int`   | ID of the brand (required).                |
| `applicationDate`| `string`| Date and time in ISO 8601 format (required).|

#### Example Successful Request With UTC:

**Request URL:**

```bash
curl --location 'http://localhost:8080/api/price?productId=35455&brandId=1&applicationDate=2020-06-14T22:00:00Z'
```

**Response Body:**

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00Z",
  "endDate": "2020-12-31T23:59:59Z",
  "price": 35.5,
  "currency": "EUR"
}
```

**Response Code:**

```plaintext
200 OK
```

#### Example Successful Request With +02:00 Zone Time:

**Request URL:**

```bash
curl --location 'http://localhost:8080/api/price?productId=35455&brandId=1&applicationDate=2020-06-15T00:00:00%2B02:00'
```

**Response Body:**

```json
{
   "productId": 35455,
   "brandId": 1,
   "priceList": 1,
   "startDate": "2020-06-14T02:00+02:00",
   "endDate": "2021-01-01T01:59:59+02:00",
   "price": 35.5,
   "currency": "EUR"
}
```

**Response Code:**

```plaintext
200 OK
```

#### Example Error Request:

**Request URL:**

```bash
curl --location 'http://localhost:8080/api/price?productId=0&brandId=0&applicationDate=2020-06-14T22:00:00Z'
```

**Response Body:**

```json
{
  "productId": "Product ID must be positive",
  "brandId": "Brand ID must be positive"
}
```

**Response Code:**

```plaintext
400 Bad Request
```

## Health Check

### **GET** `/health`

This endpoint can be used to verify if the application is running and healthy.

#### Example Request URL

```bash
curl --location 'http://localhost:8080/health'
```

**Response Body:**

```plaintest
Application is healthy
```

**Response Code:**

```plaintest
200 OK: The application is healthy.
```

## Contributing

We welcome contributions to this project! To contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature (git checkout -b feature/your-feature-name).
3. Commit your changes (git commit -am 'Add new feature').
4. Push to your branch (git push origin feature/your-feature-name).
5. Create a pull request to merge your feature into the main repository.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

