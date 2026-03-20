# Library Management System

A Spring Boot-based RESTful API for managing a library. This system handles books, borrowers, and the borrowing/returning process with built-in validation and error handling.

## Features

* **Book Management:** Add, update, delete, and view books in the library.
* **Borrower Management:** Register and manage library members.
* **Borrowing System:**
  * Borrow books (links a physical book to a borrower).
  * Return books.
  * Prevents borrowing a book that is already checked out.
* **API Documentation:** Integrated with OpenAPI (Swagger) for easy endpoint testing and exploration.
* **Robust Error Handling:** Global exception handling for missing resources, conflicts, and validation errors.

## Tech Stack

* **Java**
* **Spring Boot** (Web, Data JPA, Validation)
* **Springdoc OpenAPI / Swagger UI** for API documentation
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

## API Documentation

Once the application is running, you can access the Swagger UI to view and interact with the API endpoints.

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## API Endpoints Overview

### Books (`/api/books`)
* `GET /api/books` - Retrieve all books
* `GET /api/books/{id}` - Retrieve a specific book
* `POST /api/books` - Add a new book
* `PUT /api/books/{id}` - Update a book
* `DELETE /api/books/{id}` - Delete a book

### Borrowers (`/api/borrowers`)
* `GET /api/borrowers` - Retrieve all borrowers
* `GET /api/borrowers/{id}` - Retrieve a specific borrower
* `POST /api/borrowers` - Add a new borrower
* `PUT /api/borrowers/{id}` - Update a borrower
* `DELETE /api/borrowers/{id}` - Delete a borrower

### Borrowings (`/api/borrowings`)
* `POST /api/borrowings/borrow` - Borrow a book
* `POST /api/borrowings/return/{loanId}` - Return a book
