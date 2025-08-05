# User API - Spring Boot RESTful Service

A comprehensive RESTful API built with Spring Boot for managing user data with full CRUD operations. This project includes Docker support, comprehensive testing, and production-ready configuration.

## 🚀 Features

- **Create** new users
- **Read** user data (all users or by ID)
- **Update** existing users
- **Delete** users
- File-based data persistence (JSON format)
- RESTful API design
- JSON request/response format
- **Docker containerization**
- **Environment-based configuration**
- **Comprehensive test coverage** (38 tests)
- **Production-ready setup**
- **Health checks**
- **Postman collection for API testing**

## 📁 Project Structure

```
├── Dockerfile                           # Docker container configuration
├── docker-compose.yml                   # Docker Compose setup
├── .env.dev                            # Development environment variables
├── .env.prod                           # Production environment variables
├── .dockerignore                       # Docker ignore file
├── pom.xml                             # Maven configuration
├── README.md                           # Project documentation
├── docs/
│   └── postman/                        # Postman collection for API testing
│       └── User-API-Basic.postman_collection.json
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           └── userapi/
    │   │               ├── UserApiApplication.java     # Main application class
    │   │               ├── controller/
    │   │               │   └── UserController.java    # REST API endpoints
    │   │               ├── model/
    │   │               │   └── User.java              # User data model
    │   │               └── repository/
    │   │                   └── UserRepository.java    # Data access layer
    │   └── resources/
    │       └── application.properties                  # Spring Boot configuration
    └── test/
        └── java/
            └── com/
                └── example/
                    └── userapi/
                        ├── controller/
                        │   └── UserControllerIntegrationTest.java  # Controller tests
                        ├── integration/
                        │   └── UserApiIntegrationTest.java         # Full integration tests
                        └── repository/
                            └── UserRepositoryTest.java             # Repository unit tests
```

## 📋 Requirements

### Local Development
- Java 11 or higher
- Maven 3.6 or higher

### Docker Development
- Docker 20.10 or higher
- Docker Compose 1.29 or higher

## User Model

Each user has the following properties:
- `id` (Long) - Auto-generated unique identifier
- `name` (String) - User's full name
- `email` (String) - User's email address
- `age` (Integer) - User's age

## API Endpoints

### Base URL
```
http://localhost:8080/api/users
```

### Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/users` | Get all users | None | Array of User objects |
| GET | `/api/users/{id}` | Get user by ID | None | User object |
| POST | `/api/users` | Create new user | User object (without ID) | Created User object |
| PUT | `/api/users/{id}` | Update user by ID | User object | Updated User object |
| DELETE | `/api/users/{id}` | Delete user by ID | None | No content (204) |

### Example User JSON

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 30
}
```

## 🚀 Getting Started

### Option 1: Local Development

#### 1. Build the Project
```bash
mvn clean install
```

#### 2. Run Tests
```bash
mvn test
```

#### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Option 2: Docker Development (Recommended)

#### 1. Build the JAR file
```bash
mvn clean package -DskipTests
```

#### 2. Run with Docker Compose (Development)
```bash
# Using development environment
docker-compose --env-file .env.dev up --build
```

#### 3. Run with Docker Compose (Production)
```bash
# Using production environment
docker-compose --env-file .env.prod up --build -d
```

#### 4. Stop the application
```bash
docker-compose down
```

#### 5. View logs
```bash
docker-compose logs -f userapi
```

### 3. Test the API

#### Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "age": 30
  }'
```

#### Get All Users
```bash
curl http://localhost:8080/api/users
```

#### Get User by ID
```bash
curl http://localhost:8080/api/users/1
```

#### Update a User
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "email": "john.smith@example.com",
    "age": 31
  }'
```

#### Delete a User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

## 💾 Data Persistence

User data is stored in a `users.json` file:
- **Local development**: Project root directory
- **Docker**: `/app/data/users.json` (mounted as persistent volume)

The file is automatically created when the first user is added and updated with each CRUD operation.

Example `users.json` structure:
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "age": 30
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "age": 25
  }
]
```

## ⚙️ Configuration

### Application Properties
The application configuration is located in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Application Configuration  
spring.application.name=userapi

# Logging Configuration
logging.level.com.example.userapi=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

### Environment Variables

#### Development (.env.dev)
```bash
SPRING_PROFILE=dev
APP_PORT=8080
USER_DATA_FILE=/app/data/users.json
LOG_LEVEL=INFO
APP_LOG_LEVEL=DEBUG
JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC
```

#### Production (.env.prod)
```bash
SPRING_PROFILE=prod
APP_PORT=8080
USER_DATA_FILE=/app/data/users.json
LOG_LEVEL=WARN
APP_LOG_LEVEL=ERROR
JAVA_OPTS=-Xmx1024m -Xms512m -XX:+UseG1GC -XX:+OptimizeStringConcat
```

## 📦 Dependencies

The project uses the following key dependencies:
- **Spring Boot Starter Web** - For REST API functionality
- **Jackson Databind** - For JSON serialization/deserialization
- **Spring Boot Starter Test** - For comprehensive testing support
- **JUnit 5** - Modern testing framework
- **Mockito** - Mocking framework for unit tests
- **Spring Test** - Integration testing support

## Error Handling

The API returns appropriate HTTP status codes:
- `200 OK` - Successful GET, PUT operations
- `201 Created` - Successful POST operation
- `204 No Content` - Successful DELETE operation
- `400 Bad Request` - Invalid request data
- `404 Not Found` - User not found

## 🧪 Testing

The project includes comprehensive test coverage with **38 tests** across three categories:

### Unit Tests (19 tests)
- **UserRepositoryTest**: Repository layer testing with edge cases
- Tests for CRUD operations, data validation, and error handling
- Coverage for null values, special characters, and boundary conditions

### Integration Tests (5 tests)
- **UserApiIntegrationTest**: Full end-to-end API testing
- Real HTTP requests with TestRestTemplate
- Complete application context testing

### Controller Tests (14 tests)
- **UserControllerIntegrationTest**: MockMvc-based controller testing
- JSON serialization/deserialization validation
- HTTP status code and error handling verification

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserRepositoryTest

# Run tests with coverage
mvn test jacoco:report
```

## 📬 Postman Collection

Import the Postman collection for easy API testing:
- **File**: `docs/postman/User-API-Basic.postman_collection.json`
- **Base URL**: `http://localhost:8080`
- **Includes**: All CRUD operations with sample data

## 🐳 Docker Features

- **Multi-stage build** for optimized image size
- **Non-root user** for enhanced security
- **Health checks** for container monitoring
- **Persistent volumes** for data storage
- **Environment-based configuration**
- **Production-ready** container setup

## 📝 Development Notes

- IDs are auto-generated starting from 1
- The application loads existing users from `users.json` on startup
- All CRUD operations automatically persist changes to the file
- The file is created automatically if it doesn't exist
- Docker volumes ensure data persistence across container restarts

## 🚀 Future Enhancements

Potential improvements for production use:
- Add input validation with Bean Validation
- Implement proper database integration (PostgreSQL/MySQL)
- Add authentication and authorization (JWT/OAuth2)
- Include API documentation with OpenAPI/Swagger
- Implement pagination for large datasets
- Add centralized logging and monitoring (ELK stack)
- Implement caching (Redis)
- Add rate limiting and API throttling
- Include metrics and observability (Micrometer/Prometheus)
- Add CI/CD pipeline configuration
- Implement database migrations (Flyway/Liquibase)

## License

This project is created for educational purposes.
