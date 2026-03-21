# Library Management System - Technical Assessment Documentation

## 1. Project Overview
The Library Management System is a robust RESTful API built to manage library operations, specifically handling books, borrowers, and the borrowing/returning process. The project adheres to modern architectural patterns and best practices.

### Tech Stack Choices
* **Language:** Java 17 (Requirement 1a)
* **Framework:** Spring Boot 3.x (Requirement 1a)
* **Dependency Manager:** Maven (Requirement 3)
* **Database:** MySQL
* **API Documentation:** Springdoc OpenAPI (Swagger)

## 2. Requirements Mapping
* **Actions Implemented:** Register borrower, Register book, List all books, Borrow book, Return book.
* **Environment Configurability:** Handled using Spring profiles (`application.properties` and `application-dev.properties`), easily overridden via Environment Variables. (Requirement 2)
* **Data Validation & Error Handling:** Implemented Jakarta Validation (`@Valid`, `@NotBlank`) and a `@ControllerAdvice` Global Exception Handler mapped to standard HTTP status codes (`400 Bad Request`, `404 Not Found`, `409 Conflict`). (Requirement 4)
* **REST API:** Designed using resource-oriented noun paths (`/api/v1/books`, `/api/v1/borrowers`, `/api/v1/loans`). (Requirement 6)
* **ISBN Multi-copy Logic:** The system uses unique `UUID` primary keys for physical books, allowing multiple rows with the exact same ISBN. (Requirement 7)
* **Borrowing Constraint:** Implemented a repository check `existsByBookIdAndReturnedAtIsNull` to guarantee a physical book ID cannot be double-borrowed. (Requirement 8)

## 3. Database Choice & Justification (Requirement 5a)
**Database Chosen: MySQL (Relational / SQL)**

**Justification:**
1. **ACID Compliance:** The borrowing process is highly transactional. When a book is borrowed or returned, we need strict guarantees that the data state is consistent.
2. **Relational Data:** The domain models heavily rely on relationships (A `BorrowRecord` maps strictly to one `Book` and one `Borrower`). SQL Foreign Key constraints ensure referential integrity, preventing "ghost" loans.
3. **NoSQL Mismatch:** While a NoSQL database (like MongoDB) is great for unstructured data, a library system has a rigid, highly structured schema that benefits from strict table definitions and predictable queries.

## 4. Assumptions (Requirement 10)
During development, the following business logic assumptions were made as they were not explicitly defined in the task:
1. **Physical Book Identification:** A single `Book` entity represents a *physical copy* of a book. To support multiple copies of the same ISBN, a globally unique `UUID` is assigned to every book registered. 
2. **Borrower Limitations:** There is no hard limit on how many different books a single borrower can have checked out at the same time.
3. **Historical Records:** Returning a book does not delete the loan record. Instead, it updates the `status` to `RETURNED` and populates the `returnedAt` timestamp, acting as an audit log for library analytics.
4. **Author/Title Format:** Titles and Authors are treated as case-insensitive when validating the rule: *"2 books with the same ISBN numbers must have the same title and same author."*

## 5. Twelve-Factor App Conformance (Nice to have #4)
The application architecture adheres to several principles of the 12-Factor methodology:
* **I. Codebase:** Tracked in a single Git repository.
* **II. Dependencies:** Explicitly declared and isolated using Maven (`pom.xml` and `mvnw`).
* **III. Config:** Environment-specific configurations are abstracted from the code. Production credentials can be injected dynamically via Environment Variables (e.g., `SPRING_DATASOURCE_URL`).
* **VI. Processes:** The application executes as a stateless Spring Boot process.
* **VII. Port binding:** The application is entirely self-contained, utilizing an embedded Apache Tomcat server bound to port `8080`.
* **IX. Disposability:** The app can be started or stopped rapidly and gracefully due to Spring Boot's lifecycle management.

## 6. API Documentation & Usage (Requirement 9)
Live API documentation and testing capabilities are bundled directly into the application via Swagger UI.

**To run the application:**
1. Ensure MySQL is running and configured as per `application-dev.properties`.
2. Execute the Maven wrapper: `./mvnw spring-boot:run` (Linux/Mac) or `mvnw.cmd spring-boot:run` (Windows).

**To view the API Documentation:**
Navigate to: `http://localhost:8080/swagger-ui.html`

### Key Endpoints
* **Books**
  * `POST /api/v1/books` - Register a physical book copy
  * `GET /api/v1/books` - Retrieve the catalog
* **Borrowers**
  * `POST /api/v1/borrowers` - Register a member
* **Loans**
  * `POST /api/v1/loans` - Borrow a book (requires `bookId` and `borrowerId`)
  * `POST /api/v1/loans/{loanId}/return` - Return a book

## 7. Testing Strategy (Nice to have #1)
The application includes extensive unit test coverage for both the core business logic (Service Layer) and HTTP interactions (Web Layer).
* **Mocking:** Mockito is utilized to isolate components from the database.
* **Execution:** Tests can be run via `./mvnw test`.
