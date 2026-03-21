#  Database Design

This document describes the database schema, table relationships, and design decisions
for the Library Management System.

---

## Why MySQL?

| Factor | Decision |
|--------|----------|
| Data structure | Relational — books, borrowers and borrow records are naturally linked |
| Relationships | Foreign keys between tables ensure data integrity |
| Transactions | ACID compliance ensures borrow/return operations are reliable |
| Maturity | Well documented, widely supported, production proven |
| Spring Boot | Excellent support via Spring Data JPA and Hibernate |

---

## Entity Relationship Diagram

```
┌─────────────────────┐         ┌──────────────────────────┐         ┌─────────────────────┐
│       borrowers      │         │       borrow_records      │         │        books         │
├─────────────────────┤         ├──────────────────────────┤         ├─────────────────────┤
│ borrower_id (PK)    │◄────────│ borrower_id (FK)         │────────►│ book_id (PK)         │
│ name                │  1    * │ loan_id (PK)             │ *    1  │ isbn                 │
│ email               │         │ book_id (FK)             │         │ title                │
│ created_at          │         │ status                   │         │ author               │
└─────────────────────┘         │ borrowed_at              │         │ created_at           │
                                 │ returned_at              │         └─────────────────────┘
                                 │ message                  │
                                 └──────────────────────────┘
```

---

## Table Definitions

### `borrowers` Table

Stores library member information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `borrower_id` | VARCHAR(36) | PK, NOT NULL | UUID unique identifier |
| `name` | VARCHAR(255) | NOT NULL | Full name of borrower |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | Email address — must be unique |
| `created_at` | DATETIME | NOT NULL | Auto set on record creation |

---

### `books` Table

Stores individual book copies. Multiple rows can share the same ISBN (multiple copies).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `book_id` | VARCHAR(36) | PK, NOT NULL | UUID unique identifier per copy |
| `isbn` | VARCHAR(255) | NOT NULL | ISBN number — not unique (multiple copies allowed) |
| `title` | VARCHAR(255) | NOT NULL | Book title |
| `author` | VARCHAR(255) | NOT NULL | Book author |
| `created_at` | DATETIME | NOT NULL | Auto set on record creation |

**Important:** ISBN is NOT a unique constraint. Two books with the same ISBN are
treated as separate physical copies with different `book_id` values.

---

### `borrow_records` Table

Tracks borrowing history — links borrowers to books.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `loan_id` | VARCHAR(36) | PK, NOT NULL | UUID unique identifier |
| `borrower_id` | VARCHAR(36) | FK → borrowers, NOT NULL | Who borrowed the book |
| `book_id` | VARCHAR(36) | FK → books, NOT NULL | Which book was borrowed |
| `status` | ENUM | NOT NULL | BORROWED or RETURNED |
| `borrowed_at` | DATETIME | NOT NULL | Auto set when borrowed |
| `returned_at` | DATETIME | NULL | Set when book is returned |
| `message` | VARCHAR(255) | NULL | Optional notes |

---

## Relationships

### Borrower → BorrowRecord (One to Many)
```
One borrower can have many borrow records
→ A member can borrow multiple books over time
→ borrower_id is foreign key in borrow_records
```

### Book → BorrowRecord (One to Many)
```
One book can have many borrow records
→ A book can be borrowed and returned multiple times
→ book_id is foreign key in borrow_records
```

---

## UUID as Primary Key

All tables use UUID (VARCHAR 36) instead of auto-increment integers.

| Reason | Explanation |
|--------|-------------|
| Security | IDs are not guessable or sequential |
| Distributed | Safe for distributed systems — no collision |
| Readability | Stored as VARCHAR for easy debugging |
| Spring Boot | `@GeneratedValue(strategy = GenerationType.UUID)` |

---

## ISBN Design Decision

```
Requirement: Multiple copies of same ISBN are allowed

Design:
→ ISBN is just a metadata field on the books table
→ NOT a unique constraint
→ Each physical copy gets its own UUID book_id

Example:
book_id: uuid-001, isbn: 9780132350884, title: Clean Code ← copy 1
book_id: uuid-002, isbn: 9780132350884, title: Clean Code ← copy 2

Both are separate borrowable items ✅
```

---

## Borrow Business Rule

```
Requirement: Only one borrower per book at a time

Implementation:
→ Before creating BorrowRecord check if active BORROWED record exists
   for that book_id
→ If exists → return 409 Conflict
→ If not    → create new BorrowRecord with status BORROWED

SELECT * FROM borrow_records
WHERE book_id = ? AND status = 'BORROWED'
→ If found → book is not available
→ If empty → book is available
```

---

## LoanStatus Enum

```java
public enum LoanStatus {
    BORROWED,   // book is currently borrowed
    RETURNED    // book has been returned
}
```

| Status | Meaning | returned_at |
|--------|---------|-------------|
| `BORROWED` | Currently checked out | NULL |
| `RETURNED` | Returned to library | Set to return datetime |

---

## Sample Data

### Borrowers
```sql
INSERT INTO borrowers (borrower_id, name, email, created_at) VALUES
('8f2e91a4-5b3c-4d2a-9e1f-6a7b8c9d0e1f', 'Alice Johnson', 'alice.johnson@example.com', NOW()),
('3d7a2b5c-9e1f-4a8b-bc2d-1e0f9a8b7c6d', 'Bob Smith', 'bob.smith@example.com', NOW());
```

### Books (Same ISBN — Different Copies)
```sql
INSERT INTO books (book_id, isbn, title, author, created_at) VALUES
('a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6', '9780132350884', 'Clean Code', 'Robert C. Martin', NOW()),
('f9e8d7c6-b5a4-4321-9087-654321abcdef', '9780132350884', 'Clean Code', 'Robert C. Martin', NOW());
-- Same ISBN → two separate borrowable copies ✅
```

### Borrow Records
```sql
INSERT INTO borrow_records (loan_id, borrower_id, book_id, status, borrowed_at) VALUES
('7c8d9e0f-1a2b-43c4-d5e6-f7a8b9c0d1e2', 'a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6', '8f2e91a4-5b3c-4d2a-9e1f-6a7b8c9d0e1f', 'BORROWED', NOW()),
-- book '3d7a2b5c-...' is still available to borrow ✅
```
