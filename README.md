# Orders Project with Spring Boot, Kafka (KRaft), and Cloud SQL (PostgreSQL)

This project demonstrates a multi-module Java 21 application using Spring Boot that integrates with:
- **Google Cloud SQL (PostgreSQL)** for data persistence (via Spring Data JPA)
- **Apache Kafka (KRaft mode)** for event streaming
- **Dockerized Cloud SQL Auth Proxy** for secure, local connectivity to Cloud SQL

The project is organized into two microservices:
- **order-service**: Creates orders, saves them in PostgreSQL, and publishes events to Kafka.
- **payment-service**: Consumes Kafka events and processes payments.

This README provides full setup instructions for local development on Windows.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Structure](#project-structure)
3. [Configuration Details](#configuration-details)
    - [application.yml (Order Service)](#applicationyml-order-service)
    - [application.yml (Payment Service)](#applicationyml-payment-service)
4. [Dockerized Cloud SQL Auth Proxy](#dockerized-cloud-sql-auth-proxy)
5. [Running the Application Locally](#running-the-application-locally)
6. [Testing the Setup](#testing-the-setup)
7. [Additional Notes](#additional-notes)
8. [Further Resources](#further-resources)

---

## Prerequisites

- **Java 21**: Install a JDK 21 distribution (e.g., Eclipse Temurin, Azul Zulu).
- **Gradle**: The project uses the Gradle wrapper (Gradle 8.x).
- **Docker**: Docker Desktop (or Docker Engine) installed on your machine.
- **Google Cloud Project**: A project with Cloud SQL (PostgreSQL) enabled.
- **Service Account**: A dedicated service account with roles:
    - `Cloud SQL Admin` (or `Cloud SQL Client`)
    - `Cloud SQL Instance User`
- **service_account.json**: Download the JSON key for the service account. **Keep it secure.**

---

## Project Structure

```
orders-project
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── settings.gradle
├── build.gradle              # Root build file
├── order-service/           # Order microservice
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com.example.orderservice/
│           │       ├── controller/       # OrderController (uses DTOs)
│           │       ├── dto/              # OrderRequest & OrderResponse
│           │       ├── exception/        # Custom exceptions
│           │       ├── model/            # Order entity
│           │       ├── repository/       # OrderRepository
│           │       └── service/          # OrderService
│           └── resources/
│               └── application.yml
└── payment-service/         # Payment microservice (similar structure)
    ├── build.gradle
    └── src/
        └── main/
            ├── java/
            │   └── com.example.paymentservice/
            │       ├── config/           # KafkaConsumerConfig
            │       ├── consumer/         # OrderEventConsumer
            │       └── model/            # Payment entity
            └── resources/
                └── application.yml
```

---

## Configuration Details

### application.yml (Order Service)

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: myuser
    password: mypassword
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

kafka:
  bootstrap-servers: localhost:9092
  producer:
    key-serializer: org.apache.kafka.common.serialization.StringSerializer
    value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  properties:
    security.protocol: SASL_SSL
```

### application.yml (Payment Service)

```yaml
server:
  port: 8082

spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: payment-service-group
```

---

## Dockerized Cloud SQL Auth Proxy

To securely connect your local application to a Cloud SQL PostgreSQL instance, use the Cloud SQL Auth Proxy in Docker.

### Steps:
1. **Place `service_account.json`** in a secure directory (e.g., `secure-dir`).
2. **Create a `docker-compose.yml`** file with the following content:

```yaml
version: '3.8'

services:
  cloudsql-proxy:
    container_name: cloudsql-proxy
    image: gcr.io/cloud-sql-connectors/cloud-sql-proxy:latest
    command: >
      <PROJECT_ID>:<REGION>:<INSTANCE_NAME> --credentials-file=/config/service_account.json --address=0.0.0.0 --port=5432
    volumes:
      - ./secure-dir/service_account.json:/config/service_account.json:ro
    ports:
      - "5432:5432"
```

3. **Start the Proxy**:
   ```bash
   docker compose up -d
   ```
   Check logs with:
   ```bash
   docker logs cloudsql-proxy
   ```

---

## Running the Application Locally

1. **Ensure the Cloud SQL Auth Proxy is running**.
2. **Build the Multi-Module Project**:
   ```bash
   gradlew.bat clean build
   ```
3. **Start the Order Service**:
   ```bash
   gradlew.bat :order-service:bootRun
   ```
4. **Start the Payment Service** (in another terminal):
   ```bash
   gradlew.bat :payment-service:bootRun
   ```

---

## Testing the Setup

- **Create an Order**:
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"description":"Test order"}' http://localhost:8081/orders
  ```
- **Retrieve the Order**:
  ```bash
  curl http://localhost:8081/orders/<externalId>
  ```
- **Check Payment Service Logs** to ensure the event was consumed.

---

## Further Resources

- [Cloud SQL Auth Proxy Documentation](https://cloud.google.com/sql/docs/postgres/connect-admin-proxy)
- [Spring Boot Kafka Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-messaging)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
```

