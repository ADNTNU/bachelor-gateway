![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)


# Bachelor Gateway
The **Bachelor Gateway** acts as the unified entry point for user authentication and proxying gRPC API calls, route REST calls and handle WebSocket. It centralizes security concerns and metadata propagation before forwarding requests to downstream services.

## Features
- **JWT Authentication**: Validates incoming JWTs and extracts user claims (company ID, scopes).
- **Scope-Based Authorization**: Enforces method-level and route level access control.
- **gRPC Reflection**: Supports server reflection for service discovery.
- **Metadata Propagation**: Forwards authentication headers to downstream gRPC services seamlessly.
- **REST Endpoints**: Exposes HTTP controllers for tasks like WebSocket token issuance.
- **WebSocket Support**: Issues and manages tokens for secure WebSocket connections via the `/ws-auth-token` endpoint.


## Prerequisites
- Java 21 or higher
- Maven 3.9.9
- Docker and Docker Compose

### Getting started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bachelor-gateway
   ```

2. **Generate gRPC source files**
   ```bash
   mvn clean generate-sources
   ```

3. **Use Docker Compose** (optional)
   ```bash
   docker-compose up --build
   ```

4. **Build & run locally**
   ```bash
   mvn spring-boot:run
   ```

## IntelliJ Setup

If you're using IntelliJ IDEA, mark the generated sources as follows:

1. Go to **File** > **Project Structure** > **Modules**.
2. Locate `target/generated-sources/protobuf` (and any subfolders).
3. Right-click and select **Mark Directory as** > **Generated Sources Root**.


## Configuration

All configurable properties are in `src/main/resources/application.yml` (or `application.properties`):
