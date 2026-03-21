# Library Management System

A Spring Boot-based RESTful API for managing a library. This system handles books, borrowers, and the borrowing/returning process with built-in validation and error handling.

## Features

* **Book Management:** Add and view books in the library.
* **Borrower Management:** Register and view library members.
* **Borrowing System:**
  * Borrow books (links a physical book to a borrower).
  * Return books.
  * Prevents borrowing a book that is already checked out.
* **API Documentation:** Integrated with OpenAPI (Swagger) for easy endpoint testing and exploration.
* **Robust Error Handling:** Global exception handling for missing resources, conflicts, and validation errors.
* **Unit Testing:** Comprehensive test coverage for service and controller layers using JUnit 5 and Mockito.
* **Containerization:** Fully dockerized with a multi-stage build and `docker-compose` integration.

## Tech Stack

* **Java 17**
* **Spring Boot 3.x** (Web, Data JPA, Validation)
* **MySQL 8.0** for relational data persistence
* **Docker & Docker Compose** for containerization
* **Springdoc OpenAPI / Swagger UI** for API documentation
* **JUnit 5 & Mockito** for unit testing (`@WebMvcTest`, `@MockitoBean`, etc.)
* **Maven** for dependency management

---

## Project Structure

```
library-management/
├── .github/
│   └── workflows/
│       └── ci.yml
├── src/
│   ├── main/
│   │   ├── java/com/library/management/
│   │   │   ├── book/
│   │   │   ├── borrower/
│   │   │   ├── borrowrecord/
│   │   │   └── common/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
├── deployment/
│   └── docker/
│       ├── docker-compose.yml
│       └── Dockerfile
├── .env.template
├── .gitignore
├── ASSUMPTIONS.md
└── pom.xml
```

---

## Prerequisites

#### Run Locally
- Java 17
- Maven 3.x
- MySQL 8.0

#### Run with Docker
- Docker
- Docker Compose

---

## Getting Started

### Option 1 — Run Locally

**1. Clone the repository**
```bash
git clone https://github.com/RebeccaNila/library-management.git
cd library-management
```

**2. Create the database**
```sql
CREATE DATABASE library_db;
```

**3. Configure dev properties**
Ensure your local MySQL credentials match the `application-dev.properties`.

**4. Build and run the application**
* **Linux/macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```
* **Windows:**
  ```cmd
  mvnw.cmd spring-boot:run
  ```

### Option 2 — Run with Docker (Recommended)

**1. Clone the repository**
```bash
git clone https://github.com/RebeccaNila/library-management.git
cd library-management
```

**2. Create your `.env` file**
```bash
cp .env.template .env
```

Edit `.env` with your desired secure values:
```env
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
MYSQL_DATABASE=library_db
MYSQL_ROOT_PASSWORD=yourpassword
MYSQL_USERNAME=library_user
MYSQL_PASSWORD=yourpassword
```

**3. Build and run**
```bash
docker compose -f deployment/docker/docker-compose.yml up --build
```
*Note: This utilizes a multi-stage `Dockerfile` to build the Java application dynamically. You do not need Java or Maven installed on your host machine to run this via Docker. The Docker Compose file automatically spins up a MySQL database and links it to the API.*

**4. Access the API Documentation**
Navigate to the Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

**5. Stop the containers**
```bash
docker compose -f deployment/docker/docker-compose.yml down
```

---

## API Documentation

Once the application is running, you can access the Swagger UI to view and interact with the API endpoints.

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

##  API Endpoints

### Books
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/books` | Register a new book |
| `GET` | `/api/v1/books` | Get all books |

**Register Book Request:**
```json
{
  "isbn": "978-3-16-148410-0",
  "title": "Clean Code",
  "author": "Robert C. Martin"
}
```

**Register Book Response:**
```json
{
  "id": "uuid",
  "isbn": "978-3-16-148410-0",
  "title": "Clean Code",
  "author": "Robert C. Martin"
}
```

### Borrowers
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/borrowers` | Register a new borrower |
| `GET` | `/api/v1/borrowers` | Get all borrowers |

**Register Borrower Request:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

**Register Borrower Response:**
```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

### Loans (Borrowing)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/loans` | Borrow a book |
| `POST` | `/api/v1/loans/{id}/return` | Return a borrowed book |

**Borrow Book Request:**
```json
{
  "borrowerId": "uuid",
  "bookId": "uuid"
}
```

**Borrow Book Response:**
```json
{
  "loanId": "uuid",
  "borrowerId": "uuid",
  "bookId": "uuid",
  "status": "BORROWED",
  "message": "Book borrowed successfully.",
  "timestamp": "2026-03-21T10:00:00"
}
```

---

## Error Handling

A global exception handler returns consistent structured error responses.

| Status | Meaning |
|--------|---------|
| `400` | Validation failed |
| `404` | Resource not found |
| `409` | Conflict — duplicate or business rule violation |
| `500` | Unexpected server error |

**Error Response Format:**
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

## Testing & CI/CD

### Running Tests Locally

```bash
# Run all tests
./mvnw test

# Run tests with coverage report
./mvnw verify

# View coverage report (if JaCoCo is configured)
open target/site/jacoco/index.html
```
**Current Coverage: 89%**

### CI/CD Pipeline

GitHub Actions automatically runs on every push to `master`:

```
Push to master
      ↓
Checkout code
      ↓
Setup Java 17
      ↓
Run unit tests
      ↓
Build application
```

---

## Assumptions & Design Decisions

See [ASSUMPTIONS.md](ASSUMPTIONS.md) for detailed documentation of all assumptions and design decisions made during development (e.g., Database choice, ISBN Multi-copy logic, and Borrowing Constraints).

---

### Author

**Rebecca Nila**
- GitHub: [@RebeccaNila](https://github.com/RebeccaNila)