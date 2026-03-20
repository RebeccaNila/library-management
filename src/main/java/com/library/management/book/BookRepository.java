package com.library.management.book;

import org.springframework.data.jpa.repository.JpaRepository;

interface BookRepository extends JpaRepository<Book, Long> {
}
