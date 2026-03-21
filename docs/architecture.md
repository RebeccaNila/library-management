# Architecture & Design

This document describes the architecture, design patterns, and technical decisions
made in the Library Management System.

---

## System Architecture

```
Client (Postman / Browser)
          ↓
    REST API Layer
    (Spring Boot)
          ↓
   Service Layer
   (Business Logic)
          ↓
  Repository Layer
  (Spring Data JPA)
          ↓
    MySQL Database
```

---

## Package Structure — Package by Feature

The project follows package by feature instead of package by layer.

```
com.library.management/
├── book/                    → everything related to books
│   ├── Book.java            → entity
│   ├── BookController.java  → REST endpoints
│   ├── BookRepository.java  → data access
│   ├── BookRequest.java     → input DTO
│   ├── BookResponse.java    → output DTO
│   ├── BookService.java     → service interface
│   └── BookServiceImpl.java → business logic
├── borrower/                → everything related to borrowers
├── borrowrecord/            → everything related to borrowing
└── common/                  → shared code
    ├── config/              → OpenAPI config
    └── exception/           → global exception handling
```

### Why Package by Feature?

| Package by Layer | Package by Feature |
|-----------------|-------------------|
| Split by technical role | Split by business domain |
| Hard to find related files | All related files together |
| Hard to scale | Easy to add new features |
| Common in old projects | Modern best practice |

---

## Layer Responsibilities

### Controller Layer
- Receives HTTP requests
- Validates input with @Valid
- Delegates to Service layer
- Returns HTTP response with correct status code

### Service Layer
- Applies business rules
- Handles borrow/return logic
- Checks for duplicates and conflicts
- Throws appropriate exceptions

### Repository Layer
- Database queries via Spring Data JPA
- Custom JPQL queries where needed
- No business logic here

---

## Design Patterns Used

### 1. DTO Pattern (Data Transfer Object)
```
Client sends BookRequest (input DTO)
        ↓
Service maps to Book entity
        ↓
Repository saves Book entity
        ↓
Service maps to BookResponse (output DTO)
        ↓
Client receives BookResponse
```

Why:
- Never expose entity directly to client
- Control exactly what data comes in and out
- Protect internal data model

### 2. Repository Pattern
- Service never writes SQL directly
- Calls Repository interface methods
- Spring Data JPA generates SQL automatically
- Easy to test by mocking the repository

### 3. Global Exception Handler
```
Any exception thrown in any layer
        ↓
Caught by @RestControllerAdvice
        ↓
Mapped to consistent error response
        ↓
Client always gets same error format
```

Exceptions handled:
- NotFoundException → 404
- ConflictException → 409
- MethodArgumentNotValidException → 400
- Exception → 500

---

## Deployment Architecture

### Local Development
```
Developer Machine
├── Spring Boot App (port 8080)
└── MySQL (port 3306)
```

### Docker Compose
```
Docker Network (library-network)
├── library-api container (port 8080)
│   └── connects to library-db via container name
└── library-db container (port 3306)
    └── data persisted in ./data/mysql_data
```

### Kubernetes
```
Namespace: library-management
├── library-api-deployment (pod port 8080)
│   ├── config from library-config ConfigMap
│   └── secrets from library-secret Secret
├── mysql-deployment (pod port 3306)
│   └── data persisted in mysql-pvc
├── library-api-service (NodePort 30080)
└── mysql-service (ClusterIP internal only)
```

---

## Error Handling Strategy

All errors return consistent format:
```json
{
  "timestamp": "2026-03-21T10:00:00",
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "field": "error message"
  }
}
```

---

## Security Considerations

| Area | Decision |
|------|----------|
| Authentication | Not implemented — outside scope |
| Passwords | Never stored in codebase — env vars only |
| UUIDs | Non-sequential IDs — not guessable |
| Prod secrets | K8s Secrets + .env file — never in Git |
| Docker | Non-root user in container |
| SQL Injection | Prevented by JPA parameterized queries |

---

## Testing Strategy

```
Unit Tests (JUnit 5 + Mockito)
├── Service layer
│   ├── Happy path — success scenarios
│   └── Unhappy path — exception scenarios
└── Controller layer
    ├── HTTP status codes
    ├── Response format
    └── Validation behavior

Coverage: 89% (JaCoCo)
```

---

## CI/CD Pipeline

```
Push to master
      ↓
GitHub Actions triggered
      ↓
Ubuntu runner spins up
      ↓
MySQL container started
      ↓
Java 17 installed
      ↓
Unit tests run
      ↓
JAR built
      ↓
Pipeline passes or fails
```
