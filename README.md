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

## Tech Stack

* **Java 17**
* **Spring Boot 3.x** (Web, Data JPA, Validation)
* **Springdoc OpenAPI / Swagger UI** for API documentation
* **JUnit 5 & Mockito** for unit testing (`@WebMvcTest`, `@MockitoBean`, etc.)
* **Maven** for dependency management
* **Lombok** to reduce boilerplate code

## Project Structure

The project follows a feature-based package structure:

* `com.library.management.book`: Controllers, services, and repositories for managing books.
* `com.library.management.borrower`: Controllers, services, and repositories for managing borrowers.
* `com.library.management.borrowrecord`: Core business logic handling the borrowing and returning process, including `LoanStatus` tracking.
* `com.library.management.common`: Shared configurations (like OpenAPI) and global exception handling.

## Getting Started

### Prerequisites

* Java Development Kit (JDK) 17 or higher
* Maven (optional, as the project includes the Maven Wrapper)

### Running the Application

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd library-management
   ```

2. Build and run the application using the Maven wrapper:
   * **Linux/macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```
   * **Windows:**
     ```cmd
     mvnw.cmd spring-boot:run
     ```

3. The application will start on `http://localhost:8080` by default.

### Running Tests

To run the unit tests, use the following Maven command:
* **Linux/macOS:**
  ```bash
  ./mvnw test
  ```
* **Windows:**
  ```cmd
  mvnw.cmd test
  ```

This will execute the test suites covering:
* **Service Layer (`*ServiceImplTest.java`):** Business logic tests, mock database validations, and exception paths.
* **Controller Layer (`*ControllerTest.java`):** Web/HTTP layer tests using `@WebMvcTest` asserting request validation and status codes.

## API Documentation

Once the application is running, you can access the Swagger UI to view and interact with the API endpoints.

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## API Endpoints Overview

### Books (`/api/v1/books`)
* `GET /api/v1/books` - Retrieve all books
* `POST /api/v1/books` - Add a new book

### Borrowers (`/api/v1/borrowers`)
* `GET /api/v1/borrowers` - Retrieve all borrowers
* `POST /api/v1/borrowers` - Add a new borrower

### Loans (`/api/v1/loans`)
* `POST /api/v1/loans` - Borrow a book (Create a loan)
* `POST /api/v1/loans/{loanId}/return` - Return a borrowed book
