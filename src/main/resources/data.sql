-- Borrowers
INSERT INTO borrowers (borrower_id, NAME, email, created_at)
VALUES ('d2e3f4a5-b6c7-48d9-e0f1-a2b3c4d5e6f7', 'Peter', 'peter@example.com', NOW(6));

INSERT INTO borrowers (borrower_id, NAME, email, created_at)
VALUES ('6a7b8c9d-0e1f-42a3-b4c5-d6e7f8a9b0c1', 'John', 'john@example.com', NOW(6));

-- Books
INSERT INTO books (book_id, isbn, title, author, created_at)
VALUES ('c1d2e3f4-a5b6-47c8-d9e0-f1a2b3c4d5e6', '978-3-16-148410-5', 'A Different Kind of Power', 'Jacinda Ardern', NOW(6));

INSERT INTO books (book_id, isbn, title, author, created_at)
VALUES ('5f6a7b8c-9d0e-41f2-a3b4-c5d6e7f8a9b0', '978-3-16-148410-6', 'AI on Trial', 'Shri Sujeet Kumar', NOW(6));