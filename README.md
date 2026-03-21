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
* **Containerization & Orchestration:** Fully dockerized with a multi-stage build, `docker-compose` integration, and Kubernetes manifests for production deployment.

## 🛠️ Tech Stack

| Technology      | Version  | Purpose                        |
|-----------------|----------|--------------------------------|
| Java            | 17       | Programming language           |
| Spring Boot     | 3.x      | Application framework          |
| Spring Data JPA | 3.x      | Database ORM                   |
| MySQL           | 8.0      | Relational database            |
| Maven           | 3.x      | Dependency management          |
| Docker          | latest   | Containerization               |
| Docker Compose  | latest   | Multi-container orchestration  |
| Kubernetes      | v1.34.1  | Container orchestration        |
| JUnit 5         | 5.x      | Unit testing                   |
| Mockito         | latest   | Mocking framework              |
| JaCoCo          | latest   | Test coverage (89%)            |
| Swagger/OpenAPI | 3.x      | API documentation              |
| GitHub Actions  | latest   | CI/CD pipeline                 |

---

## Documentation

For deep-dive documentation on the architecture and design decisions of this project, please see the `docs/` folder:

* [**Assumptions & Design Decisions**](ASSUMPTIONS.md) — Detailed rationale for database choice, ISBN rules, and constraints.
* [**Architecture & Design**](docs/architecture.md) — Explanation of the layered architecture, design patterns (DTOs, Repository), and deployment flow.
* [**Database Design**](docs/database-design.md) — Entity Relationship diagrams and table definitions.
* [**12-Factor App Conformance**](docs/12-factor.md) — How this project aligns with modern scalable SaaS methodology.

---

## Project Structure

```
library-management/
├── .github/
│   └── workflows/
│       └── ci.yml
├── docs/
│   ├── 12-factor.md
│   ├── architecture.md
│   └── database-design.md
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
│   ├── docker/
│   │   ├── docker-compose.yml
│   │   └── Dockerfile
│   └── k8s/
│       ├── namespace.yml
│       ├── configmap.yml
│       ├── secret.template.yml
│       ├── mysql-pvc.yml
│       ├── mysql-deployment.yml
│       ├── mysql-service.yml
│       ├── api-deployment.yml
│       └── api-service.yml
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

#### Run with Kubernetes
- Docker Desktop with Kubernetes enabled
- `kubectl` CLI

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
**5. Access the API**
```
http://localhost:8080/api
```
---

### Option 2 — Run with Docker (Recommended for Dev)

**1. Create your `.env` file**
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

**2. Build and run**
```bash
docker compose -f deployment/docker/docker-compose.yml up --build -d
```
*Note: This utilizes a multi-stage `Dockerfile` to build the Java application dynamically. You do not need Java or Maven installed on your host machine to run this via Docker.*

**3. Access the API Documentation**
Navigate to the Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

**4. Stop the containers**
```bash
docker compose -f deployment/docker/docker-compose.yml down
```

---

### Option 3 — Run with Kubernetes (Production Ready)

Why Kubernetes?
Docker Compose is great for local development but Kubernetes provides:
- **Self healing** — automatically restarts crashed pods
- **Scalability** — easily scale replicas up or down
- **Declarative config** — define desired state in YAML files
- **Persistent storage** — MySQL data survives pod restarts via PersistentVolumeClaims

**1. Create `secret.yml` from template**
```bash
cp deployment/k8s/secret.template.yml deployment/k8s/secret.yml
```

**2. Generate base64 values for your secrets**
*Windows PowerShell:*
```powershell
[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("yourvalue"))
```
*Mac/Linux:*
```bash
echo -n "yourvalue" | base64
```

Fill in your values in `deployment/k8s/secret.yml`:
```yaml
data:
  MYSQL_ROOT_PASSWORD: <base64-root-password>
  MYSQL_USER: <base64-username>
  MYSQL_PASSWORD: <base64-password>
  DB_URL: <base64-db-url>
  MYSQL_DATABASE: <base64-db-name>
```

**3. Build Docker Image**
```bash
docker build -t library-management-api:latest -f deployment/docker/Dockerfile .
```

**4. Apply Kubernetes Files**
Apply in order (first time):
```bash
kubectl apply -f deployment/k8s/namespace.yml
kubectl apply -f deployment/k8s/configmap.yml
kubectl apply -f deployment/k8s/secret.yml
kubectl apply -f deployment/k8s/mysql-pvc.yml
kubectl apply -f deployment/k8s/mysql-deployment.yml
kubectl apply -f deployment/k8s/mysql-service.yml
kubectl apply -f deployment/k8s/api-deployment.yml
kubectl apply -f deployment/k8s/api-service.yml
```
*(For subsequent updates, you can just run: `kubectl apply -f deployment/k8s/`)*

**5. Verify Everything Running**
```bash
kubectl get all -n library-management
```
*Expected output should show pods for `mysql-deployment` and `library-api-deployment` as `Running`.*

**6. Access the API**
Because the API is exposed via a NodePort service, access it here:
```
http://localhost:30080/api
```
Swagger UI:
```
http://localhost:30080/swagger-ui/index.html
```

**7. Useful kubectl Commands**
```bash
# Check logs
kubectl logs -f deployment/library-api-deployment -n library-management

# Restart app only
kubectl rollout restart deployment/library-api-deployment -n library-management

# Delete everything including PVC (wipes DB data)
kubectl delete all --all -n library-management
kubectl delete pvc --all -n library-management
```

---

## API Documentation

Once the application is running locally or via Docker, you can access the Swagger UI:
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

*(If running via Kubernetes, use `http://localhost:30080/swagger-ui/index.html`)*

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

## Development Notes
This project was developed with AI assistance for guidance on best practices, debugging, and documentation. All architectural decisions, code reviews, and implementations were made by the developer.

---

### Author

**Rebecca Nila**
- GitHub: [@RebeccaNila](https://github.com/RebeccaNila)
