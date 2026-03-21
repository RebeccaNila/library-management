# Assumptions & Design Decisions

This document outlines the assumptions and design decisions made during the development
of the Library Management System where requirements were not explicitly stated.

---

## 1. Database Choice — MySQL

**Decision:** MySQL 8.0 was chosen as the database.

**Reasoning:**
- Book, Borrower and BorrowRecord data is naturally relational
- BorrowRecord links Borrowers to Books via foreign keys — relational DB fits perfectly
- MySQL is widely used, well documented and production ready
- Strong support with Spring Data JPA and Hibernate
- Easy to run locally and in containerized environments via Docker

**Alternatives considered:**
- PostgreSQL — also a strong choice but MySQL was preferred for familiarity
- MongoDB — rejected because the data model is relational, not document-based
- H2 — used only for testing considerations, not suitable for production

---

## 2. Book Identity & ISBN Logic

**Decision:** Multiple books can share the same ISBN but each gets a unique UUID.

**Reasoning:**
- Requirement 7 explicitly states same ISBN should result in different book IDs
- ISBN is treated as a metadata field (title, author reference) not a unique constraint
- Each physical book copy is represented as its own entity with a unique UUID
- This allows a library to own multiple copies of the same book

**Example:**
```
Book 1: id=uuid-001, isbn=978-3-16, title="Clean Code"
Book 2: id=uuid-002, isbn=978-3-16, title="Clean Code"
→ Two separate copies of the same book
```

---

## 3. Borrow Business Rules

**Decision:** A book can only be borrowed by one member at a time, tracked via BorrowRecord status.

**Reasoning:**
- Requirement 8 states no more than one member can borrow the same book (same book ID) at a time
- BorrowRecord uses `LoanStatus` enum with values `BORROWED` and `RETURNED`
- Before creating a new BorrowRecord, the system checks if an active `BORROWED` record exists for that book ID
- If a `BORROWED` record exists → 409 Conflict is returned
- A book becomes available again only when its status is updated to `RETURNED`

**Borrow flow:**
```
Borrower requests book
        ↓
System checks for active BORROWED record
        ↓
If exists → reject with 409 Conflict
If not    → create BorrowRecord with status BORROWED
        ↓
On return → update status to RETURNED
```

---

## 4. API Design Decisions & Future Scalability

**Decision:** Standard REST conventions were followed throughout, but kept simple to match the "simple library system" requirement.

**Assumptions made:**
- UUIDs are used for all entity IDs instead of auto-increment integers
  - More secure — IDs are not guessable or sequential
  - Better suited for distributed systems
- Timestamps (`createdAt`, `borrowedAt`, `returnedAt`) are auto-generated server-side
  - Clients do not need to provide timestamps
- All responses follow a consistent JSON structure
- **Pagination:** Pagination was purposefully *not* implemented for the `GET /api/v1/books` and `GET /api/v1/borrowers` endpoints. Because the scope requested a "simple library system", returning a flat JSON array is sufficient. For a future production iteration handling thousands of records, these endpoints would be upgraded to use Spring Data `Pageable` and return a standard `PagedResult<T>` structure.

**Endpoints summary:**
```
POST   /api/v1/books              → Register a new book
GET    /api/v1/books              → Get all books
POST   /api/v1/borrowers          → Register a new borrower
GET    /api/v1/borrowers          → Get all borrowers
POST   /api/v1/loans              → Borrow a book
POST   /api/v1/loans/{id}/return  → Return a book
```

---

## 5. Validation Rules

**Decision:** Input validation is enforced at the API layer using Jakarta Bean Validation.

**Rules applied:**

| Field | Rule |
|---|---|
| Book ISBN | Not null, not empty |
| Book title | Not null, not empty |
| Book author | Not null, not empty |
| Borrower name | Not null, not empty |
| Borrower email | Not null, valid email format |
| Borrow request | Both borrower ID and book ID required |

**Assumption:** Email uniqueness is enforced — a borrower cannot register twice with the same email address.

---

## 6. Error Handling

**Decision:** A global exception handler returns consistent structured error responses.

**Error response format:**
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

**HTTP status codes used:**

| Status | When |
|---|---|
| 200 OK | Successful GET / Successful Return |
| 201 Created | Successful POST |
| 400 Bad Request | Validation failure |
| 404 Not Found | Resource does not exist |
| 409 Conflict | Duplicate resource or business rule violation |
| 500 Internal Server Error | Unexpected server error |

---

## 7. Security Assumptions

**Decision:** Authentication and authorization were not implemented in this version.

**Reasoning:**
- The requirements did not explicitly require authentication
- The focus was on core library management functionality
- For a production system, JWT-based authentication would be recommended as a next step

---

## 8. Environment Configuration

**Decision:** Two Spring profiles were created — `dev` and `prod`.

| Profile | Purpose |
|---|---|
| `dev` | Local development with local MySQL |
| `prod` | Production environment using environment variables for secrets |

- Production credentials are never stored in the codebase
- CI pipeline executes successfully using standard runners

---

## 9. UUID Storage

**Decision:** UUIDs are stored as `VARCHAR(36)` in the database.

**Reasoning:**
- `@JdbcTypeCode(java.sql.Types.VARCHAR)` annotation used for explicit VARCHAR mapping
- More readable and debuggable in the database compared to binary storage
- Minor storage overhead is acceptable for this project scope

---

*This document will be updated as the project evolves.*