# User API - Spring Boot RESTful Service

[![GitHub Repository](https://img.shields.io/badge/GitHub-java--simple--user--mgmt-blue)](https://github.com/bayoehafiz/java-simple-user-mgmt)

A comprehensive RESTful API built with Spring Boot for managing user data with full CRUD operations. This project includes Docker support, comprehensive testing, and production-ready configuration.

## 🚀 Features

### Core User Management
- **Create** new users
- **Read** user data (all users, by ID, or by username)
- **Update** existing users
- **Delete** users
- **Username-based search** for efficient user lookup

### Authentication & Security
- **User registration** with role-based access control (RBAC)
- **JWT-based authentication** for secure API access
- **Password encryption** using BCrypt
- **Role management** (USER, ADMIN, MANAGER)
- **Username uniqueness validation**

### Technical Features
- File-based data persistence (JSON format)
- RESTful API design
- JSON request/response format
- **Docker containerization**
- **Environment-based configuration**
- **Comprehensive test coverage** (70+ tests including JWT security tests)
- **Production-ready setup**
- **Health checks**
- **Complete Postman collection for API testing**

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
│   │               ├── repository/
│   │               │   └── UserRepository.java    # Data access layer
│   │               └── security/
│   │                   └── JwtUtil.java           # JWT token management
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
                        ├── repository/
                        │   ├── UserRepositoryTest.java             # Repository unit tests
                        │   └── FindByUsernameIntegrationTest.java  # Username search tests
                        └── security/
                            └── JwtUtilTest.java                     # JWT security tests
```

## 📋 Requirements

### Local Development
- Java 17 or higher (LTS)
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
- `username` (String) - Unique username for authentication
- `password` (String) - Encrypted password (write-only)
- `role` (Role) - User role (USER, ADMIN, MANAGER)
- `enabled` (Boolean) - Account status (default: true)

## API Endpoints

### User Management Endpoints
**Base URL**: `http://localhost:8080/api/users`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/users` | Get all users | None | Array of User objects |
| GET | `/api/users/{id}` | Get user by ID | None | User object |
| GET | `/api/users/username/{username}` | Get user by username | None | User object |
| POST | `/api/users` | Create new user | User object (without ID) | Created User object |
| PUT | `/api/users/{id}` | Update user by ID | User object | Updated User object |
| DELETE | `/api/users/{id}` | Delete user by ID | None | No content (204) |

### Authentication Endpoints
**Base URL**: `http://localhost:8080/api/auth`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/auth/register` | Register new user | RegisterRequest | JWT token + user info |
| POST | `/api/auth/login` | Authenticate user | LoginRequest | JWT token + user info |

### Example Request/Response JSON

#### Basic User JSON
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 30
}
```

#### Registration Request JSON
```json
{
  "name": "New User",
  "email": "newuser@example.com",
  "age": 26,
  "username": "newuser",
  "password": "securePassword123",
  "role": "USER"
}
```

#### Login Request JSON
```json
{
  "username": "newuser",
  "password": "securePassword123"
}
```

#### Authentication Response JSON
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXd1c2VyIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2OTc2MTYxMDAsImV4cCI6MTY5NzYxOTcwMH0...",
  "user": {
    "id": 1,
    "username": "newuser",
    "name": "New User",
    "email": "newuser@example.com",
    "role": "USER"
  }
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

#### Get User by Username
```bash
curl http://localhost:8080/api/users/username/johndoe
```

#### Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New User",
    "email": "newuser@example.com",
    "age": 26,
    "username": "newuser",
    "password": "securePassword123",
    "role": "USER"
  }'
```

#### Login User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "securePassword123"
  }'
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
JWT_SECRET=secureJwtSecretKeyForDevelopmentUse123456789
JWT_EXPIRATION=86400
JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC
```

#### Production (.env.prod)
```bash
SPRING_PROFILE=prod
APP_PORT=8080
USER_DATA_FILE=/app/data/users.json
LOG_LEVEL=WARN
APP_LOG_LEVEL=ERROR
JWT_SECRET=${YOUR_SECURE_JWT_SECRET_KEY_HERE}
JWT_EXPIRATION=86400
SSL_ENABLED=true
REQUIRE_SSL=true
JAVA_OPTS=-Xmx1024m -Xms512m -XX:+UseG1GC -XX:+OptimizeStringConcat
```

### Security Configuration

#### JWT Authentication
- **JWT Secret**: Set via `JWT_SECRET` environment variable (required for production)
- **Token Expiration**: Configurable via `JWT_EXPIRATION` (default: 24 hours)
- **Secure Headers**: HSTS, X-Content-Type-Options enabled
- **Actuator Security**: Sensitive endpoints secured and restricted

#### Production Security Checklist
- ✅ JWT secrets managed via environment variables
- ✅ Password encryption using BCrypt
- ✅ Secure HTTP headers enabled
- ✅ Non-root Docker container user
- ✅ Actuator endpoints secured
- ✅ Input validation with comprehensive constraints
- ⚠️ SSL/TLS configuration (configure for production)

## 📦 Dependencies

The project uses the following key dependencies:

### Core Dependencies
- **Spring Boot Starter Web** - For REST API functionality
- **Jackson Databind** - For JSON serialization/deserialization  
- **Spring Boot Starter Test** - For comprehensive testing support

### Security & Authentication
- **Spring Boot Starter Security** - For authentication and authorization
- **JJWT API** - JWT token creation and validation
- **JJWT Implementation** - JWT processing implementation
- **JJWT Jackson** - JSON processing for JWT
- **BCrypt Password Encoder** - For secure password hashing

### Testing Framework
- **JUnit 5** - Modern testing framework
- **Mockito** - Mocking framework for unit tests
- **Spring Test** - Integration testing support
- **Spring Security Test** - Security testing utilities

## Error Handling

The API returns appropriate HTTP status codes:
- `200 OK` - Successful GET, PUT operations
- `201 Created` - Successful POST operation
- `204 No Content` - Successful DELETE operation
- `400 Bad Request` - Invalid request data
- `404 Not Found` - User not found

## 🧪 Testing

The project includes comprehensive test coverage with **70+ tests** across four categories:

### Repository Tests (42 tests)
- **UserRepositoryTest**: Repository layer testing with edge cases (32 tests)
- **FindByUsernameIntegrationTest**: Username search functionality (10 tests)
- Tests for CRUD operations, data validation, and error handling
- Coverage for null values, special characters, and boundary conditions

### Security Tests (19 tests)
- **JwtUtilTest**: Comprehensive JWT utility testing
- Token generation, validation, expiration handling
- Security scenarios: malformed tokens, wrong signatures, expired tokens
- Edge cases: special characters, long usernames, Unicode support
- Authentication and authorization flow testing

### Integration Tests (5 tests)
- **UserApiIntegrationTest**: Full end-to-end API testing
- Real HTTP requests with TestRestTemplate
- Complete application context testing

### Controller Tests (14+ tests)
- **UserControllerIntegrationTest**: MockMvc-based controller testing with security annotations
- JSON serialization/deserialization validation
- HTTP status code and error handling verification
- Role-based access control testing with `@WithMockUser`

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

## 🎉 Recent Enhancements (v0.3.0)

### Security & Testing Improvements
- ✅ **Enhanced JWT Utility** with comprehensive token management
  - Token generation, validation, and expiration handling
  - Role-based JWT tokens with custom claims
  - Secure signature verification and clock skew handling
- ✅ **Expanded Test Coverage** (70+ tests)
  - Comprehensive JWT security testing suite
  - Enhanced repository testing with edge cases
  - Role-based access control testing with Spring Security annotations
  - Authentication flow testing with proper mocking
- ✅ **Improved Security Testing**
  - `@WithMockUser` annotations for role-based testing
  - Security-aware integration tests
  - Comprehensive JWT validation scenarios
- ✅ **Production-Ready JWT Configuration**
  - Environment-based secret management
  - Configurable token expiration
  - Enhanced error handling for authentication failures

### Previous Enhancements (v0.2.0)
- ✅ **Upgraded to Java 17** (LTS) for better performance and security
- ✅ **Enhanced JWT Security** with environment-based secret management
- ✅ **Improved Security Headers** with HSTS and content type options
- ✅ **Secured Actuator Endpoints** with proper authentication requirements
- ✅ **Docker Security Hardening** with non-root user and health checks
- ✅ **Environment Variable Configuration** for production-ready deployments

## 🚀 Future Enhancements

Potential improvements for production use:
- Implement proper database integration (PostgreSQL/MySQL)
- Include API documentation with OpenAPI/Swagger
- Implement pagination for large datasets
- Add centralized logging and monitoring (ELK stack)
- Implement caching (Redis)
- Add rate limiting and API throttling
- Include metrics and observability (Micrometer/Prometheus)
- Implement database migrations (Flyway/Liquibase)
- Add comprehensive integration tests for security features

## License

This project is created for educational purposes.

---
**CI/CD Pipeline Status**: ✅ Active and configured for automated testing, code quality checks, and deployment.
